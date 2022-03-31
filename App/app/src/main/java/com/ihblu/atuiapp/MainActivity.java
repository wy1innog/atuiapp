package com.ihblu.atuiapp;

import android.databinding.DataBindingUtil;

import com.ihblu.atuiapp.databinding.ActivityMainBinding;
import com.ihblu.common.app.BaseActivity;
import com.ihblu.common.bean.UserBean;

public class MainActivity extends BaseActivity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, getContentLayoutId());
        UserBean userBean = new UserBean("小明", 18);
        binding.setUser(userBean);
    }
}