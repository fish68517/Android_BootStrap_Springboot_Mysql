package com.gameplatform.base;

/**
 * Base interface for all MVP views
 * Requirements: 1.1, 2.1, 3.1, 4.1, 5.1, 6.1
 */
public interface BaseView {
    
    /**
     * Show loading indicator
     */
    void showLoading();
    
    /**
     * Hide loading indicator
     */
    void hideLoading();
    
    /**
     * Show error message
     * @param message Error message to display
     */
    void showError(String message);
    
    /**
     * Show success message
     * @param message Success message to display
     */
    void showSuccess(String message);
    
    /**
     * Check if view is active (not destroyed)
     * @return true if view is active, false otherwise
     */
    boolean isActive();
}