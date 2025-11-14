package com.gameplatform.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Permission utility class for handling runtime permissions
 * Requirements: 1.3, 6.2
 */
public class PermissionUtil {
    
    // Permission request codes
    public static final int REQUEST_CODE_CAMERA = 1001;
    public static final int REQUEST_CODE_STORAGE = 1002;
    public static final int REQUEST_CODE_PHONE = 1003;
    public static final int REQUEST_CODE_LOCATION = 1004;
    public static final int REQUEST_CODE_MULTIPLE = 1005;
    
    // Common permissions
    public static final String[] CAMERA_PERMISSIONS = {
            Manifest.permission.CAMERA
    };
    
    public static final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    
    public static final String[] PHONE_PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE
    };
    
    public static final String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    
    /**
     * Check if a single permission is granted
     * @param context Context
     * @param permission Permission to check
     * @return true if granted, false otherwise
     */
    public static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * Check if all permissions in array are granted
     * @param context Context
     * @param permissions Permissions to check
     * @return true if all granted, false otherwise
     */
    public static boolean arePermissionsGranted(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (!isPermissionGranted(context, permission)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Get list of denied permissions from array
     * @param context Context
     * @param permissions Permissions to check
     * @return List of denied permissions
     */
    public static List<String> getDeniedPermissions(Context context, String[] permissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (!isPermissionGranted(context, permission)) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }
    
    /**
     * Request single permission
     * @param activity Activity
     * @param permission Permission to request
     * @param requestCode Request code
     */
    public static void requestPermission(Activity activity, String permission, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }
    
    /**
     * Request multiple permissions
     * @param activity Activity
     * @param permissions Permissions to request
     * @param requestCode Request code
     */
    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }
    
    /**
     * Request camera permission
     * @param activity Activity
     */
    public static void requestCameraPermission(Activity activity) {
        requestPermissions(activity, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
    }
    
    /**
     * Request storage permissions
     * @param activity Activity
     */
    public static void requestStoragePermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses different permissions
            String[] permissions = {
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
            };
            requestPermissions(activity, permissions, REQUEST_CODE_STORAGE);
        } else {
            requestPermissions(activity, STORAGE_PERMISSIONS, REQUEST_CODE_STORAGE);
        }
    }
    
    /**
     * Request phone permission
     * @param activity Activity
     */
    public static void requestPhonePermission(Activity activity) {
        requestPermissions(activity, PHONE_PERMISSIONS, REQUEST_CODE_PHONE);
    }
    
    /**
     * Request location permissions
     * @param activity Activity
     */
    public static void requestLocationPermissions(Activity activity) {
        requestPermissions(activity, LOCATION_PERMISSIONS, REQUEST_CODE_LOCATION);
    }
    
    /**
     * Check if camera permission is granted
     * @param context Context
     * @return true if granted, false otherwise
     */
    public static boolean isCameraPermissionGranted(Context context) {
        return arePermissionsGranted(context, CAMERA_PERMISSIONS);
    }
    
    /**
     * Check if storage permissions are granted
     * @param context Context
     * @return true if granted, false otherwise
     */
    public static boolean isStoragePermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] permissions = {
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
            };
            return arePermissionsGranted(context, permissions);
        } else {
            return arePermissionsGranted(context, STORAGE_PERMISSIONS);
        }
    }
    
    /**
     * Check if phone permission is granted
     * @param context Context
     * @return true if granted, false otherwise
     */
    public static boolean isPhonePermissionGranted(Context context) {
        return arePermissionsGranted(context, PHONE_PERMISSIONS);
    }
    
    /**
     * Check if location permissions are granted
     * @param context Context
     * @return true if granted, false otherwise
     */
    public static boolean isLocationPermissionGranted(Context context) {
        return arePermissionsGranted(context, LOCATION_PERMISSIONS);
    }
    
    /**
     * Check if user should show rationale for permission
     * @param activity Activity
     * @param permission Permission to check
     * @return true if should show rationale, false otherwise
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }
    
    /**
     * Check if user should show rationale for any of the permissions
     * @param activity Activity
     * @param permissions Permissions to check
     * @return true if should show rationale for any, false otherwise
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Handle permission request result
     * @param requestCode Request code
     * @param permissions Requested permissions
     * @param grantResults Grant results
     * @param callback Callback to handle result
     */
    public static void handlePermissionResult(int requestCode, String[] permissions, int[] grantResults, 
                                            PermissionCallback callback) {
        if (callback == null) return;
        
        boolean allGranted = true;
        List<String> deniedPermissions = new ArrayList<>();
        
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                deniedPermissions.add(permissions[i]);
            }
        }
        
        if (allGranted) {
            callback.onPermissionGranted(requestCode);
        } else {
            callback.onPermissionDenied(requestCode, deniedPermissions);
        }
    }
    
    /**
     * Get permission name for display
     * @param permission Permission constant
     * @return Human readable permission name
     */
    public static String getPermissionName(String permission) {
        switch (permission) {
            case Manifest.permission.CAMERA:
                return "相机";
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
            case Manifest.permission.READ_MEDIA_IMAGES:
            case Manifest.permission.READ_MEDIA_VIDEO:
                return "存储";
            case Manifest.permission.READ_PHONE_STATE:
                return "电话";
            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                return "位置";
            default:
                return "未知权限";
        }
    }
    
    /**
     * Get permission description for rationale dialog
     * @param permission Permission constant
     * @return Permission description
     */
    public static String getPermissionDescription(String permission) {
        switch (permission) {
            case Manifest.permission.CAMERA:
                return "需要相机权限来拍照和扫描";
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
            case Manifest.permission.READ_MEDIA_IMAGES:
            case Manifest.permission.READ_MEDIA_VIDEO:
                return "需要存储权限来保存和读取图片";
            case Manifest.permission.READ_PHONE_STATE:
                return "需要电话权限来获取设备信息";
            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                return "需要位置权限来提供基于位置的服务";
            default:
                return "应用需要此权限来正常运行";
        }
    }
    
    /**
     * Interface for permission request callbacks
     */
    public interface PermissionCallback {
        void onPermissionGranted(int requestCode);
        void onPermissionDenied(int requestCode, List<String> deniedPermissions);
    }
}