package com.gameplatform.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

/**
 * Image loading utility class using Glide
 * Requirements: 1.3, 2.2, 6.2
 */
public class ImageUtil {
    
    private static final int DEFAULT_PLACEHOLDER = android.R.drawable.ic_menu_gallery;
    private static final int DEFAULT_ERROR = android.R.drawable.ic_menu_close_clear_cancel;
    
    /**
     * Load image into ImageView with default options
     * @param context Context
     * @param url Image URL
     * @param imageView Target ImageView
     */
    public static void loadImage(Context context, String url, ImageView imageView) {
        loadImage(context, url, imageView, DEFAULT_PLACEHOLDER, DEFAULT_ERROR);
    }
    
    /**
     * Load image into ImageView with custom placeholder and error images
     * @param context Context
     * @param url Image URL
     * @param imageView Target ImageView
     * @param placeholder Placeholder resource ID
     * @param error Error resource ID
     */
    public static void loadImage(Context context, String url, ImageView imageView, 
                                @DrawableRes int placeholder, @DrawableRes int error) {
        Glide.with(context)
                .load(url)
                .placeholder(placeholder)
                .error(error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
    
    /**
     * Load circular image (for avatars)
     * @param context Context
     * @param url Image URL
     * @param imageView Target ImageView
     */
    public static void loadCircularImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(DEFAULT_PLACEHOLDER)
                .error(DEFAULT_ERROR)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
    
    /**
     * Load rounded corner image
     * @param context Context
     * @param url Image URL
     * @param imageView Target ImageView
     * @param cornerRadius Corner radius in pixels
     */
    public static void loadRoundedImage(Context context, String url, ImageView imageView, int cornerRadius) {
        Glide.with(context)
                .load(url)
                .placeholder(DEFAULT_PLACEHOLDER)
                .error(DEFAULT_ERROR)
                .apply(RequestOptions.bitmapTransform(new CenterCrop(), new RoundedCorners(cornerRadius)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
    
    /**
     * Load image with loading callback
     * @param context Context
     * @param url Image URL
     * @param imageView Target ImageView
     * @param callback Loading callback
     */
    public static void loadImageWithCallback(Context context, String url, ImageView imageView, 
                                           ImageLoadCallback callback) {
        Glide.with(context)
                .load(url)
                .placeholder(DEFAULT_PLACEHOLDER)
                .error(DEFAULT_ERROR)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, 
                                              Target<Drawable> target, boolean isFirstResource) {
                        if (callback != null) {
                            callback.onLoadFailed(e != null ? e.getMessage() : "Unknown error");
                        }
                        return false;
                    }
                    
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, 
                                                 Target<Drawable> target, DataSource dataSource, 
                                                 boolean isFirstResource) {
                        if (callback != null) {
                            callback.onLoadSuccess();
                        }
                        return false;
                    }
                })
                .into(imageView);
    }
    
    /**
     * Load game icon with specific size
     * @param context Context
     * @param url Image URL
     * @param imageView Target ImageView
     * @param size Image size in pixels
     */
    public static void loadGameIcon(Context context, String url, ImageView imageView, int size) {
        Glide.with(context)
                .load(url)
                .placeholder(DEFAULT_PLACEHOLDER)
                .error(DEFAULT_ERROR)
                .override(size, size)
                .apply(RequestOptions.bitmapTransform(new CenterCrop(), new RoundedCorners(16)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
    
    /**
     * Load game cover image
     * @param context Context
     * @param url Image URL
     * @param imageView Target ImageView
     */
    public static void loadGameCover(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(DEFAULT_PLACEHOLDER)
                .error(DEFAULT_ERROR)
                .apply(RequestOptions.bitmapTransform(new CenterCrop()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
    
    /**
     * Clear image cache for specific URL
     * @param context Context
     * @param url Image URL
     */
    public static void clearImageCache(Context context, String url) {
        Glide.with(context).clear(Glide.with(context).load(url).into(new Target<Drawable>() {
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {}
            
            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {}
            
            @Override
            public void onResourceReady(Drawable resource, 
                                      com.bumptech.glide.request.transition.Transition<? super Drawable> transition) {}
            
            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {}
            
            @Override
            public void getSize(com.bumptech.glide.request.target.SizeReadyCallback cb) {
                cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
            }
            
            @Override
            public void removeCallback(com.bumptech.glide.request.target.SizeReadyCallback cb) {}
            
            @Override
            public void setRequest(@Nullable com.bumptech.glide.request.Request request) {}
            
            @Override
            public com.bumptech.glide.request.Request getRequest() {
                return null;
            }
            
            @Override
            public void onStart() {}
            
            @Override
            public void onStop() {}
            
            @Override
            public void onDestroy() {}
        }));
    }
    
    /**
     * Clear all image cache
     * @param context Context
     */
    public static void clearAllCache(Context context) {
        Glide.get(context).clearMemory();
        // Clear disk cache should be done on background thread
        new Thread(() -> Glide.get(context).clearDiskCache()).start();
    }
    
    /**
     * Preload image
     * @param context Context
     * @param url Image URL
     */
    public static void preloadImage(Context context, String url) {
        Glide.with(context)
                .load(url)
                .preload();
    }
    
    /**
     * Interface for image loading callbacks
     */
    public interface ImageLoadCallback {
        void onLoadSuccess();
        void onLoadFailed(String error);
    }
}