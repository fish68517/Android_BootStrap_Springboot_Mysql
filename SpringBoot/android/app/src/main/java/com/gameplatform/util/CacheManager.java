package com.gameplatform.util;

import android.content.Context;
import android.util.Log;
import android.util.LruCache;
import com.gameplatform.model.Game;
import com.gameplatform.model.User;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Cache Manager for optimizing data loading and app performance
 * Requirements: 2.3
 */
public class CacheManager {
    
    private static final String TAG = "CacheManager";
    private static CacheManager instance;
    
    // Memory cache sizes (in number of items)
    private static final int GAME_CACHE_SIZE = 100;
    private static final int USER_CACHE_SIZE = 50;
    
    // Cache expiration times (in milliseconds)
    private static final long GAME_CACHE_EXPIRY = 30 * 60 * 1000L; // 30 minutes
    private static final long USER_CACHE_EXPIRY = 60 * 60 * 1000L; // 1 hour
    
    // Memory caches
    private LruCache<String, CacheItem<Game>> gameCache;
    private LruCache<String, CacheItem<List<Game>>> gameListCache;
    private LruCache<String, CacheItem<User>> userCache;
    
    // Cache metadata
    private ConcurrentHashMap<String, Long> cacheTimestamps;
    
    // Background executor for cache operations
    private ExecutorService cacheExecutor;
    
    private Context context;
    
    private CacheManager(Context context) {
        this.context = context.getApplicationContext();
        initializeCaches();
        cacheExecutor = Executors.newSingleThreadExecutor();
        cacheTimestamps = new ConcurrentHashMap<>();
    }
    
