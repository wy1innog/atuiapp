package com.ihblu.atuiapp;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.ihblu.atuiapp.frags.main.ActiveFragment;
import com.ihblu.atuiapp.frags.main.ContactFragment;
import com.ihblu.atuiapp.frags.main.GroupFragment;
import com.ihblu.atuiapp.helper.NavHelper;
import com.ihblu.common.app.BaseActivity;
import com.ihblu.common.widget.PortraitView;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;

import butterknife.BindView;


public class MainActivity extends BaseActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavHelper.OnTabChangedListener<Integer> {

    private static final String TAG = "MainActivity";
    private View mLayAppbar;
    private PortraitView mIvPortrait;
    private TextView mTvTitle;
    private ImageView mIvSearch;
    private FrameLayout mFlContainer;
    private FloatActionButton mFabAction;
    private BottomNavigationView mNavigation;
    private NavHelper<Integer> mNavHelper;

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
        mNavigation.setOnNavigationItemSelectedListener(this);
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

    @Override
    protected void initData() {
        super.initData();
        mNavHelper = new NavHelper<>(this, R.id.lay_container, getSupportFragmentManager(), this);
        mNavHelper.add(R.id.action_home, new NavHelper.Tab<>(ActiveFragment.class, R.string.title_home))
        .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.title_group))
        .add(R.id.action_contact, new NavHelper.Tab<>(ContactFragment.class, R.string.title_contact));

        // ????????????????????????Menu,???????????????????????????
        Menu menu = mNavigation.getMenu();
        // ??????????????????Home
        menu.performIdentifierAction(R.id.action_home, 0);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == mIvSearch) {

            } else if (view == mFabAction) {

            }
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // ???????????????
        return mNavHelper.performClickMenu(menuItem.getItemId());
    }

    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        mTvTitle.setText(newTab.extra);

        // ?????????????????????????????????????????????
        float transY = 0;
        float rotation = 0;
        if (newTab.extra.equals(R.string.title_home)) {
            // ???????????????????????????transY?????????0????????????
            transY = Ui.dipToPx(getResources(), 76);
        } else if (newTab.extra.equals(R.string.title_group)){
            mFabAction.setImageResource(com.ihblu.common.R.drawable.ic_group_add);
            rotation = -360;
        } else if (newTab.extra.equals(R.string.title_contact)) {
            mFabAction.setImageResource(com.ihblu.common.R.drawable.ic_contact_add);
            rotation = 360;
        }
        // ????????????
        // ?????????Y????????????????????????????????????
        mFabAction.animate()
                .rotation(rotation)
                .translationY(transY)
                .setDuration(480)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .start();
    }
}