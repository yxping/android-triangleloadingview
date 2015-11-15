package com.yxp.android_triangleloadingview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;

import com.yxp.android_triangleloadingview.view.MyListView;
import com.yxp.triangleloadingview.TriangleLoadingView;

/**
 * Created by yxp on 2015/11/14.
 */
public class DynamicLoadingActivity extends Activity {
    private MyListView mListView;
    private ArrayAdapter<String> mAdapter;
    private TriangleLoadingView loadingView;

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
                }
            }
        });
    }

    public void initLoadingView(){
        loadingView.setOnStateListener(new TriangleLoadingView.OnStateListener() {
            @Override
            public void onRefreshing() {
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
