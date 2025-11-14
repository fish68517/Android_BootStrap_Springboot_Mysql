package com.gameplatform.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Network status utility for monitoring connectivity and providing offline support
 * Requirements: 3.3, 4.4, 5.5
 */
public class NetworkStatusUtil {
    
    private static final String TAG = "NetworkStatusUtil";
    private static NetworkStatusUtil instance;
    
    private Context context;
    private ConnectivityManager connectivityManager;
    private MutableLiveData<Boolean> networkStatusLiveData;
    private MutableLiveData<NetworkType> networkTypeLiveData;
    private CopyOnWriteArrayList<NetworkStatusListener> listeners;
    
    private boolean isNetworkAvailable = false;
    private NetworkType currentNetworkType = NetworkType.NONE;
    
    // Network callback for API 24+
    private ConnectivityManager.NetworkCallback networkCallback;
    
    public enum NetworkType {
        NONE,
        WIFI,
        CELLULAR,
        ETHERNET,
        OTHER
    }
    
    public interface NetworkStatusListener {
        void onNetworkAvailable(NetworkType networkType);
        void onNetworkLost();
        void onNetworkChanged(NetworkType networkType);
    }
    
    private NetworkStatusUtil(Context context) {
        this.context = context.getApplicationContext();
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.networkStatusLiveData = new MutableLiveData<>();
        this.networkTypeLiveData = new MutableLiveData<>();
        this.listeners = new CopyOnWriteArrayList<>();
        
        initializeNetworkMonitoring();
        checkInitialNetworkStatus();
    }
    
