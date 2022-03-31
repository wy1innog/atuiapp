package com.ihblu.common.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

/**
 * @Description:
 * @Author: wy1in
 * @Date: 2022/3/31
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在界面未初始化之前调用的初始化窗口
        if (initArgs(getIntent().getExtras())) {

            int layoutId = getContentLayoutId();
            setContentView(layoutId);
            initView();
            initData();
        } else {
            finish();
        }

    }

    /**
     * 初始化窗口
     */
    protected void initWindows() {

    }

    /**
     * 初始化相关参数
     * @param bundle 如果
     * @return
     */
    protected boolean initArgs(Bundle bundle) {
        return true;
    }
    // get current source Id
    protected abstract int getContentLayoutId();

    protected void initView(){
    }

    protected void initData() {

    }

    /**
     * 当点击界面导航返回时，finsh当时界面
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof BaseFragment) {
                    if (((BaseFragment) fragment).onBackPressed()) {
                        return;
                    }
                }
            }
        }

        super.onBackPressed();
        finish();
    }
}
