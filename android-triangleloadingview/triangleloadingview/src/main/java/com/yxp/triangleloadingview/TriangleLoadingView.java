package com.yxp.triangleloadingview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by yxp on 2015/11/7.
 */
public class TriangleLoadingView extends View {
    public final static int DISTANCE = 4;
    public final static int TRIANGLE_NUM = 6;
    public final static float SIN60 = 0.866f;
    public final static int WIDTH_DEFAULT = 160;
    public final static int HEIGHT_DEFAULT = 250;
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;
    public final static int STATE_UP = 3;
    public final static int TIME_WAIT_UP = 400;
    public final static int TIME_GAP_UP = 10;
    public final static int TIME_SWITCH_COLOR = 300;
    public final static int STATUS_NORMAL = 1;
    public final static int STATUS_DYNAMIC = 2;
    public static int LENGTH = 40;
    private int status = STATUS_DYNAMIC;
    private int state = STATE_NORMAL;
    private Context context;
    private Paint paint;
    private Triangle[] trianglesLists;
    private int[][] topPoints;
    private int startX, startY;
    private int colorIndex = 0;
    private int curDes = 0;
    private int path = 0;
    private boolean hasInit = false;
    private boolean isAllocateFall = false;
    private OnStateListener onStateListener;

    private int[][] colorLists = new int[][]{{Color.RED, Color.parseColor("#FF7256")},
            {Color.parseColor("#FF7F00"), Color.parseColor("#FFC125")},
            {Color.parseColor("#FFD700"), Color.parseColor("#FFFACD")},
            {Color.parseColor("#00CD00"), Color.parseColor("#C1FFC1")},
            {Color.parseColor("#1C86EE"), Color.parseColor("#AB82FF")},
            {Color.parseColor("#BA55D3"), Color.parseColor("#EEAEEE")}};
    private int[] degree = new int[]{-30, -90, -150, 150, 90, 30};

    public TriangleLoadingView(Context context) {
        super(context);
        init(context, null);
    }

