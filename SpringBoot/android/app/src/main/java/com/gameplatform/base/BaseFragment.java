package com.gameplatform.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment基础类，提供通用功能和生命周期管理
 */
public abstract class BaseFragment extends Fragment {

    protected static final String TAG = "BaseFragment";
    
    // Fragment与Activity通信接口
    protected FragmentInteractionListener interactionListener;

    /**
     * Fragment与Activity通信接口
     */
    public interface FragmentInteractionListener {
        void onFragmentInteraction(String action, Bundle data);
        void showLoading(boolean show);
        void showMessage(String message);
        void showError(String error);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            interactionListener = (FragmentInteractionListener) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initData();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    /**
     * 初始化视图组件
     */
    protected abstract void initViews(View view);

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 显示加载状态
     */
    protected void showLoading(boolean show) {
        if (interactionListener != null) {
            interactionListener.showLoading(show);
        }
    }
}    /*
*
     * 显示消息
     */
    protected void showMessage(String message) {
        if (interactionListener != null) {
            interactionListener.showMessage(message);
        }
    }

    /**
     * 显示错误信息
     */
    protected void showError(String error) {
        if (interactionListener != null) {
            interactionListener.showError(error);
        }
    }

    /**
     * 与Activity进行交互
     */
    protected void sendInteraction(String action, Bundle data) {
        if (interactionListener != null) {
            interactionListener.onFragmentInteraction(action, data);
        }
    }

    /**
     * 保存Fragment状态
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveInstanceState(outState);
    }

    /**
     * 恢复Fragment状态
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
    }

    /**
     * 子类重写此方法保存状态
     */
    protected void saveInstanceState(Bundle outState) {
        // 子类实现
    }

    /**
     * 子类重写此方法恢复状态
     */
    protected void restoreInstanceState(Bundle savedInstanceState) {
        // 子类实现
    }

    /**
     * Fragment是否可见
     */
    protected boolean isFragmentVisible() {
        return isAdded() && !isHidden() && getUserVisibleHint();
    }

    /**
     * 当Fragment变为可见时调用
     */
    protected void onFragmentVisible() {
        // 子类重写实现懒加载等逻辑
    }

    /**
     * 当Fragment变为不可见时调用
     */
    protected void onFragmentInvisible() {
        // 子类重写实现暂停操作等逻辑
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFragmentVisible()) {
            onFragmentVisible();
        } else {
            onFragmentInvisible();
        }
    }
}