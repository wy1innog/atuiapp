package com.ihblu.atuiapp.helper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.widget.TabHost;

import com.ihblu.common.app.BaseFragment;

/**
 * @Description: 完成对fragment的调度和重用问题，达到最优fragment切换
 * @Author: wy1in
 * @Date: 2022/7/23
 */
public class NavHelper<T> {
    // 所有的Tab几乎
    private final SparseArray<Tab<T>> tabs = new SparseArray<>();
    // 用户初始化的必需参数
    private final FragmentManager mFragmentManager;
    private final int containerId;
    private final Context mContext;
    private OnTabChangedListener<T> listener;

    // 当前选中的Tab
    private Tab<T> currentTab;

    public NavHelper(Context mContext, int containerId, FragmentManager mFragmentManager,
                     OnTabChangedListener<T> listener) {
        this.mFragmentManager = mFragmentManager;
        this.containerId = containerId;
        this.mContext = mContext;
        this.listener = listener;
    }

    /**
     * 添加Tab
     * @param menuId Tab对应的菜单id
     * @param tab Tab
     */
    public NavHelper<T> add(int menuId, Tab<T> tab) {
        tabs.put(menuId, tab);
        // 流式添加
        return this;
    }

    /**
     * 获取当前显示的Tab
     * @return 当前的Tab
     */
    public Tab<T> getCurrentTab() {
        return currentTab;
    }

    /**
     * 执行点击菜单的操作
     * @param itemId 菜单的id
     * @return 是否能够处理这个点击
     */
    public boolean performClickMenu(int itemId) {
        // 集合中寻找点击的菜单对应的Tab, 如果有则进行处理
        Tab<T> tab = tabs.get(itemId);
        if (tab != null) {
            doSelect(tab);
            return true;
        }
        return false;
    }

    /**
     * 进行Tab选择操作
     */
    private void doSelect(Tab<T> tab) {
        Tab<T> oldTab = null;
        if (currentTab != null) {
            oldTab = currentTab;
            if (oldTab == tab) {
                notifyTabReselect(tab);
                return;
            }
        }
        currentTab = tab;
        doTabChanged(tab, oldTab);
    }

    /**
     * 进行Fragment的真实调度操作
     * @param newTab 新Tab
     * @param oldTab 旧Tab
     */
    private void doTabChanged(Tab<T> newTab, Tab<T> oldTab) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (oldTab != null) {
            if (oldTab.fragment != null) {
                // 从界面移除，但是还在Fragment的缓存空间中
                ft.detach(oldTab.fragment);
            }
        }

        if (newTab != null) {
            if (newTab.fragment == null) {
                // 首次新建
                Fragment fragment = Fragment.instantiate(mContext, newTab.clz.getName(), null);
                // 缓存起来
                newTab.fragment = fragment;
                // 提交到FragmentManager
                ft.add(containerId, fragment, newTab.clz.getName());
            } else {
                // 从提交到FragmentManager的缓存控件中重新加载到界面中
                ft.attach(newTab.fragment);
            }
        }
        ft.commit();
        notifyTabSelect(newTab, oldTab);
    }

    /**
     * 回调监听器
     * @param newTab 新Tab
     * @param oldTab 旧Tab
     */
    private void notifyTabSelect(Tab<T> newTab, Tab<T> oldTab) {
        if (listener != null) {
            listener.onTabChanged(newTab, oldTab);
        }
    }

    private void notifyTabReselect(Tab<T> tab) {
        //TODO: 二次点击Tab所做的操作
    }
    /**
     * 所有的Tab基础属性
     * @param <T> 泛型的额外参数
     */
    public static class Tab<T> {
        public Tab(Class<?> clz, T extra) {
            this.clz = clz;
            this.extra = extra;
        }

        // Fragment对应的Class信息，继承自BaseFragment
        public Class<?> clz;
        // 额外的字段，用户自己设定需要使用
        public T extra;
        // 内部缓存的对应的Fragment, Package权限，外部无法使用
        Fragment fragment;
    }

    /**
     * 定义事件处理完成后的回调接口
     * @param <T>
     */
    public interface OnTabChangedListener<T> {
        void onTabChanged(Tab<T> newTab, Tab<T> oldTab);
    }
}
