package com.ihblu.common.widget.recycler;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ihblu.common.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @Description:
 * @Author: wy1in
 * @Date: 2022/4/1
 */
public abstract class RecyclerAdapter<Data>
        extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder<Data>>
        implements View.OnClickListener, View.OnLongClickListener, AdapterCallback<Data>{
    private List<Data> mDataList = new ArrayList<>();
    private AdapterListener<Data> listeners;

    public RecyclerAdapter(List<Data> dataList, AdapterListener<Data> listener) {
        this.mDataList = dataList;
        this.listeners = listener;
    }

    public RecyclerAdapter(AdapterListener<Data> listener) {
        this(new ArrayList<Data>(), listener);
    }

    public RecyclerAdapter() {
        this(null);
    }
    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, mDataList.get(position));
    }

    /**
     * 得到布局的类型
     * @param position 坐标
     * @param data 当前的数据
     * @return XML文件的ID，用于创建ViewHolder
     */
    @LayoutRes
    protected abstract int getItemViewType(int position, Data data);

    /**
     * 创建一个ViewHolder
     * @param viewGroup parent:RecyclerView
     * @param viewType 约定为XML布局的Id
     * @return ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder<Data> onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // 得到LayoutInflater用于把XML初始化为View
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        // 把XML id为viewTYpe的文件初始化为一个root view
        View root = inflater.inflate(viewType, viewGroup, false);
        // 通过子类必须实现的方法，得到一个viewHolder
        ViewHolder<Data> holder = onCreateViewHolder((ViewGroup) root, viewType);

        root.setTag(R.id.tag_recycler_holder, holder);
        root.setOnClickListener(this);
        root.setOnLongClickListener(this);
        holder.callback = this;
        // 设置view的Tag为ViewHolder,进行双向绑定
        return null;
    }

    /**
     * 当得到一个新的ViewHolder
     * @param root 根布局
     * @param viewType 布局类型，其实就是xml的ID
     * @return
     */
    protected abstract ViewHolder<Data> onCreateViewHolder(View root, int viewType);

    /**
     * 绑定数据到一个Holder上
     * @param dataViewHolder ViewHolder
     * @param position 坐标
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder<Data> dataViewHolder, int position) {
        // 得到需要绑定的数据
        Data data = mDataList.get(position);
        // 出发Holder的绑定方法
        dataViewHolder.bind(data);
    }


    /**
     * 得到当前集合的数据量
     * @return
     */
    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public void onClick(View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.tag_recycler_holder);
        if (this.listeners != null) {
            // 得到ViewHolder当前对应的适配器中的坐标
            int pos = viewHolder.getAdapterPosition();
            this.listeners.onItemClick(viewHolder, mDataList.get(pos));
        }
    }

    @Override
    public boolean onLongClick(View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.tag_recycler_holder);
        if (this.listeners != null) {
            // 得到ViewHolder当前对应的适配器中的坐标
            int pos = viewHolder.getAdapterPosition();
            this.listeners.onItemLongClick(viewHolder, mDataList.get(pos));
            return true;
        }
        return false;
    }

    @Override
    public void update(Data data, ViewHolder<Data> holder) {

    }

    /**
     * 插入一条数据并通知插入
     * @param data Data
     */
    public void add(Data data) {
        mDataList.add(data);
        notifyItemInserted(mDataList.size() - 1);
    }

    /**
     * 插入一堆数据，并通知这段集合更新
     * @param dataList
     */
    public void add(Data... dataList) {
        if (dataList != null && dataList.length > 0 ) {
            int startPosition = mDataList.size();
            Collections.addAll(mDataList, dataList);
            notifyItemRangeChanged(startPosition, dataList.length);
        }
    }

    public void add(Collection<Data> dataList) {
        if (dataList != null && dataList.size() > 0) {
            int startPosition = mDataList.size();
            mDataList.addAll(dataList);
            notifyItemRangeChanged(startPosition, dataList.size());
        }
    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public void replace(Collection<Data> dataList) {
        if (dataList == null || dataList.size() == 0) {
            return;
        }
        mDataList.clear();
        mDataList.addAll(dataList);
        notifyDataSetChanged();
    }


    /**
     * 自定义的VIewHolder
     * @param <Data> 泛型类型
     */
    public static abstract class ViewHolder<Data> extends RecyclerView.ViewHolder{
        protected Data mData;
        private AdapterCallback<Data> callback;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        /**
         * 用于绑定数据的触发
         * @param data 绑定的数据
         */
        void bind(Data data) {
            this.mData = data;

        }

        /**
         * 当出发绑定的时候的回调，必须复写
         * @param data
         */
        protected abstract void onBind(Data data);

        public void updateData(Data data) {
            if (this.callback != null) {
                this.callback.update(data, this);
            }
        }
    }

    public interface AdapterListener<Data> {
        void onItemClick(RecyclerAdapter.ViewHolder holder, Data data);
        void onItemLongClick(RecyclerAdapter.ViewHolder holder, Data data);
    }

    public void setAdapterListeners(AdapterListener<Data> listener) {
        this.listeners = listener;
    }
}
