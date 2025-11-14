package com.gameplatform.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * File operation utility class
 * Requirements: 1.3, 6.2
 */
public class FileUtil {
    
    private static final String TAG = "FileUtil";
    private static final String IMAGE_DIR = "images";
    private static final String CACHE_DIR = "cache";
    private static final String TEMP_DIR = "temp";
    
    // Image compression quality
    private static final int COMPRESS_QUALITY = 80;
    private static final int MAX_IMAGE_SIZE = 1024; // Max width/height in pixels
    
    /**
     * Get app's external files directory
     * @param context Context
     * @return External files directory
     */
    public static File getExternalFilesDir(Context context) {
        return context.getExternalFilesDir(null);
    }
    
    /**
     * Get app's cache directory
     * @param context Context
     * @return Cache directory
     */
    public static File getCacheDir(Context context) {
        return context.getCacheDir();
    }
    
    /**
     * Get images directory
     * @param context Context
     * @return Images directory
     */
    public static File getImagesDir(Context context) {
        File imagesDir = new File(getExternalFilesDir(context), IMAGE_DIR);
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }
        return imagesDir;
    }
    
    /**
     * Get cache directory for specific type
     * @param context Context
     * @return Cache directory
     */
    public static File getCacheDir(Context context, String type) {
        File cacheDir = new File(getCacheDir(context), type);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }
    
    /**
     * Get temporary directory
     * @param context Context
     * @return Temporary directory
     */
    public static File getTempDir(Context context) {
        File tempDir = new File(getCacheDir(context), TEMP_DIR);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        return tempDir;
    }
    
    /**
     * Generate unique file name with timestamp
     * @param prefix File name prefix
     * @param extension File extension (without dot)
     * @return Unique file name
     */
    public static String generateUniqueFileName(String prefix, String extension) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        return prefix + "_" + timestamp + "." + extension;
    }
    
    /**
     * Create image file in images directory
     * @param context Context
     * @param fileName File name
     * @return Created file
     */
    public static File createImageFile(Context context, String fileName) {
        return new File(getImagesDir(context), fileName);
    }
    
    /**
     * Create temporary image file
     * @param context Context
     * @return Created temporary file
     */
    public static File createTempImageFile(Context context) {
        String fileName = generateUniqueFileName("temp_image", "jpg");
        return new File(getTempDir(context), fileName);
    }
    
    /**
     * Copy file from source to destination
     * @param source Source file
     * @param destination Destination file
     * @return true if successful, false otherwise
     */
    public static boolean copyFile(File source, File destination) {
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(destination)) {
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error copying file", e);
            return false;
        }
    }
    
    /**
     * Delete file
     * @param file File to delete
     * @return true if successful, false otherwise
     */
    public static boolean deleteFile(File file) {
        if (file != null && file.exists()) {
            return file.delete();
        }
        return false;
    }
    
    /**
     * Delete directory and all its contents
     * @param directory Directory to delete
     * @return true if successful, false otherwise
     */
    public static boolean deleteDirectory(File directory) {
        if (directory != null && directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            return directory.delete();
        }
        return false;
    }
    
    /**
     * Get file size in bytes
     * @param file File to check
     * @return File size in bytes
     */
    public static long getFileSize(File file) {
        if (file != null && file.exists()) {
            return file.length();
        }
        return 0;
    }
    
    /**
     * Format file size for display
     * @param bytes File size in bytes
     * @return Formatted file size string
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format(Locale.getDefault(), "%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format(Locale.getDefault(), "%.1f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format(Locale.getDefault(), "%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * Check if external storage is available for writing
     * @return true if available, false otherwise
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    
    /**
     * Check if external storage is available for reading
     * @return true if available, false otherwise
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
               Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
    
    /**
     * Compress and save bitmap to file
     * @param bitmap Bitmap to compress
     * @param file Output file
     * @param quality Compression quality (0-100)
     * @return true if successful, false otherwise
     */
    public static boolean saveBitmapToFile(Bitmap bitmap, File file, int quality) {
        try (FileOutputStream out = new FileOutputStream(file)) {
            return bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
        } catch (IOException e) {
            Log.e(TAG, "Error saving bitmap to file", e);
            return false;
        }
    }
    
    /**
     * Compress and save bitmap to file with default quality
     * @param bitmap Bitmap to compress
     * @param file Output file
     * @return true if successful, false otherwise
     */
    public static boolean saveBitmapToFile(Bitmap bitmap, File file) {
        return saveBitmapToFile(bitmap, file, COMPRESS_QUALITY);
    }
    
    /**
     * Load bitmap from file with size constraints
     * @param file Image file
     * @param maxWidth Maximum width
     * @param maxHeight Maximum height
     * @return Loaded bitmap or null if failed
     */
    public static Bitmap loadBitmapFromFile(File file, int maxWidth, int maxHeight) {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
            
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        } catch (Exception e) {
            Log.e(TAG, "Error loading bitmap from file", e);
            return null;
        }
    }
    
    /**
     * Load bitmap from file with default size constraints
     * @param file Image file
     * @return Loaded bitmap or null if failed
     */
    public static Bitmap loadBitmapFromFile(File file) {
        return loadBitmapFromFile(file, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE);
    }
    
    /**
     * Calculate sample size for bitmap loading
     * @param options BitmapFactory options
     * @param reqWidth Required width
     * @param reqHeight Required height
     * @return Sample size
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        
        return inSampleSize;
    }
    
    /**
     * Get file extension from file name
     * @param fileName File name
     * @return File extension (without dot) or empty string
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        
        return "";
    }
    
    /**
     * Check if file is an image based on extension
     * @param fileName File name
     * @return true if image file, false otherwise
     */
    public static boolean isImageFile(String fileName) {
        String extension = getFileExtension(fileName);
        return extension.equals("jpg") || extension.equals("jpeg") || 
               extension.equals("png") || extension.equals("gif") || 
               extension.equals("bmp") || extension.equals("webp");
    }
    
    /**
     * Clean up temporary files older than specified time
     * @param context Context
     * @param maxAgeMillis Maximum age in milliseconds
     */
    public static void cleanupTempFiles(Context context, long maxAgeMillis) {
        File tempDir = getTempDir(context);
        if (tempDir.exists()) {
            File[] files = tempDir.listFiles();
            if (files != null) {
                long currentTime = System.currentTimeMillis();
                for (File file : files) {
                    if (currentTime - file.lastModified() > maxAgeMillis) {
                        deleteFile(file);
                    }
                }
            }
        }
    }
    
    /**
     * Clean up cache files to free space
     * @param context Context
     * @param maxCacheSize Maximum cache size in bytes
     */
    public static void cleanupCache(Context context, long maxCacheSize) {
        File cacheDir = getCacheDir(context);
        long currentSize = getDirSize(cacheDir);
        
        if (currentSize > maxCacheSize) {
            // Delete oldest files first
            File[] files = cacheDir.listFiles();
            if (files != null) {
                // Sort by last modified time
                java.util.Arrays.sort(files, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));
                
                for (File file : files) {
                    if (currentSize <= maxCacheSize) {
                        break;
                    }
                    long fileSize = getFileSize(file);
                    if (deleteFile(file)) {
                        currentSize -= fileSize;
                    }
                }
            }
        }
    }
    
    /**
     * Get directory size recursively
     * @param directory Directory to measure
     * @return Total size in bytes
     */
    private static long getDirSize(File directory) {
        long size = 0;
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        size += getDirSize(file);
                    } else {
                        size += file.length();
                    }
                }
            }
        }
        return size;
    }
}