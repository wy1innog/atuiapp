package com.ihblu.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ihblu.common.R;
import com.ihblu.common.widget.recycler.RecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class GalleyView extends RecyclerView {
    private static final int LOADER_ID = 0x100;
    // 最大的选中图片数量
    private static final int MAX_IMAEG_COUNT = 3;
    // 最小的图片大小
    private static final int MIN_IMAGE_FILE_SIZE = 10 * 1024;
    private Adapter mAdapter = new Adapter();
    private LoaderCallback mLoaderCallback = new LoaderCallback();
    private List<Image> mSelectedImages = new LinkedList<>();
    private SelectedChangeListener mSelectedChangeListener;


    public GalleyView(Context context) {
        super(context);
        init();
    }

    public GalleyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new GridLayoutManager(getContext(), 4));
        // 初始化适配器
        setAdapter(mAdapter);
        mAdapter.setAdapterListeners(new RecyclerAdapter.AdapterListenerImpl<Image>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Image image) {
                // Cell点击操作，如果说我们的点击是允许的，那么更新对应的Cell状态，然后更新界面
                // 同理，如果说不能允许点击(以达到最大的选中数量)，那么就不刷新界面
                if (onItemSelectClick(image)) {
                    holder.updateData(image);
                }
            }
        });
    }

    /**
     * 初始化方法
     * @param loaderManager Loader管理器
     * @return 返回一个loaderId，可用于销毁Loader
     */
    public int setup(LoaderManager loaderManager, SelectedChangeListener listener) {
        mSelectedChangeListener = listener;
        loaderManager.initLoader(LOADER_ID, null, mLoaderCallback);
        return LOADER_ID;
    }

    /**
     * Cell点击的具体逻辑
     * @param image
     * @return True, 代表进行了数据修改，需要刷新，反之，不刷新
     */
    private boolean onItemSelectClick(Image image) {
        // 是否需要进行刷新
        boolean notifyRefresh;
        if (mSelectedImages.contains(image)) {
            // 如果之前已经存在则移除
            mSelectedImages.remove(image);
            image.isSelect = false;
            // 状态已经改变则需要更新
            notifyRefresh = true;
        } else {
            if (mSelectedImages.size() >= MAX_IMAEG_COUNT) {
                String str = getResources().getString(
                        com.ihblu.lang.R.string.label_gallery_select_max_size);
                Toast.makeText(getContext(),
                        String.format(str, MAX_IMAEG_COUNT), Toast.LENGTH_SHORT).show();
                notifyRefresh = false;
            } else {
                image.isSelect = true;
                mSelectedImages.add(image);
                notifyRefresh = true;
            }
        }
        // 如果数据有更改，那么需要通知外面的监听者数据选中改变了
        if (notifyRefresh) {
            notifySelectChanged();
        }
        return true;
    }

    /**
     * 得到选中的图片的全部地址
     * @return 返回一个数组
     */
    public String[] getSelectedPath() {
        String[] paths = new String[mSelectedImages.size()];
        int index = 0;
        for (Image image : mSelectedImages) {
            paths[index++] = image.path;
        }
        return paths;
    }

    /**
     * 可以进行清空选中的图片
     */
    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        for (Image image : mSelectedImages) {
            image.isSelect = false;
        }
        mSelectedImages.clear();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 通知选中状态改变
     */
    private void notifySelectChanged() {
        SelectedChangeListener listener = mSelectedChangeListener;
        if (listener != null) {
            listener.onSelectedCountChanged(mSelectedImages.size());
        }
    }

    /**
     * 通知Adapter数据更改的方法
     * @param images
     */
    private void updateSource(List<Image> images) {
        mAdapter.replace(images);
    }
    /**
     * 用于实际的数据加载的Loader Callback
     */
    private class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        private final String[] IMAGE_PROJECTION = new String[] {
                MediaStore.Images.Media._ID, //id
                MediaStore.Images.Media.DATA, // 图片路径
                MediaStore.Images.Media.DATE_ADDED // 图片的创建时间
        };
        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
            // 创建一个Loader
            if (id == LOADER_ID) {
                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,
                        null,
                        null,
                        // 按照图片的创建时间倒序查询
                        IMAGE_PROJECTION[2] + " DESC");
            }
            return null;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
            // 当Loader加载完成时
            List<Image> images = new ArrayList<>();
            if (cursor != null) {
                int count = cursor.getCount();
                if (count > 0) {
                    // 移动游标到开始
                    cursor.moveToFirst();
                    // 得到对应的列的Index坐标
                    int indexId = cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                    int indexPath = cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                    int indexDate = cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);
                    do {
                        int id = cursor.getInt(indexId);
                        String path = cursor.getString(indexPath);
                        long dateTime = cursor.getLong(indexDate);

                        File file = new File(path);
                        if (!file.exists() || file.length() < MIN_IMAGE_FILE_SIZE) {
                            continue;
                        }
                        Image image = new Image();
                        image.id = id;
                        image.path = path;
                        image.date = dateTime;
                        images.add(image);
                    } while (cursor.moveToNext());
                }
            }
            updateSource(images);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            // 当Loader销毁或者重置，进行数据清空
            updateSource(null);
        }
    }
    /**
     * 内部的数据结构
     */
    private static class Image {
        // 数据id
        int id;
        // 图片的路径
        String path;
        // 图片的创建时间
        long date;
        // 是否选中
        boolean isSelect;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Image image = (Image) o;

            return path != null ? path.equals(image.path) : image.path == null;
        }

        @Override
        public int hashCode() {
            return path != null ? path.hashCode() : 0;
        }
    }

    private class Adapter extends RecyclerAdapter<Image> {

        @Override
        protected int getItemViewType(int position, Image image) {
            return R.layout.cell_galley;
        }

        @Override
        protected ViewHolder<Image> onCreateViewHolder(View root, int viewType) {
            return new GalleyView.ViewHolder(root);
        }
    }

    /**
     * Cell对应的Holder
     */
    private class ViewHolder extends RecyclerAdapter.ViewHolder<Image> {
        private ImageView mPic;
        private View mShade;
        private CheckBox mSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mPic = itemView.findViewById(R.id.iv_image);
            mShade = itemView.findViewById(R.id.view_shade);
            mSelected = itemView.findViewById(R.id.cb_select);
        }

        @Override
        protected void onBind(Image image) {
            Glide.with(getContext())
                    .load(image.path) // 加载路径
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用缓存，直接从原图加载
                    .centerCrop()
                    .placeholder(net.qiujuer.genius.res.R.color.grey_200) // 默认颜色
                    .into(mPic);
            mShade.setVisibility(image.isSelect ? VISIBLE : INVISIBLE);
            mSelected.setChecked(image.isSelect);
            mSelected.setVisibility(VISIBLE);
        }
    }

    /**
     * 对外的监听器
     */
    public interface SelectedChangeListener {
        void onSelectedCountChanged(int count);
    }
}