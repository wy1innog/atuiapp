package com.ihblu.common.app;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @Description:
 * @Author: wy1in
 * @Date: 2022/3/31
 */
public abstract class BaseFragment extends Fragment {
    protected View mRoot;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initArgs(getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRoot == null) {
            int layoutId = getContentLayoutId();
            // 初始化当前的根布局，但是不在创建时就添加到container里面
            View root = inflater.inflate(layoutId, container, false);
            initView(root);
            mRoot = root;
        } else {
            if (mRoot.getParent() != null) {
                ((ViewGroup)mRoot.getParent()).removeView(mRoot);
            }
        }

        return mRoot;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    protected abstract int getContentLayoutId();

    protected void initView(View view) {
    }

    protected void initData() {

    }

    protected void initArgs(Bundle bundle) {
    }

    /**
     * 返回案件触发时调用
     * @return 返回True代表我已处理返回逻辑，Activity不用自己finish
     */
    public boolean onBackPressed() {
        return false;
    }
}
