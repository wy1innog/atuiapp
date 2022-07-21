package com.ihblu.atuiapp;

import android.annotation.SuppressLint;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.ihblu.common.app.BaseActivity;
import com.ihblu.common.widget.PortraitView;

import net.qiujuer.genius.ui.widget.FloatActionButton;

import butterknife.BindView;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private View mLayAppbar;
    private PortraitView mIvPortrait;
    private TextView mTvTitle;
    private ImageView mIvSearch;
    private FrameLayout mFlContainer;
    private FloatActionButton mFabAction;
    private BottomNavigationView mNavigation;
    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        mLayAppbar = findViewById(R.id.appbar);
        mIvPortrait = findViewById(R.id.iv_portrait);
        mTvTitle = findViewById(R.id.tv_title);
        mIvSearch = findViewById(R.id.iv_search);
        mFlContainer = findViewById(R.id.lay_container);
        mFabAction = findViewById(R.id.btn_action);
        mNavigation = findViewById(R.id.navigation);
        mIvSearch.setOnClickListener(mOnClickListener);
        mFabAction.setOnClickListener(mOnClickListener);
        // add title background
        Glide.with(this)
                .load(R.drawable.bg_src_morning)
                .centerCrop()
                .into(new ViewTarget<View, GlideDrawable>(mLayAppbar) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource.getCurrent());
                    }
                });
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == mIvSearch) {

            } else if (view == mFabAction) {

            }
        }
    };

}