    public static synchronized NetworkStatusUtil getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkStatusUtil(context);
        }
        return instance;
    }
    
    public static NetworkStatusUtil getInstance() {
        if (instance == null) {
            throw new IllegalStateException("NetworkStatusUtil not initialized. Call getInstance(Context) first.");
        }
        return instance;
    }
    
    /**
     * Initialize network monitoring based on API level
     */
    private void initializeNetworkMonitoring() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Use NetworkCallback for API 24+
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    Log.d(TAG, "Network available: " + network);
                    updateNetworkStatus(true);
                    updateNetworkType();
                    notifyNetworkAvailable();
                }
                
                @Override
                public void onLost(@NonNull Network network) {
                    Log.d(TAG, "Network lost: " + network);
                    updateNetworkStatus(false);
                    updateNetworkType();
                    notifyNetworkLost();
                }
                
                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    Log.d(TAG, "Network capabilities changed: " + network);
                    updateNetworkType();
                    notifyNetworkChanged();
                }
            };
            
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            connectivityManager.registerNetworkCallback(builder.build(), networkCallback);
        }
    }
    
    /**
     * Check initial network status
     */
    private void checkInitialNetworkStatus() {
        boolean isConnected = isNetworkConnected();
        updateNetworkStatus(isConnected);
        updateNetworkType();
        
        Log.d(TAG, "Initial network status - Connected: " + isConnected + ", Type: " + currentNetworkType);
    }
    
    /**
     * Check if network is currently connected
     */
    public boolean isNetworkConnected() {
        if (connectivityManager == null) return false;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork == null) return false;
            
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            return capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            );
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }
    
    /**
     * Get current network type
     */
    public NetworkType getNetworkType() {
        if (connectivityManager == null) return NetworkType.NONE;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork == null) return NetworkType.NONE;
            
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            if (capabilities == null) return NetworkType.NONE;
            
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return NetworkType.WIFI;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return NetworkType.CELLULAR;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return NetworkType.ETHERNET;
            } else {
                return NetworkType.OTHER;
            }
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                return NetworkType.NONE;
            }
            
            int type = activeNetworkInfo.getType();
            switch (type) {
                case ConnectivityManager.TYPE_WIFI:
                    return NetworkType.WIFI;
                case ConnectivityManager.TYPE_MOBILE:
                    return NetworkType.CELLULAR;
                case ConnectivityManager.TYPE_ETHERNET:
                    return NetworkType.ETHERNET;
                default:
                    return NetworkType.OTHER;
            }
        }
    }
    
    /**
     * Check if network is metered (limited data)
     */
    public boolean isNetworkMetered() {
        if (connectivityManager == null) return false;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return connectivityManager.isActiveNetworkMetered();
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && 
                   activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
    }
    
    /**
     * Get network strength (for cellular networks)
     */
    public int getNetworkStrength() {
        // This would require additional permissions and TelephonyManager
        // For now, return a default value
        return -1;
    }
    
    /**
     * Update network status
     */
    private void updateNetworkStatus(boolean isConnected) {
        if (isNetworkAvailable != isConnected) {
            isNetworkAvailable = isConnected;
            networkStatusLiveData.postValue(isConnected);
            Log.d(TAG, "Network status updated: " + isConnected);
        }
    }
    
    /**
     * Update network type
     */
    private void updateNetworkType() {
        NetworkType newType = getNetworkType();
        if (currentNetworkType != newType) {
            currentNetworkType = newType;
            networkTypeLiveData.postValue(newType);
            Log.d(TAG, "Network type updated: " + newType);
        }
    }
    
    /**
     * Notify listeners of network availability
     */
    private void notifyNetworkAvailable() {
        for (NetworkStatusListener listener : listeners) {
            try {
                listener.onNetworkAvailable(currentNetworkType);
            } catch (Exception e) {
                Log.e(TAG, "Error notifying network available", e);
            }
        }
    }
    
    /**
     * Notify listeners of network loss
     */
    private void notifyNetworkLost() {
        for (NetworkStatusListener listener : listeners) {
            try {
                listener.onNetworkLost();
            } catch (Exception e) {
                Log.e(TAG, "Error notifying network lost", e);
            }
        }
    }
    
    /**
     * Notify listeners of network change
     */
    private void notifyNetworkChanged() {
        for (NetworkStatusListener listener : listeners) {
            try {
                listener.onNetworkChanged(currentNetworkType);
            } catch (Exception e) {
                Log.e(TAG, "Error notifying network changed", e);
            }
        }
    }
    
    /**
     * Add network status listener
     */
    public void addNetworkStatusListener(NetworkStatusListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            Log.d(TAG, "Network status listener added");
        }
    }
    
    /**
     * Remove network status listener
     */
    public void removeNetworkStatusListener(NetworkStatusListener listener) {
        if (listener != null) {
            listeners.remove(listener);
            Log.d(TAG, "Network status listener removed");
        }
    }
    
    /**
     * Get network status LiveData
     */
    public LiveData<Boolean> getNetworkStatusLiveData() {
        return networkStatusLiveData;
    }
    
    /**
     * Get network type LiveData
     */
    public LiveData<NetworkType> getNetworkTypeLiveData() {
        return networkTypeLiveData;
    }
    
    /**
     * Get network status description
     */
    public String getNetworkStatusDescription() {
        if (!isNetworkAvailable) {
            return "无网络连接";
        }
        
        switch (currentNetworkType) {
            case WIFI:
                return "WiFi连接";
            case CELLULAR:
                return isNetworkMetered() ? "移动网络 (计费)" : "移动网络";
            case ETHERNET:
                return "以太网连接";
            case OTHER:
                return "其他网络";
            default:
                return "无网络连接";
        }
    }
    
    /**
     * Check if network is suitable for large downloads
     */
    public boolean isNetworkSuitableForLargeDownloads() {
        return isNetworkAvailable && 
               (currentNetworkType == NetworkType.WIFI || 
                currentNetworkType == NetworkType.ETHERNET);
    }
    
    /**
     * Check if should show data usage warning
     */
    public boolean shouldShowDataUsageWarning() {
        return isNetworkAvailable && 
               currentNetworkType == NetworkType.CELLULAR && 
               isNetworkMetered();
    }
    
    /**
     * Force refresh network status
     */
    public void refreshNetworkStatus() {
        boolean isConnected = isNetworkConnected();
        updateNetworkStatus(isConnected);
        updateNetworkType();
        
        Log.d(TAG, "Network status refreshed - Connected: " + isConnected + ", Type: " + currentNetworkType);
    }
    
    /**
     * Cleanup resources
     */
    public void cleanup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && networkCallback != null) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (Exception e) {
                Log.e(TAG, "Error unregistering network callback", e);
            }
        }
        
        listeners.clear();
        Log.d(TAG, "NetworkStatusUtil cleaned up");
    }
    
    /**
     * Get network info for debugging
     */
    public NetworkInfo getNetworkInfo() {
        try {
            return connectivityManager.getActiveNetworkInfo();
        } catch (Exception e) {
            Log.e(TAG, "Error getting network info", e);
            return null;
        }
    }
    
    /**
     * Log network status details
     */
    public void logNetworkStatus() {
        Log.i(TAG, "=== Network Status ===");
        Log.i(TAG, "Connected: " + isNetworkAvailable);
        Log.i(TAG, "Type: " + currentNetworkType);
        Log.i(TAG, "Description: " + getNetworkStatusDescription());
        Log.i(TAG, "Metered: " + isNetworkMetered());
        Log.i(TAG, "Suitable for downloads: " + isNetworkSuitableForLargeDownloads());
        Log.i(TAG, "===================");
    }
}