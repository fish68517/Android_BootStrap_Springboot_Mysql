package com.gameplatform.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.gameplatform.R;

/**
 * Animation utility class for Material Motion and UI animations
 * Requirements: 3.3, 4.4, 5.5
 */
public class AnimationUtil {
    
    private static final String TAG = "AnimationUtil";
    
    // Animation durations
    public static final int DURATION_SHORT = 150;
    public static final int DURATION_MEDIUM = 300;
    public static final int DURATION_LONG = 500;
    
    // Animation delays
    public static final int DELAY_SHORT = 50;
    public static final int DELAY_MEDIUM = 100;
    public static final int DELAY_LONG = 200;
    
    /**
     * Fade in animation
     */
    public static void fadeIn(View view) {
        fadeIn(view, DURATION_MEDIUM, null);
    }
    
    public static void fadeIn(View view, int duration, Animator.AnimatorListener listener) {
        if (view == null) return;
        
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(duration);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        
        if (listener != null) {
            fadeIn.addListener(listener);
        }
        
        fadeIn.start();
    }
    
    /**
     * Fade out animation
     */
    public static void fadeOut(View view) {
        fadeOut(view, DURATION_MEDIUM, null);
    }
    
    public static void fadeOut(View view, int duration, Animator.AnimatorListener listener) {
        if (view == null) return;
        
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        fadeOut.setDuration(duration);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
            }
        });
        
        fadeOut.start();
    }
    
    /**
     * Scale in animation (Material Design)
     */
    public static void scaleIn(View view) {
        scaleIn(view, DURATION_MEDIUM, null);
    }
    
    public static void scaleIn(View view, int duration, Animator.AnimatorListener listener) {
        if (view == null) return;
        
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.setVisibility(View.VISIBLE);
        
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        
        animatorSet.playTogether(scaleX, scaleY, alpha);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new OvershootInterpolator(1.2f));
        
        if (listener != null) {
            animatorSet.addListener(listener);
        }
        
        animatorSet.start();
    }
    
    /**
     * Scale out animation
     */
    public static void scaleOut(View view) {
        scaleOut(view, DURATION_MEDIUM, null);
    }
    
    public static void scaleOut(View view, int duration, Animator.AnimatorListener listener) {
        if (view == null) return;
        
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        
        animatorSet.playTogether(scaleX, scaleY, alpha);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
            }
        });
        
        animatorSet.start();
    }
    
    /**
     * Slide in from bottom animation
     */
    public static void slideInFromBottom(View view) {
        slideInFromBottom(view, DURATION_MEDIUM, null);
    }
    
    public static void slideInFromBottom(View view, int duration, Animator.AnimatorListener listener) {
        if (view == null) return;
        
        view.setTranslationY(view.getHeight());
        view.setVisibility(View.VISIBLE);
        
        ObjectAnimator slideIn = ObjectAnimator.ofFloat(view, "translationY", view.getHeight(), 0f);
        slideIn.setDuration(duration);
        slideIn.setInterpolator(new DecelerateInterpolator());
        
        if (listener != null) {
            slideIn.addListener(listener);
        }
        
        slideIn.start();
    }
    
    /**
     * Slide out to bottom animation
     */
    public static void slideOutToBottom(View view) {
        slideOutToBottom(view, DURATION_MEDIUM, null);
    }
    
    public static void slideOutToBottom(View view, int duration, Animator.AnimatorListener listener) {
        if (view == null) return;
        
        ObjectAnimator slideOut = ObjectAnimator.ofFloat(view, "translationY", 0f, view.getHeight());
        slideOut.setDuration(duration);
        slideOut.setInterpolator(new AccelerateInterpolator());
        
        slideOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
            }
        });
        
        slideOut.start();
    }
    
    /**
     * Slide in from right animation
     */
    public static void slideInFromRight(View view) {
        slideInFromRight(view, DURATION_MEDIUM, null);
    }
    
    public static void slideInFromRight(View view, int duration, Animator.AnimatorListener listener) {
        if (view == null) return;
        
        view.setTranslationX(view.getWidth());
        view.setVisibility(View.VISIBLE);
        
        ObjectAnimator slideIn = ObjectAnimator.ofFloat(view, "translationX", view.getWidth(), 0f);
        slideIn.setDuration(duration);
        slideIn.setInterpolator(new DecelerateInterpolator());
        
        if (listener != null) {
            slideIn.addListener(listener);
        }
        
        slideIn.start();
    }
    
    /**
     * Bounce animation for button clicks
     */
    public static void bounceClick(View view) {
        if (view == null) return;
        
        AnimatorSet animatorSet = new AnimatorSet();
        
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f);
        scaleDownX.setDuration(100);
        scaleDownY.setDuration(100);
        
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f);
        scaleUpX.setDuration(100);
        scaleUpY.setDuration(100);
        
        animatorSet.play(scaleDownX).with(scaleDownY);
        animatorSet.play(scaleUpX).with(scaleUpY).after(scaleDownX);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        
        animatorSet.start();
    }
    
    /**
     * Pulse animation for highlighting elements
     */
    public static void pulse(View view) {
        pulse(view, 2, null);
    }
    
    public static void pulse(View view, int repeatCount, Animator.AnimatorListener listener) {
        if (view == null) return;
        
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.7f, 1f);
        
        animatorSet.playTogether(scaleX, scaleY, alpha);
        animatorSet.setDuration(DURATION_LONG);
        animatorSet.setRepeatCount(repeatCount);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        
        if (listener != null) {
            animatorSet.addListener(listener);
        }
        
        animatorSet.start();
    }
    
    /**
     * Shake animation for error indication
     */
    public static void shake(View view) {
        if (view == null) return;
        
        ObjectAnimator shake = ObjectAnimator.ofFloat(view, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
        shake.setDuration(DURATION_LONG);
        shake.setInterpolator(new AccelerateDecelerateInterpolator());
        shake.start();
    }
    
    /**
     * Rotate animation
     */
    public static void rotate(View view, float fromDegrees, float toDegrees) {
        rotate(view, fromDegrees, toDegrees, DURATION_MEDIUM, null);
    }
    
    public static void rotate(View view, float fromDegrees, float toDegrees, int duration, Animator.AnimatorListener listener) {
        if (view == null) return;
        
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", fromDegrees, toDegrees);
        rotate.setDuration(duration);
        rotate.setInterpolator(new AccelerateDecelerateInterpolator());
        
        if (listener != null) {
            rotate.addListener(listener);
        }
        
        rotate.start();
    }
    
    /**
     * Loading animation (continuous rotation)
     */
    public static ObjectAnimator startLoadingAnimation(View view) {
        if (view == null) return null;
        
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        rotate.setDuration(1000);
        rotate.setRepeatCount(ValueAnimator.INFINITE);
        rotate.setInterpolator(new AccelerateDecelerateInterpolator());
        rotate.start();
        
        return rotate;
    }
    
    /**
     * Stop loading animation
     */
    public static void stopLoadingAnimation(ObjectAnimator animator) {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
    }
    
    /**
     * Animate RecyclerView item entrance
     */
    public static void animateRecyclerViewItem(View itemView, int position) {
        if (itemView == null) return;
        
        // Stagger animation based on position
        int delay = position * DELAY_SHORT;
        
        itemView.setAlpha(0f);
        itemView.setTranslationY(100f);
        
        ViewCompat.animate(itemView)
                .alpha(1f)
                .translationY(0f)
                .setDuration(DURATION_MEDIUM)
                .setStartDelay(delay)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }
    
    /**
     * Animate list item removal
     */
    public static void animateItemRemoval(View itemView, Runnable onComplete) {
        if (itemView == null) return;
        
        ViewCompat.animate(itemView)
                .alpha(0f)
                .translationX(itemView.getWidth())
                .setDuration(DURATION_MEDIUM)
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(onComplete)
                .start();
    }
    
    /**
     * Animate favorite button state change
     */
    public static void animateFavoriteToggle(View favoriteButton, boolean isFavorited) {
        if (favoriteButton == null) return;
        
        AnimatorSet animatorSet = new AnimatorSet();
        
        // Scale down
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(favoriteButton, "scaleX", 1f, 0.7f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(favoriteButton, "scaleY", 1f, 0.7f);
        scaleDownX.setDuration(150);
        scaleDownY.setDuration(150);
        
        // Scale up with overshoot
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(favoriteButton, "scaleX", 0.7f, 1f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(favoriteButton, "scaleY", 0.7f, 1f);
        scaleUpX.setDuration(200);
        scaleUpY.setDuration(200);
        scaleUpX.setInterpolator(new OvershootInterpolator(2f));
        scaleUpY.setInterpolator(new OvershootInterpolator(2f));
        
        animatorSet.play(scaleDownX).with(scaleDownY);
        animatorSet.play(scaleUpX).with(scaleUpY).after(scaleDownX);
        
        animatorSet.start();
    }
    
    /**
     * Animate like button with heart effect
     */
    public static void animateLikeButton(View likeButton, boolean isLiked) {
        if (likeButton == null) return;
        
        if (isLiked) {
            // Animate like
            AnimatorSet animatorSet = new AnimatorSet();
            
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(likeButton, "scaleX", 1f, 1.3f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(likeButton, "scaleY", 1f, 1.3f, 1f);
            ObjectAnimator rotation = ObjectAnimator.ofFloat(likeButton, "rotation", 0f, 15f, -10f, 0f);
            
            animatorSet.playTogether(scaleX, scaleY, rotation);
            animatorSet.setDuration(DURATION_MEDIUM);
            animatorSet.setInterpolator(new OvershootInterpolator(1.5f));
            animatorSet.start();
        } else {
            // Simple scale animation for unlike
            bounceClick(likeButton);
        }
    }
    
    /**
     * Animate progress bar
     */
    public static void animateProgress(View progressBar, int fromProgress, int toProgress) {
        if (progressBar == null) return;
        
        ValueAnimator animator = ValueAnimator.ofInt(fromProgress, toProgress);
        animator.setDuration(DURATION_LONG);
        animator.setInterpolator(new DecelerateInterpolator());
        
        animator.addUpdateListener(animation -> {
            int progress = (int) animation.getAnimatedValue();
            // Update progress bar here if it's a ProgressBar
            // progressBar.setProgress(progress);
        });
        
        animator.start();
    }
    
    /**
     * Animate view group children with stagger
     */
    public static void animateViewGroupChildren(ViewGroup viewGroup) {
        if (viewGroup == null) return;
        
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child != null) {
                child.setAlpha(0f);
                child.setTranslationY(50f);
                
                ViewCompat.animate(child)
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(DURATION_MEDIUM)
                        .setStartDelay(i * DELAY_SHORT)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
            }
        }
    }
    
    /**
     * Create shared element transition name
     */
    public static String createTransitionName(String prefix, String id) {
        return prefix + "_" + id;
    }
    
    /**
     * Material Design elevation animation
     */
    public static void animateElevation(View view, float fromElevation, float toElevation) {
        if (view == null) return;
        
        ValueAnimator animator = ValueAnimator.ofFloat(fromElevation, toElevation);
        animator.setDuration(DURATION_SHORT);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        
        animator.addUpdateListener(animation -> {
            float elevation = (float) animation.getAnimatedValue();
            ViewCompat.setElevation(view, elevation);
        });
        
        animator.start();
    }
    
    /**
     * Animate color change
     */
    public static void animateColorChange(View view, int fromColor, int toColor) {
        if (view == null) return;
        
        ValueAnimator colorAnimator = ValueAnimator.ofArgb(fromColor, toColor);
        colorAnimator.setDuration(DURATION_MEDIUM);
        colorAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        
        colorAnimator.addUpdateListener(animation -> {
            int color = (int) animation.getAnimatedValue();
            view.setBackgroundColor(color);
        });
        
        colorAnimator.start();
    }
    
    /**
     * Check if animations are enabled on the device
     */
    public static boolean areAnimationsEnabled(Context context) {
        try {
            float animationScale = android.provider.Settings.Global.getFloat(
                    context.getContentResolver(),
                    android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
                    1.0f
            );
            return animationScale > 0.0f;
        } catch (Exception e) {
            return true; // Default to enabled
        }
    }
    
    /**
     * Get scaled animation duration based on system settings
     */
    public static int getScaledDuration(Context context, int baseDuration) {
        if (!areAnimationsEnabled(context)) {
            return 0;
        }
        
        try {
            float animationScale = android.provider.Settings.Global.getFloat(
                    context.getContentResolver(),
                    android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
                    1.0f
            );
            return (int) (baseDuration * animationScale);
        } catch (Exception e) {
            return baseDuration;
        }
    }
}