    public TriangleLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TriangleLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TriangleLoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        this.context = context;
        // get the setting from xml
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TriangleLoadingViewAttr, 0, 0);
            status = typedArray.getInt(R.styleable.TriangleLoadingViewAttr_state, STATUS_DYNAMIC);
            typedArray.recycle();
        }

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        // init after the width and height are measured
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!hasInit) {
                    LENGTH = Math.min(getHeight(), getWidth()) / 4;
//                    LENGTH = getWidth() / 4;
                    trianglesLists = new Triangle[TRIANGLE_NUM];
                    startX = getWidth() / 2 - DISTANCE;
                    startY = 4 * LENGTH;
                    initTopPoints(startX, startY);
                    if (status == STATUS_DYNAMIC) {
                        initFall();
                    }
                    for (int i = 0; i < TRIANGLE_NUM; i++) {
                        trianglesLists[i] = new Triangle(topPoints[i][0], topPoints[i][1], degree[i], colorLists[i][0], colorLists[i][1]);
                        if (status == STATUS_NORMAL) trianglesLists[i].setDev(-50);
                    }
                    hasInit = true;
                    invalidate();
                    if (status == STATUS_NORMAL) startRefresh();
                }
            }
        });
        path = Triangle.SHIFT;
    }

    // by this way can set the default width and height of the view
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureDimension(WIDTH_DEFAULT, widthMeasureSpec);
        int height = measureDimension(HEIGHT_DEFAULT, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    public int measureDimension(int defaultSize, int measureSpec) {
        int result;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize;   //UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.reset();
        paint.setAlpha(256);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        paint.reset();
        drawTriangle(canvas);
    }

    /**
     * init six vertex of hexagon
     * @param x
     * @param y
     */
    protected void initTopPoints(int x, int y) {
        topPoints = new int[TRIANGLE_NUM][2];
        topPoints[0][0] = x - DISTANCE / 2;
        topPoints[0][1] = y;
        topPoints[1][0] = (int) (topPoints[0][0] - LENGTH * SIN60) - DISTANCE;
        topPoints[1][1] = topPoints[0][1] + LENGTH / 2 + DISTANCE;
        topPoints[2][0] = topPoints[1][0] + DISTANCE;
        topPoints[2][1] = topPoints[1][1] + LENGTH + DISTANCE;
        topPoints[3][0] = topPoints[0][0] + DISTANCE + DISTANCE / 2;
        topPoints[3][1] = topPoints[0][1] + LENGTH * 2 + DISTANCE * 2;
        topPoints[4][0] = (int) (topPoints[2][0] + 2 * LENGTH * SIN60) + 2 * DISTANCE;
        topPoints[4][1] = topPoints[2][1] - DISTANCE;
        topPoints[5][0] = (int) (topPoints[1][0] + 2 * LENGTH * SIN60) + 2 * DISTANCE + DISTANCE / 2;
        topPoints[5][1] = topPoints[1][1] - DISTANCE;
    }

    public void drawTriangle(Canvas canvas) {
        for (int i = 0; i < TRIANGLE_NUM; i++) {
            trianglesLists[i].drawSelf(canvas, paint);
        }
    }

    /**
     * change color
     */
    public void switchTriangleColor() {
        for (int i = TRIANGLE_NUM - 1; i >= 0; i--) {
            colorIndex = colorIndex % TRIANGLE_NUM;
            trianglesLists[i].setColor(colorLists[colorIndex][0],
                    colorLists[colorIndex][1]);
            colorIndex++;
        }
        colorIndex++;
        postInvalidate();
    }

    /**
     * circularly change color
     */
    public void startRefresh() {
        if (state == STATE_REFRESHING) return;
        if (onStateListener != null) onStateListener.onRefreshing();
        setState(STATE_REFRESHING);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (state == STATE_REFRESHING) {
                    try {
                        Thread.sleep(TIME_SWITCH_COLOR);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    switchTriangleColor();
                }
            }
        }).start();
    }

    /**
     * if need fall state, change the vertex
     */
    protected void initFall() {
        topPoints[0][1] -= 4 * LENGTH;
        topPoints[1][1] -= 2 * LENGTH;
        topPoints[2][1] -= 1 * LENGTH;
        topPoints[3][1] -= 0 * LENGTH;
        topPoints[4][1] -= 2 * LENGTH;
        topPoints[5][1] -= 3 * LENGTH;
        isAllocateFall = true;
    }

    /**
     * fall through the length between the last point and current point
     * @param delta
     */
    public void fallByDelta(int delta) {
        if (state == STATE_REFRESHING || state == STATE_UP || status == STATUS_NORMAL) return;
        if (trianglesLists == null) return;
        if (onStateListener != null) {
            onStateListener.onFall(delta);
        }
        path += delta;
        for (int i = 0; i < TRIANGLE_NUM; i++) {
            if (i == 0 && path > 4 * LENGTH) {
                trianglesLists[i].moveVertically(4 * LENGTH);
                if (state == STATE_NORMAL) {
                    setState(STATE_READY);
                }
                return;
            }
            if (i == 1 && path > 2 * LENGTH) {
                trianglesLists[i].moveVertically(2 * LENGTH);
                continue;
            }
            if (i == 2 && path > 1 * LENGTH) {
                trianglesLists[i].moveVertically(1 * LENGTH);
                continue;
            }
            if (i == 3 && path > 0){
                trianglesLists[i].moveVertically(0);
                continue;
            }
            if (i == 4 && path > 2 * LENGTH) {
                trianglesLists[i].moveVertically(2 * LENGTH);
                continue;
            }
            if (i == 5 && path > 3 * LENGTH) {
                trianglesLists[i].moveVertically(3 * LENGTH);
                continue;
            }
            trianglesLists[i].moveVertically(path);
        }
        postInvalidate();
        curDes = path;
    }

    /**
     * fall through the length between the first point and current point
     * @param length
     */
    public void fallByLength(int length) {
        if (trianglesLists == null || status == STATUS_NORMAL) return;
        if (onStateListener != null) {
            onStateListener.onFall(length);
        }
        for (int i = 0; i < TRIANGLE_NUM; i++) {
            if (i == 0 && length > 4 * LENGTH) {
                trianglesLists[i].moveVertically(4 * LENGTH);
                if (state == STATE_NORMAL) {
                    setState(STATE_READY);
                }
                return;
            }
            if (i == 1 && length > 2 * LENGTH) {
                trianglesLists[i].moveVertically(2 * LENGTH);
                continue;
            }
            if (i == 2 && length > 1 * LENGTH) {
                trianglesLists[i].moveVertically(1 * LENGTH);
                continue;
            }
            if (i == 3 && length > 0) {
                trianglesLists[i].moveVertically(0);
                continue;
            }
            if (i == 4 && length > 2 * LENGTH) {
                trianglesLists[i].moveVertically(2 * LENGTH);
                continue;
            }
            if (i == 5 && length > 3 * LENGTH) {
                trianglesLists[i].moveVertically(3 * LENGTH);
                continue;
            }
            trianglesLists[i].moveVertically(length);
        }
        postInvalidate();
        curDes = length;
    }

    /**
     * start rise animation
     */
    public void startRiseAnim() {
        if(state == STATE_UP || status == STATUS_NORMAL) return;
        setState(STATE_UP);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(TIME_WAIT_UP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (state == STATE_UP) {
                    try {
                        Thread.sleep(TIME_GAP_UP);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (isAllocateFall) {
                        riseWhileFall();
                    } else {
                        rise();
                    }
                }
            }
        }).start();
    }

    protected void riseWhileFall() {
        curDes -= 3;
        for (int i = 0; i < TRIANGLE_NUM; i++) {
            if (i == 0 && curDes < Triangle.SHIFT) {
                setState(STATE_NORMAL);
                curDes = Triangle.SHIFT;
                break;
            }
            if (i == 1 && curDes > 2 * LENGTH) continue;
            if (i == 2 && curDes > 1 * LENGTH) continue;
            if (i == 3 && curDes > 0 * LENGTH) continue;
            if (i == 4 && curDes > 2 * LENGTH) continue;
            if (i == 5 && curDes > 3 * LENGTH) continue;
            trianglesLists[i].moveVertically(curDes);
        }
        postInvalidate();
    }

    protected void rise() {
        for (int i = 0; i < TRIANGLE_NUM; i++) {
            if (i == 0 && trianglesLists[0].getDev() < Triangle.SHIFT) {
                setState(STATE_NORMAL);
                break;
            }
            if (i == 1 && trianglesLists[0].getDev() > -2 * LENGTH) continue;
            if (i == 2 && trianglesLists[0].getDev() > -4 * LENGTH) continue;
            if (i == 3 && trianglesLists[0].getDev() > -3 * LENGTH) continue;
            if (i == 4 && trianglesLists[0].getDev() > -2 * LENGTH) continue;
            if (i == 5 && trianglesLists[0].getDev() > -1 * LENGTH) continue;
            trianglesLists[i].moveUp();
        }
        postInvalidate();
    }

    private void resetTriangle(){
        for (int i = 0; i < TRIANGLE_NUM; i++) {
            trianglesLists[i].reset();
        }
        path = Triangle.SHIFT;
        colorIndex = 0;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        if (state == this.state) return;
        this.state = state;
        if (onStateListener != null) {
            if (state == STATE_READY) {
                onStateListener.onReady();
            }
            if (state == STATE_UP) {
                onStateListener.onUp();
            }
            if (state == STATE_NORMAL) {
                resetTriangle();
            }
        }

    }

    public void setStatus(int status) {
        if (status != STATUS_DYNAMIC || status != STATUS_NORMAL || status == this.status) {
            return;
        }
        this.status = status;
        init(context, null);
    }

    public void setOnStateListener(OnStateListener onStateListener) {
        this.onStateListener = onStateListener;
    }

    public interface OnStateListener {
        void onRefreshing();

        void onReady();

        void onUp();

        void onFall(int deltaY);
    }
}
