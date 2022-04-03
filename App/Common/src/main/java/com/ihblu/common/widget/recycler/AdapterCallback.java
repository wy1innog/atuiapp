package com.ihblu.common.widget.recycler;

/**
 * @Description:
 * @Author: wy1in
 * @Date: 2022/4/1
 */
public interface AdapterCallback<Data> {
    void update(Data data, RecyclerAdapter.ViewHolder<Data> holder);
}
