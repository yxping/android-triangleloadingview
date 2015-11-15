package com.yxp.android_triangleloadingview.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;

import com.yxp.android_triangleloadingview.R;
import com.yxp.triangleloadingview.TriangleLoadingView;

/**
 * Created by yxp on 2015/11/12.
 */
public class MyListView extends ListView {
    public final static int SCROLL_DURATION = 400; // scroll back duration
    private OnMyScrollListener onMyScrollListener;
    private OnRefreshListener onRefreshListener;
    private TextView textView;
    private LinearLayout.LayoutParams params;
    private boolean hasInit = false;
    private int headerViewHeight;
    private int marginTop;
    private float mLastY = -1;
    private final static float OFFSET_RADIO = 2.5f;
    private Scroller scroller;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public MyListView(Context context) {
        super(context);
        init(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(Context context) {
        View headView = LayoutInflater.from(context).inflate(R.layout.head_view, null);
        textView = (TextView) headView.findViewById(R.id.head_view_textview);
        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!hasInit) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                    headerViewHeight = textView.getHeight();
                    params.topMargin = -headerViewHeight;
                    marginTop = params.topMargin;
                    textView.setLayoutParams(params);
                    hasInit = true;
                }

            }
        });
        addHeaderView(headView);
        scroller = new Scroller(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (getFirstVisiblePosition() == 0 && deltaY > 0) {
                    // the first item is showing, header has shown or pull down.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    invokeOnScrolling(deltaY / OFFSET_RADIO);
                } else if (getFirstVisiblePosition() == 0 && marginTop > -headerViewHeight
                        && deltaY < 0) {
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    invokeOnScrolling(deltaY / OFFSET_RADIO);
                }
                break;
            default:
                mLastY = -1; // reset
                if (marginTop > -headerViewHeight) {
                    // invoke refresh
                    if (-marginTop > headerViewHeight) {
                        if (onRefreshListener != null) {
                            onRefreshListener.onRefresh();
                        }
                    }
                    resetHeaderHeight();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void resetHeaderHeight() {
        int height = headerViewHeight + marginTop;

        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
//        if (textView.getState() == TriangleLoadingView.STATE_REFRESHING && height > headerViewHeight) {
//            finalHeight = headerViewHeight;
//        }
        scroller.startScroll(0, height, 0, finalHeight - height,
                SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
        onMyScrollListener.onScrollChanged(0, false);
    }

    private void updateHeaderHeightWhileUp(float delta) {
        marginTop = (int) (marginTop + delta);
    }

    private void updateHeaderHeight(float delta) {
        marginTop = (int) (marginTop + delta);
        params = (LinearLayout.LayoutParams) textView.getLayoutParams();
        params.topMargin = marginTop;
        textView.setLayoutParams(params);
        setSelection(0);
    }

    private void invokeOnScrolling(float deltaY) {
        if (onMyScrollListener != null) onMyScrollListener.onScrollChanged((int) deltaY, true);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            params = (LinearLayout.LayoutParams) textView.getLayoutParams();
            params.topMargin = marginTop - (headerViewHeight + marginTop) + scroller.getCurrY();
            textView.setLayoutParams(params);
            if (scroller.getCurrY() == 0) marginTop = -headerViewHeight;
        }
        super.computeScroll();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        View c = this.getChildAt(1);
//        int scrolly = -c.getTop() + this.getFirstVisiblePosition() * c.getHeight();
    }

    public void setOnMyScrollListener(OnMyScrollListener listener) {
        onMyScrollListener = listener;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public interface OnMyScrollListener {
        void onScrollChanged(int scrolly, boolean stillTouch);
    }

    public interface OnRefreshListener {
        void onRefresh();
    }
}
