package com.ihblu.atuiapp;

import android.widget.TextView;

import com.ihblu.common.app.BaseActivity;

import butterknife.BindView;


public class MainActivity extends BaseActivity {

    private TextView mTvTest;
    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
    }
}