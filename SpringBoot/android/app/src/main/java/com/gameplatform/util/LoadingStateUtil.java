package com.gameplatform.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import com.gameplatform.R;

/**
 * Loading state utility for managing different UI states
 * Requirements: 3.3, 4.4, 5.5
 */
public class LoadingStateUtil {
    
    private static final String TAG = "LoadingStateUtil";
    
    public enum State {
        LOADING,
        SUCCESS,
        ERROR,
        EMPTY,
        NO_NETWORK
    }
    
    public interface OnRetryClickListener {
        void onRetryClick();
    }
    
    public interface OnEmptyActionClickListener {
        void onEmptyActionClick();
    }
    
    /**
     * State view holder for managing different states
     */
    public static class StateViewHolder {
        private ViewGroup container;
        private View loadingView;
        private View errorView;
        private View emptyView;
        private View noNetworkView;
        private View contentView;
        
        private ProgressBar progressBar;
        private TextView loadingText;
        
        private ImageView errorIcon;
        private TextView errorTitle;
        private TextView errorMessage;
        private Button errorRetryButton;
        
        private ImageView emptyIcon;
        private TextView emptyTitle;
        private TextView emptyMessage;
        private Button emptyActionButton;
        
        private ImageView noNetworkIcon;
        private TextView noNetworkTitle;
        private TextView noNetworkMessage;
        private Button noNetworkRetryButton;
        
        private OnRetryClickListener onRetryClickListener;
        private OnEmptyActionClickListener onEmptyActionClickListener;
        
        public StateViewHolder(ViewGroup container) {
            this.container = container;
            initializeViews();
        }
        
        private void initializeViews() {
            Context context = container.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            
            // Create loading view
            loadingView = inflater.inflate(R.layout.view_loading_state, container, false);
            progressBar = loadingView.findViewById(R.id.progress_bar);
            loadingText = loadingView.findViewById(R.id.tv_loading);
            
            // Create error view
            errorView = inflater.inflate(R.layout.view_error_state, container, false);
            errorIcon = errorView.findViewById(R.id.iv_error_icon);
            errorTitle = errorView.findViewById(R.id.tv_error_title);
            errorMessage = errorView.findViewById(R.id.tv_error_message);
            errorRetryButton = errorView.findViewById(R.id.btn_retry);
            
            // Create empty view
            emptyView = inflater.inflate(R.layout.view_empty_state, container, false);
            emptyIcon = emptyView.findViewById(R.id.iv_empty_icon);
            emptyTitle = emptyView.findViewById(R.id.tv_empty_title);
            emptyMessage = emptyView.findViewById(R.id.tv_empty_message);
            emptyActionButton = emptyView.findViewById(R.id.btn_empty_action);
            
            // Create no network view
            noNetworkView = inflater.inflate(R.layout.view_no_network_state, container, false);
            noNetworkIcon = noNetworkView.findViewById(R.id.iv_no_network_icon);
            noNetworkTitle = noNetworkView.findViewById(R.id.tv_no_network_title);
            noNetworkMessage = noNetworkView.findViewById(R.id.tv_no_network_message);
            noNetworkRetryButton = noNetworkView.findViewById(R.id.btn_no_network_retry);
            
            // Set click listeners
            errorRetryButton.setOnClickListener(v -> {
                if (onRetryClickListener != null) {
                    AnimationUtil.bounceClick(errorRetryButton);
                    onRetryClickListener.onRetryClick();
                }
            });
            
            emptyActionButton.setOnClickListener(v -> {
                if (onEmptyActionClickListener != null) {
                    AnimationUtil.bounceClick(emptyActionButton);
                    onEmptyActionClickListener.onEmptyActionClick();
                }
            });
            
            noNetworkRetryButton.setOnClickListener(v -> {
                if (onRetryClickListener != null) {
                    AnimationUtil.bounceClick(noNetworkRetryButton);
                    onRetryClickListener.onRetryClick();
                }
            });
            
            // Add views to container
            container.addView(loadingView);
            container.addView(errorView);
            container.addView(emptyView);
            container.addView(noNetworkView);
            
            // Initially hide all state views
            hideAllStateViews();
        }
        
        /**
         * Show loading state
         */
        public void showLoading() {
            showLoading(R.string.loading_games);
        }
        
        public void showLoading(@StringRes int messageRes) {
            showLoading(container.getContext().getString(messageRes));
        }
        
        public void showLoading(String message) {
            hideAllStateViews();
            hideContentView();
            
            if (message != null) {
                loadingText.setText(message);
                loadingText.setVisibility(View.VISIBLE);
            } else {
                loadingText.setVisibility(View.GONE);
            }
            
            loadingView.setVisibility(View.VISIBLE);
            AnimationUtil.fadeIn(loadingView);
        }
        
        /**
         * Show error state
         */
        public void showError(String title, String message) {
            showError(R.drawable.ic_error, title, message, R.string.retry);
        }
        
        public void showError(@DrawableRes int iconRes, String title, String message, @StringRes int buttonTextRes) {
            hideAllStateViews();
            hideContentView();
            
            errorIcon.setImageResource(iconRes);
            errorTitle.setText(title);
            errorMessage.setText(message);
            errorRetryButton.setText(buttonTextRes);
            
            errorView.setVisibility(View.VISIBLE);
            AnimationUtil.fadeIn(errorView);
        }
        