    public static synchronized CacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new CacheManager(context);
        }
        return instance;
    }
    
    public static CacheManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CacheManager not initialized. Call getInstance(Context) first.");
        }
        return instance;
    }
    
    /**
     * Initialize memory caches
     */
    private void initializeCaches() {
        // Game cache
        gameCache = new LruCache<String, CacheItem<Game>>(GAME_CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, CacheItem<Game> value) {
                return 1; // Count each item as 1
            }
            
            @Override
            protected void entryRemoved(boolean evicted, String key, 
                                      CacheItem<Game> oldValue, CacheItem<Game> newValue) {
                if (evicted) {
                    Log.d(TAG, "Game cache entry evicted: " + key);
                }
            }
        };
        
        // Game list cache
        gameListCache = new LruCache<String, CacheItem<List<Game>>>(20) {
            @Override
            protected int sizeOf(String key, CacheItem<List<Game>> value) {
                return value.data != null ? value.data.size() : 1;
            }
        };
        
        // User cache
        userCache = new LruCache<String, CacheItem<User>>(USER_CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, CacheItem<User> value) {
                return 1;
            }
        };
        
        Log.d(TAG, "Memory caches initialized");
    }
    
    // Game caching methods
    
    /**
     * Cache a single game
     */
    public void cacheGame(Game game) {
        if (game == null || game.getGameId() == null) return;
        
        cacheExecutor.execute(() -> {
            try {
                CacheItem<Game> cacheItem = new CacheItem<>(game, System.currentTimeMillis());
                gameCache.put(game.getGameId(), cacheItem);
                cacheTimestamps.put("game_" + game.getGameId(), System.currentTimeMillis());
                Log.d(TAG, "Game cached: " + game.getGameId());
            } catch (Exception e) {
                Log.e(TAG, "Error caching game", e);
            }
        });
    }
    
    /**
     * Get cached game
     */
    public Game getCachedGame(String gameId) {
        if (gameId == null) return null;
        
        try {
            CacheItem<Game> cacheItem = gameCache.get(gameId);
            if (cacheItem != null && !isCacheExpired(cacheItem.timestamp, GAME_CACHE_EXPIRY)) {
                Log.d(TAG, "Game cache hit: " + gameId);
                return cacheItem.data;
            } else if (cacheItem != null) {
                // Remove expired item
                gameCache.remove(gameId);
                cacheTimestamps.remove("game_" + gameId);
                Log.d(TAG, "Game cache expired: " + gameId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting cached game", e);
        }
        
        return null;
    }
    
    /**
     * Cache game list
     */
    public void cacheGameList(String key, List<Game> games) {
        if (key == null || games == null) return;
        
        cacheExecutor.execute(() -> {
            try {
                CacheItem<List<Game>> cacheItem = new CacheItem<>(games, System.currentTimeMillis());
                gameListCache.put(key, cacheItem);
                cacheTimestamps.put("gamelist_" + key, System.currentTimeMillis());
                
                // Also cache individual games
                for (Game game : games) {
                    if (game != null && game.getGameId() != null) {
                        CacheItem<Game> gameCacheItem = new CacheItem<>(game, System.currentTimeMillis());
                        gameCache.put(game.getGameId(), gameCacheItem);
                    }
                }
                
                Log.d(TAG, "Game list cached: " + key + " (" + games.size() + " items)");
            } catch (Exception e) {
                Log.e(TAG, "Error caching game list", e);
            }
        });
    }
    
    /**
     * Get cached game list
     */
    public List<Game> getCachedGameList(String key) {
        if (key == null) return null;
        
        try {
            CacheItem<List<Game>> cacheItem = gameListCache.get(key);
            if (cacheItem != null && !isCacheExpired(cacheItem.timestamp, GAME_CACHE_EXPIRY)) {
                Log.d(TAG, "Game list cache hit: " + key);
                return cacheItem.data;
            } else if (cacheItem != null) {
                // Remove expired item
                gameListCache.remove(key);
                cacheTimestamps.remove("gamelist_" + key);
                Log.d(TAG, "Game list cache expired: " + key);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting cached game list", e);
        }
        
        return null;
    }
    
    // User caching methods
    
    /**
     * Cache user data
     */
    public void cacheUser(User user) {
        if (user == null || user.getUserId() == null) return;
        
        cacheExecutor.execute(() -> {
            try {
                CacheItem<User> cacheItem = new CacheItem<>(user, System.currentTimeMillis());
                userCache.put(user.getUserId(), cacheItem);
                cacheTimestamps.put("user_" + user.getUserId(), System.currentTimeMillis());
                Log.d(TAG, "User cached: " + user.getUserId());
            } catch (Exception e) {
                Log.e(TAG, "Error caching user", e);
            }
        });
    }
    
    /**
     * Get cached user
     */
    public User getCachedUser(String userId) {
        if (userId == null) return null;
        
        try {
            CacheItem<User> cacheItem = userCache.get(userId);
            if (cacheItem != null && !isCacheExpired(cacheItem.timestamp, USER_CACHE_EXPIRY)) {
                Log.d(TAG, "User cache hit: " + userId);
                return cacheItem.data;
            } else if (cacheItem != null) {
                // Remove expired item
                userCache.remove(userId);
                cacheTimestamps.remove("user_" + userId);
                Log.d(TAG, "User cache expired: " + userId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting cached user", e);
        }
        
        return null;
    }
    
    // Cache management methods
    
    /**
     * Check if cache item is expired
     */
    private boolean isCacheExpired(long timestamp, long expiryTime) {
        return System.currentTimeMillis() - timestamp > expiryTime;
    }
    
    /**
     * Clear all caches
     */
    public void clearAllCaches() {
        cacheExecutor.execute(() -> {
            try {
                gameCache.evictAll();
                gameListCache.evictAll();
                userCache.evictAll();
                cacheTimestamps.clear();
                Log.d(TAG, "All caches cleared");
            } catch (Exception e) {
                Log.e(TAG, "Error clearing caches", e);
            }
        });
    }
    
    /**
     * Clear expired cache entries
     */
    public void clearExpiredCaches() {
        cacheExecutor.execute(() -> {
            try {
                long currentTime = System.currentTimeMillis();
                
                // Clear expired games
                for (String key : gameCache.snapshot().keySet()) {
                    CacheItem<Game> item = gameCache.get(key);
                    if (item != null && isCacheExpired(item.timestamp, GAME_CACHE_EXPIRY)) {
                        gameCache.remove(key);
                        cacheTimestamps.remove("game_" + key);
                    }
                }
                
                // Clear expired game lists
                for (String key : gameListCache.snapshot().keySet()) {
                    CacheItem<List<Game>> item = gameListCache.get(key);
                    if (item != null && isCacheExpired(item.timestamp, GAME_CACHE_EXPIRY)) {
                        gameListCache.remove(key);
                        cacheTimestamps.remove("gamelist_" + key);
                    }
                }
                
                // Clear expired users
                for (String key : userCache.snapshot().keySet()) {
                    CacheItem<User> item = userCache.get(key);
                    if (item != null && isCacheExpired(item.timestamp, USER_CACHE_EXPIRY)) {
                        userCache.remove(key);
                        cacheTimestamps.remove("user_" + key);
                    }
                }
                
                Log.d(TAG, "Expired caches cleared");
            } catch (Exception e) {
                Log.e(TAG, "Error clearing expired caches", e);
            }
        });
    }
    
    /**
     * Get cache statistics
     */
    public CacheStats getCacheStats() {
        CacheStats stats = new CacheStats();
        stats.gameCount = gameCache.size();
        stats.gameListCount = gameListCache.size();
        stats.userCount = userCache.size();
        stats.totalTimestamps = cacheTimestamps.size();
        return stats;
    }
    
    /**
     * Preload essential data for faster app startup
     */
    public void preloadEssentialData() {
        cacheExecutor.execute(() -> {
            try {
                Log.d(TAG, "Starting essential data preload");
                
                // Clear expired caches first
                clearExpiredCaches();
                
                // Preload current user data if logged in
                PreferenceManager prefManager = PreferenceManager.getInstance();
                if (prefManager.isLoggedIn()) {
                    User currentUser = prefManager.getCurrentUser();
                    if (currentUser != null) {
                        cacheUser(currentUser);
                    }
                }
                
                Log.d(TAG, "Essential data preload completed");
            } catch (Exception e) {
                Log.e(TAG, "Error during essential data preload", e);
            }
        });
    }
    
    /**
     * Shutdown cache manager
     */
    public void shutdown() {
        if (cacheExecutor != null && !cacheExecutor.isShutdown()) {
            cacheExecutor.shutdown();
        }
    }
    
    // Cache item wrapper class
    private static class CacheItem<T> {
        final T data;
        final long timestamp;
        
        CacheItem(T data, long timestamp) {
            this.data = data;
            this.timestamp = timestamp;
        }
    }
    
    // Cache statistics class
    public static class CacheStats {
        public int gameCount;
        public int gameListCount;
        public int userCount;
        public int totalTimestamps;
        
        @Override
        public String toString() {
            return "CacheStats{" +
                    "games=" + gameCount +
                    ", gameLists=" + gameListCount +
                    ", users=" + userCount +
                    ", timestamps=" + totalTimestamps +
                    '}';
        }
    }
}