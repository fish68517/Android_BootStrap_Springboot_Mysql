package com.gameplatform.util;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import com.gameplatform.R;

/**
 * Skeleton screen utility for better loading states
 * Requirements: 3.3, 4.4, 5.5
 */
public class SkeletonUtil {
    
    private static final String TAG = "SkeletonUtil";
    
    /**
     * Create skeleton view for a given layout
     */
    public static SkeletonView createSkeletonView(Context context, ViewGroup parent) {
        SkeletonView skeletonView = new SkeletonView(context);
        
        // Match parent dimensions
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        );
        skeletonView.setLayoutParams(layoutParams);
        
        return skeletonView;
    }
    
    /**
     * Show skeleton loading for a view group
     */
    public static void showSkeleton(ViewGroup viewGroup) {
        if (viewGroup == null) return;
        
        // Hide original content
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (!(child instanceof SkeletonView)) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        
        // Add skeleton view if not exists
        SkeletonView skeletonView = findSkeletonView(viewGroup);
        if (skeletonView == null) {
            skeletonView = createSkeletonView(viewGroup.getContext(), viewGroup);
            viewGroup.addView(skeletonView);
        }
        
        skeletonView.setVisibility(View.VISIBLE);
        skeletonView.startAnimation();
    }
    
    /**
     * Hide skeleton loading
     */
    public static void hideSkeleton(ViewGroup viewGroup) {
        if (viewGroup == null) return;
        
        // Show original content
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof SkeletonView) {
                ((SkeletonView) child).stopAnimation();
                child.setVisibility(View.GONE);
            } else {
                child.setVisibility(View.VISIBLE);
            }
        }
    }
    
    /**
     * Find existing skeleton view in view group
     */
    private static SkeletonView findSkeletonView(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof SkeletonView) {
                return (SkeletonView) child;
            }
        }
        return null;
    }
    
    /**
     * Custom skeleton view with shimmer effect
     */
    public static class SkeletonView extends View {
        
        private Paint paint;
        private RectF rectF;
        private LinearGradient gradient;
        private ValueAnimator animator;
        
        private int baseColor;
        private int highlightColor;
        private float gradientX = 0f;
        
        public SkeletonView(Context context) {
            super(context);
            init(context);
        }
        
        public SkeletonView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }
        
        private void init(Context context) {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            rectF = new RectF();
            
            // Set skeleton colors
            baseColor = ContextCompat.getColor(context, R.color.skeleton_base);
            highlightColor = ContextCompat.getColor(context, R.color.skeleton_highlight);
            
            setupAnimator();
        }
        
        private void setupAnimator() {
            animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(1500);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);
            
            animator.addUpdateListener(animation -> {
                gradientX = (float) animation.getAnimatedValue();
                invalidate();
            });
        }
        
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            
            // Create gradient shader
            float gradientWidth = w * 0.3f;
            float startX = -gradientWidth + (w + gradientWidth) * gradientX;
            float endX = startX + gradientWidth;
            
            gradient = new LinearGradient(
                startX, 0, endX, 0,
                new int[]{baseColor, highlightColor, baseColor},
                new float[]{0f, 0.5f, 1f},
                Shader.TileMode.CLAMP
            );
            
            paint.setShader(gradient);
        }
        
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            
            if (getWidth() == 0 || getHeight() == 0) return;
            
            // Update gradient position
            float gradientWidth = getWidth() * 0.3f;
            float startX = -gradientWidth + (getWidth() + gradientWidth) * gradientX;
            float endX = startX + gradientWidth;
            
            gradient = new LinearGradient(
                startX, 0, endX, 0,
                new int[]{baseColor, highlightColor, baseColor},
                new float[]{0f, 0.5f, 1f},
                Shader.TileMode.CLAMP
            );
            
            paint.setShader(gradient);
            
            // Draw skeleton shapes
            drawSkeletonShapes(canvas);
        }
        
        private void drawSkeletonShapes(Canvas canvas) {
            float cornerRadius = 8f;
            float margin = 16f;
            float itemHeight = 80f;
            float spacing = 16f;
            
            int itemCount = (int) Math.ceil(getHeight() / (itemHeight + spacing));
            
            for (int i = 0; i < itemCount; i++) {
                float top = margin + i * (itemHeight + spacing);
                float bottom = top + itemHeight;
                
                if (bottom > getHeight()) break;
                
                // Draw item background
                rectF.set(margin, top, getWidth() - margin, bottom);
                canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
                
                // Draw avatar placeholder
                float avatarSize = 48f;
                float avatarTop = top + (itemHeight - avatarSize) / 2;
                rectF.set(margin + 16f, avatarTop, margin + 16f + avatarSize, avatarTop + avatarSize);
                canvas.drawRoundRect(rectF, avatarSize / 2, avatarSize / 2, paint);
                
                // Draw text lines
                float textLeft = margin + 16f + avatarSize + 16f;
                float textWidth = getWidth() - textLeft - margin - 16f;
                
                // Title line
                float titleHeight = 16f;
                float titleTop = top + 16f;
                rectF.set(textLeft, titleTop, textLeft + textWidth * 0.7f, titleTop + titleHeight);
                canvas.drawRoundRect(rectF, cornerRadius / 2, cornerRadius / 2, paint);
                
                // Subtitle line
                float subtitleHeight = 12f;
                float subtitleTop = titleTop + titleHeight + 8f;
                rectF.set(textLeft, subtitleTop, textLeft + textWidth * 0.5f, subtitleTop + subtitleHeight);
                canvas.drawRoundRect(rectF, cornerRadius / 2, cornerRadius / 2, paint);
            }
        }
        
        public void startAnimation() {
            if (animator != null && !animator.isRunning()) {
                animator.start();
            }
        }
        
        public void stopAnimation() {
            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }
        }
        
        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            stopAnimation();
        }
    }
    
    /**
     * Create skeleton for game list
     */
    public static class GameListSkeletonView extends SkeletonView {
        
        public GameListSkeletonView(Context context) {
            super(context);
        }
        
        @Override
        protected void onDraw(Canvas canvas) {
            if (getWidth() == 0 || getHeight() == 0) return;
            
            // Update gradient
            float gradientWidth = getWidth() * 0.3f;
            float startX = -gradientWidth + (getWidth() + gradientWidth) * gradientX;
            float endX = startX + gradientWidth;
            
            gradient = new LinearGradient(
                startX, 0, endX, 0,
                new int[]{baseColor, highlightColor, baseColor},
                new float[]{0f, 0.5f, 1f},
                Shader.TileMode.CLAMP
            );
            
            paint.setShader(gradient);
            
            drawGameListSkeleton(canvas);
        }
        
        private void drawGameListSkeleton(Canvas canvas) {
            float cornerRadius = 12f;
            float margin = 16f;
            float cardHeight = 120f;
            float spacing = 12f;
            
            int cardCount = (int) Math.ceil(getHeight() / (cardHeight + spacing));
            
            for (int i = 0; i < cardCount; i++) {
                float top = margin + i * (cardHeight + spacing);
                float bottom = top + cardHeight;
                
                if (bottom > getHeight()) break;
                
                // Draw card background
                rectF.set(margin, top, getWidth() - margin, bottom);
                canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
                
                // Draw game icon
                float iconSize = 80f;
                float iconTop = top + (cardHeight - iconSize) / 2;
                rectF.set(margin + 16f, iconTop, margin + 16f + iconSize, iconTop + iconSize);
                canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
                
                // Draw text content
                float textLeft = margin + 16f + iconSize + 16f;
                float textWidth = getWidth() - textLeft - margin - 32f;
                
                // Game title
                float titleHeight = 18f;
                float titleTop = top + 20f;
                rectF.set(textLeft, titleTop, textLeft + textWidth * 0.8f, titleTop + titleHeight);
                canvas.drawRoundRect(rectF, cornerRadius / 2, cornerRadius / 2, paint);
                
                // Rating bar
                float ratingTop = titleTop + titleHeight + 8f;
                float ratingHeight = 12f;
                rectF.set(textLeft, ratingTop, textLeft + textWidth * 0.4f, ratingTop + ratingHeight);
                canvas.drawRoundRect(rectF, cornerRadius / 2, cornerRadius / 2, paint);
                
                // Description
                float descTop = ratingTop + ratingHeight + 8f;
                float descHeight = 14f;
                rectF.set(textLeft, descTop, textLeft + textWidth * 0.9f, descTop + descHeight);
                canvas.drawRoundRect(rectF, cornerRadius / 2, cornerRadius / 2, paint);
                
                rectF.set(textLeft, descTop + descHeight + 4f, textLeft + textWidth * 0.6f, descTop + descHeight * 2 + 4f);
                canvas.drawRoundRect(rectF, cornerRadius / 2, cornerRadius / 2, paint);
            }
        }
    }
}