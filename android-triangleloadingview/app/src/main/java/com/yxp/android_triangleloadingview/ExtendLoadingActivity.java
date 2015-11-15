package com.yxp.android_triangleloadingview;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.yxp.android_triangleloadingview.view.MyListView;
import com.yxp.triangleloadingview.TriangleLoadingView;

/**
 * Created by yxp on 2015/11/7.
 */
public class ExtendLoadingActivity extends Activity {

    private MyListView mListView;
    private ArrayAdapter<String> mAdapter;
    private TriangleLoadingView loadingView;

    private RelativeLayout.LayoutParams params;
    private int UP_TIME = 300;
    private ValueAnimator valueAnimator;
    private int marginTop = -1;
    private int lastMargin = 0;
    private static int MAX_MARGIN = -100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extend);
        mListView = (MyListView) findViewById(R.id.listView);
        loadingView = (TriangleLoadingView) findViewById(R.id.loadingview);
        initListView();
        initLoadingView();
    }

    public void initListView(){
        String[] strings = new String[30];
        for (int i = 0; i < 30; i++) {
            strings[i] = "這是第" + i + "个item";
        }
        mAdapter = new ArrayAdapter<String>(this, R.layout.listview_items, strings);
        mListView.setAdapter(mAdapter);
        mListView.setOnMyScrollListener(new MyListView.OnMyScrollListener() {
            @Override
            public void onScrollChanged(int scrolly, boolean stillTouch) {
                if (!stillTouch && loadingView.getState() != TriangleLoadingView.STATE_REFRESHING
                        && loadingView.getState() != TriangleLoadingView.STATE_READY) {
                    loadingView.startRiseAnim();
                } else {
                    loadingView.fallByDelta(scrolly);
                    if(loadingView.getState() != TriangleLoadingView.STATE_REFRESHING) {
                        updateLoadingViewTopMargin(scrolly);
                    }
                }
            }
        });
    }

    public void updateLoadingViewTopMargin(int delta){
        params = (RelativeLayout.LayoutParams) loadingView.getLayoutParams();
        if(params.topMargin > 0) {
            params.topMargin = 0;
            loadingView.setLayoutParams(params);
            return;
        } else if (params.topMargin == 0 && delta > 0) {
            return;
        }
        params.topMargin += delta;
        loadingView.setLayoutParams(params);
    }

    public void initLoadingView(){
        loadingView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (marginTop == -1) {
                    params = (RelativeLayout.LayoutParams) loadingView.getLayoutParams();
                    params.topMargin = MAX_MARGIN;
                    marginTop = MAX_MARGIN;
                    loadingView.setLayoutParams(params);
                }

            }
        });
        loadingView.setOnStateListener(new TriangleLoadingView.OnStateListener() {
            @Override
            public void onRefreshing() {
                if (params.topMargin > MAX_MARGIN) {
                    valueAnimator = ValueAnimator.ofInt(0, params.topMargin - MAX_MARGIN);
                    valueAnimator.setTarget(loadingView);
                    valueAnimator.setDuration(UP_TIME);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            updateLoadingViewTopMargin(lastMargin - (Integer) animation.getAnimatedValue());
                            lastMargin = (Integer) animation.getAnimatedValue();
                        }
                    });
                    valueAnimator.start();
                }
                handler.sendMessageDelayed(new Message(), 1000);
            }

            @Override
            public void onReady() {
                loadingView.startRefresh();
            }

            @Override
            public void onUp() {

            }

            @Override
            public void onFall(int deltaY) {

            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            loadingView.startRiseAnim();
        }
    };
}