        /**
         * Show empty state
         */
        public void showEmpty(String title, String message) {
            showEmpty(R.drawable.ic_empty, title, message, null);
        }
        
        public void showEmpty(@DrawableRes int iconRes, String title, String message, String actionButtonText) {
            hideAllStateViews();
            hideContentView();
            
            emptyIcon.setImageResource(iconRes);
            emptyTitle.setText(title);
            emptyMessage.setText(message);
            
            if (actionButtonText != null && !actionButtonText.isEmpty()) {
                emptyActionButton.setText(actionButtonText);
                emptyActionButton.setVisibility(View.VISIBLE);
            } else {
                emptyActionButton.setVisibility(View.GONE);
            }
            
            emptyView.setVisibility(View.VISIBLE);
            AnimationUtil.fadeIn(emptyView);
        }
        
        /**
         * Show no network state
         */
        public void showNoNetwork() {
            hideAllStateViews();
            hideContentView();
            
            noNetworkIcon.setImageResource(R.drawable.ic_no_network);
            noNetworkTitle.setText(R.string.no_network_title);
            noNetworkMessage.setText(R.string.no_network_message);
            noNetworkRetryButton.setText(R.string.retry);
            
            noNetworkView.setVisibility(View.VISIBLE);
            AnimationUtil.fadeIn(noNetworkView);
        }
        
        /**
         * Show success state (content)
         */
        public void showSuccess() {
            hideAllStateViews();
            showContentView();
        }
        
        /**
         * Hide all state views
         */
        private void hideAllStateViews() {
            loadingView.setVisibility(View.GONE);
            errorView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            noNetworkView.setVisibility(View.GONE);
        }
        
        /**
         * Hide content view
         */
        private void hideContentView() {
            if (contentView != null) {
                contentView.setVisibility(View.GONE);
            }
        }
        
        /**
         * Show content view
         */
        private void showContentView() {
            if (contentView != null) {
                contentView.setVisibility(View.VISIBLE);
                AnimationUtil.fadeIn(contentView);
            }
        }
        
        /**
         * Set content view
         */
        public void setContentView(View contentView) {
            this.contentView = contentView;
        }
        
        /**
         * Set retry click listener
         */
        public void setOnRetryClickListener(OnRetryClickListener listener) {
            this.onRetryClickListener = listener;
        }
        
        /**
         * Set empty action click listener
         */
        public void setOnEmptyActionClickListener(OnEmptyActionClickListener listener) {
            this.onEmptyActionClickListener = listener;
        }
        
        /**
         * Get current state
         */
        public State getCurrentState() {
            if (loadingView.getVisibility() == View.VISIBLE) {
                return State.LOADING;
            } else if (errorView.getVisibility() == View.VISIBLE) {
                return State.ERROR;
            } else if (emptyView.getVisibility() == View.VISIBLE) {
                return State.EMPTY;
            } else if (noNetworkView.getVisibility() == View.VISIBLE) {
                return State.NO_NETWORK;
            } else {
                return State.SUCCESS;
            }
        }
        
        /**
         * Check if currently showing loading
         */
        public boolean isLoading() {
            return getCurrentState() == State.LOADING;
        }
        
        /**
         * Check if currently showing error
         */
        public boolean isError() {
            return getCurrentState() == State.ERROR;
        }
        
        /**
         * Check if currently showing empty
         */
        public boolean isEmpty() {
            return getCurrentState() == State.EMPTY;
        }
        
        /**
         * Check if currently showing no network
         */
        public boolean isNoNetwork() {
            return getCurrentState() == State.NO_NETWORK;
        }
        
        /**
         * Check if currently showing success
         */
        public boolean isSuccess() {
            return getCurrentState() == State.SUCCESS;
        }
    }
    
    /**
     * Create state view holder for a container
     */
    public static StateViewHolder createStateViewHolder(ViewGroup container) {
        return new StateViewHolder(container);
    }
    
    /**
     * Handle network-aware state management
     */
    public static void handleNetworkAwareState(StateViewHolder stateViewHolder, 
                                             boolean hasData, 
                                             boolean isLoading, 
                                             String error,
                                             Context context) {
        NetworkStatusUtil networkUtil = NetworkStatusUtil.getInstance(context);
        
        if (isLoading) {
            stateViewHolder.showLoading();
        } else if (!networkUtil.isNetworkConnected()) {
            stateViewHolder.showNoNetwork();
        } else if (error != null && !error.isEmpty()) {
            stateViewHolder.showError("加载失败", error);
        } else if (!hasData) {
            stateViewHolder.showEmpty("暂无数据", "还没有找到相关内容");
        } else {
            stateViewHolder.showSuccess();
        }
    }
    
    /**
     * Get appropriate error message based on network status
     */
    public static String getNetworkAwareErrorMessage(Context context, String originalError) {
        NetworkStatusUtil networkUtil = NetworkStatusUtil.getInstance(context);
        
        if (!networkUtil.isNetworkConnected()) {
            return context.getString(R.string.no_network_message);
        } else if (networkUtil.shouldShowDataUsageWarning()) {
            return "移动网络连接可能不稳定，请检查网络设置";
        } else {
            return originalError != null ? originalError : "网络请求失败，请稍后重试";
        }
    }
}