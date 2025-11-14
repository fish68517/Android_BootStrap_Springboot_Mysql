package com.gameplatform.base;

import java.lang.ref.WeakReference;

/**
 * Base abstract class for all MVP presenters
 * Requirements: 1.1, 2.1, 3.1, 4.1, 5.1, 6.1
 */
public abstract class BasePresenter<V extends BaseView> {
    
    private WeakReference<V> viewRef;
    
    /**
     * Attach view to presenter
     * @param view View to attach
     */
    public void attachView(V view) {
        viewRef = new WeakReference<>(view);
    }
    
    /**
     * Detach view from presenter
     */
    public void detachView() {
        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }
    }
    
    /**
     * Get attached view
     * @return Attached view or null if not attached
     */
    protected V getView() {
        return viewRef != null ? viewRef.get() : null;
    }
    
    /**
     * Check if view is attached and active
     * @return true if view is attached and active, false otherwise
     */
    protected boolean isViewAttached() {
        return viewRef != null && viewRef.get() != null && viewRef.get().isActive();
    }
    
    /**
     * Called when presenter is created
     */
    public void onCreate() {
        // Override in subclasses if needed
    }
    
    /**
     * Called when presenter is destroyed
     */
    public void onDestroy() {
        detachView();
    }
}