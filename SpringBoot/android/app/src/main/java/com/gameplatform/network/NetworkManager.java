package com.gameplatform.network;

import android.content.Context;
import android.util.Log;

import com.gameplatform.util.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Enhanced network manager for handling HTTP requests and Retrofit configuration
 * Requirements: 1.1, 2.1, 3.2, 4.1, 5.1
 */
public class NetworkManager {
    
    private static final String TAG = "NetworkManager";
    private static NetworkManager instance;
    private ApiService apiService;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private Context context;
    
    private NetworkManager() {
        // Will be initialized when context is set
    }
    
    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }
    
    /**
     * Initialize network manager with Android context
     * @param context Android application context
     */
    public void initialize(Context context) {
        this.context = context.getApplicationContext();
        initializeNetworking();
    }
    
    private void initializeNetworking() {
        // Create custom Gson instance with date formatting
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        
        // Create OkHttpClient with interceptors and timeouts
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(NetworkConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(NetworkConfig.READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(NetworkConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor())
                .addInterceptor(new HeaderInterceptor())
                .addNetworkInterceptor(new CacheInterceptor())
                .addInterceptor(new RetryInterceptor());
        
        // Add HTTP logging interceptor for debugging
        if (android.util.Log.isLoggable(TAG, Log.DEBUG)) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(loggingInterceptor);
        }
        
        // Add cache if context is available
        if (context != null) {
            Cache cache = new Cache(new File(context.getCacheDir(), "http-cache"), NetworkConfig.CACHE_SIZE);
            httpClientBuilder.cache(cache);
        }
        
        okHttpClient = httpClientBuilder.build();
        
        // Create Retrofit instance
        retrofit = new Retrofit.Builder()
                .baseUrl(NetworkConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        
        // Create API service
        apiService = retrofit.create(ApiService.class);
    }
    
    public ApiService getApiService() {
        if (apiService == null) {
            throw new IllegalStateException("NetworkManager not initialized. Call initialize(context) first.");
        }
        return apiService;
    }
    
    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
    
    public Retrofit getRetrofit() {
        return retrofit;
    }
    
    /**
     * Interceptor for adding authentication headers
     */
    private static class AuthInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            
            // Add authentication token if available
            String token = PreferenceManager.getInstance().getAuthToken();
            if (token != null && !token.isEmpty()) {
                Request authenticatedRequest = originalRequest.newBuilder()
                        .header(NetworkConfig.HEADER_AUTHORIZATION, "Bearer " + token)
                        .build();
                return chain.proceed(authenticatedRequest);
            }
            
            return chain.proceed(originalRequest);
        }
    }
    
    /**
     * Interceptor for adding common headers
     */
    private static class HeaderInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            
            Request requestWithHeaders = originalRequest.newBuilder()
                    .header(NetworkConfig.HEADER_CONTENT_TYPE, NetworkConfig.CONTENT_TYPE_JSON)
                    .header(NetworkConfig.HEADER_USER_AGENT, "GamePlatform-Android/1.0")
                    .header("Accept", NetworkConfig.CONTENT_TYPE_JSON)
                    .build();
            
            return chain.proceed(requestWithHeaders);
        }
    }
    
    /**
     * Interceptor for handling cache control
     */
    private static class CacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            
            // Cache successful GET requests
            if (request.method().equals("GET") && response.isSuccessful()) {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(NetworkConfig.CACHE_MAX_AGE, TimeUnit.SECONDS)
                        .maxStale(NetworkConfig.CACHE_MAX_STALE, TimeUnit.SECONDS)
                        .build();
                
                return response.newBuilder()
                        .header("Cache-Control", cacheControl.toString())
                        .build();
            }
            
            return response;
        }
    }
    
    /**
     * Interceptor for automatic retry on network failures
     */
    private static class RetryInterceptor implements Interceptor {
        private static final int MAX_RETRY_COUNT = 3;
        
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = null;
            IOException exception = null;
            
            for (int i = 0; i < MAX_RETRY_COUNT; i++) {
                try {
                    response = chain.proceed(request);
                    if (response.isSuccessful()) {
                        return response;
                    }
                    
                    // Don't retry on client errors (4xx)
                    if (response.code() >= 400 && response.code() < 500) {
                        return response;
                    }
                    
                    // Close the response body to avoid resource leaks
                    if (response.body() != null) {
                        response.body().close();
                    }
                    
                } catch (IOException e) {
                    exception = e;
                    Log.w(TAG, "Request failed, attempt " + (i + 1) + "/" + MAX_RETRY_COUNT, e);
                    
                    // Wait before retry (exponential backoff)
                    if (i < MAX_RETRY_COUNT - 1) {
                        try {
                            Thread.sleep((long) Math.pow(2, i) * 1000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new IOException("Request interrupted", ie);
                        }
                    }
                }
            }
            
            // If we get here, all retries failed
            if (response != null) {
                return response;
            } else if (exception != null) {
                throw exception;
            } else {
                throw new IOException("Unknown error occurred during request");
            }
        }
    }
}