package com.yxp.android_triangleloadingview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

/**
 * Created by yxp on 2015/11/14.
 */
public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onStaticClick(View view) {
        Intent intent = new Intent(MainActivity.this, NormalLoadingActivity.class);
        startActivity(intent);
    }

    public void onDynamicClick(View view) {
        Intent intent = new Intent(MainActivity.this, DynamicLoadingActivity.class);
        startActivity(intent);
    }

    public void onExtendClick(View view) {
        Intent intent = new Intent(MainActivity.this, ExtendLoadingActivity.class);
        startActivity(intent);
    }
}
