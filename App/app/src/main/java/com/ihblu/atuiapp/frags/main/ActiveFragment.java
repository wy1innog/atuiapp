package com.ihblu.atuiapp.frags.main;

import android.view.View;

import com.ihblu.atuiapp.R;
import com.ihblu.common.app.BaseFragment;
import com.ihblu.common.widget.GalleyView;

public class ActiveFragment extends BaseFragment {

    private GalleyView mGalley;

    public ActiveFragment() {
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mGalley = view.findViewById(R.id.galleyView);
    }

    @Override
    protected void initData() {
        super.initData();
        mGalley.setup(getLoaderManager(), new GalleyView.SelectedChangeListener() {
            @Override
            public void onSelectedCountChanged(int count) {

            }
        });
    }
}