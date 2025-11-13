# 设计文档

## 概述

Android游戏平台用户登录系统采用MVP架构模式，结合RESTful API设计，实现用户认证、游戏浏览、收藏管理、评论互动和个人中心功能。系统使用SharedPreferences进行本地数据存储，Retrofit进行网络请求，支持手机号验证码登录注册。

## 架构设计

### 整体架构
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Presentation  │    │    Business     │    │      Data       │
│     Layer       │    │     Layer       │    │     Layer       │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│   Activities    │◄──►│   Presenters    │◄──►│  Repositories   │
│   Fragments     │    │   Interactors   │    │   API Services  │
│   Adapters      │    │   Validators    │    │ Local Storage   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### MVP架构模式
- **Model**: 数据层，包括API服务、本地存储和数据模型
- **View**: 视图层，包括Activity、Fragment和自定义View
- **Presenter**: 业务逻辑层，处理用户交互和数据操作

## 组件和接口

### 1. 用户认证模块

#### AuthActivity
```java
public class AuthActivity extends AppCompatActivity implements AuthContract.View {
    // 登录注册界面，包含手机号输入、验证码输入
    // 支持登录和注册模式切换
}
```

#### AuthPresenter
```java
public class AuthPresenter implements AuthContract.Presenter {
    // 处理登录注册逻辑
    // 验证码发送和验证
    // 用户状态管理
}
```

#### AuthRepository
```java
public class AuthRepository {
    // 用户认证API调用
    // 本地登录状态存储
    // 验证码服务集成
}
```

### 2. 游戏列表模块

#### GameListActivity
```java
public class GameListActivity extends AppCompatActivity implements GameListContract.View {
    // 游戏列表展示
    // 下拉刷新和上拉加载
    // 分类筛选功能
}
```

#### GameListAdapter
```java
public class GameListAdapter extends RecyclerView.Adapter<GameViewHolder> {
    // 游戏列表项展示
    // 图片加载和缓存
    // 点击事件处理
}
```

#### GameRepository
```java
public class GameRepository {
    // 游戏数据API调用
    // 分页加载逻辑
    // 本地缓存管理
}
```

### 3. 游戏详情模块

#### GameDetailActivity
```java
public class GameDetailActivity extends AppCompatActivity implements GameDetailContract.View {
    // 游戏详细信息展示
    // 收藏功能
    // 评论列表展示
}
```

#### CommentAdapter
```java
public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
    // 评论列表展示
    // 点赞功能
    // 时间格式化
}
```

### 4. 收藏管理模块

#### FavoriteActivity
```java
public class FavoriteActivity extends AppCompatActivity implements FavoriteContract.View {
    // 收藏游戏列表
    // 取消收藏功能
    // 空状态处理
}
```

#### FavoriteRepository
```java
public class FavoriteRepository {
    // 收藏操作API
    // 收藏状态同步
    // 本地收藏缓存
}
```

### 5. 个人中心模块

#### ProfileActivity
```java
public class ProfileActivity extends AppCompatActivity implements ProfileContract.View {
    // 用户信息展示
    // 头像上传功能
    // 设置和退出登录
}
```

#### ImageUploadUtil
```java
public class ImageUploadUtil {
    // 图片选择和压缩
    // 头像上传处理
    // 权限管理
}
```

## 数据模型

### User Model
```java
public class User {
    private String userId;
    private String phoneNumber;
    private String nickname;
    private String avatar;
    private long registerTime;
    private boolean isLoggedIn;
}
```

### Game Model
```java
public class Game {
    private String gameId;
    private String name;
    private String icon;
    private String description;
    private float rating;
    private String category;
    private boolean isFavorited;
    private int commentCount;
}
```

### Comment Model
```java
public class Comment {
    private String commentId;
    private String gameId;
    private String userId;
    private String userNickname;
    private String userAvatar;
    private String content;
    private int likeCount;
    private boolean isLiked;
    private long createTime;
}
```

### API Response Model
```java
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private boolean success;
}
```

## 网络层设计

### API接口定义
```java
public interface ApiService {
    // 用户认证
    @POST("auth/send-code")
    Call<ApiResponse<Void>> sendVerificationCode(@Body SendCodeRequest request);
    
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);
    
    @POST("auth/register")
    Call<ApiResponse<RegisterResponse>> register(@Body RegisterRequest request);
    
    // 游戏相关
    @GET("games")
    Call<ApiResponse<GameListResponse>> getGameList(@Query("page") int page, @Query("category") String category);
    
    @GET("games/{gameId}")
    Call<ApiResponse<Game>> getGameDetail(@Path("gameId") String gameId);
    
    // 收藏相关
    @POST("favorites")
    Call<ApiResponse<Void>> addFavorite(@Body FavoriteRequest request);
    
    @DELETE("favorites/{gameId}")
    Call<ApiResponse<Void>> removeFavorite(@Path("gameId") String gameId);
    
    @GET("favorites")
    Call<ApiResponse<List<Game>>> getFavoriteList();
    
    // 评论相关
    @GET("games/{gameId}/comments")
    Call<ApiResponse<List<Comment>>> getComments(@Path("gameId") String gameId);
    
    @POST("comments")
    Call<ApiResponse<Comment>> addComment(@Body CommentRequest request);
    
    @POST("comments/{commentId}/like")
    Call<ApiResponse<Void>> likeComment(@Path("commentId") String commentId);
    
    // 用户相关
    @GET("user/profile")
    Call<ApiResponse<User>> getUserProfile();
    
    @PUT("user/profile")
    Call<ApiResponse<User>> updateProfile(@Body UpdateProfileRequest request);
    
    @POST("user/avatar")
    Call<ApiResponse<String>> uploadAvatar(@Part MultipartBody.Part avatar);
}
```

### 网络配置
```java
public class NetworkConfig {
    public static final String BASE_URL = "https://api.gameplatform.com/";
    public static final int CONNECT_TIMEOUT = 30;
    public static final int READ_TIMEOUT = 30;
    public static final int WRITE_TIMEOUT = 30;
}
```

## 本地存储设计

### SharedPreferences存储
```java
public class PreferenceManager {
    // 用户登录状态
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_AVATAR = "avatar";
    private static final String KEY_LOGIN_TIME = "login_time";
    
    // 应用设置
    private static final String KEY_AUTO_LOGIN = "auto_login";
    private static final String KEY_NOTIFICATION_ENABLED = "notification_enabled";
}
```

### 缓存策略
- 游戏列表：内存缓存30分钟，磁盘缓存24小时
- 用户信息：登录期间内存缓存，持久化存储
- 图片缓存：使用Glide进行图片缓存管理

## 错误处理

### 异常类型定义
```java
public class NetworkException extends Exception {
    // 网络连接异常
}

public class AuthException extends Exception {
    // 认证失败异常
}

public class ValidationException extends Exception {
    // 数据验证异常
}
```

### 错误处理策略
1. **网络错误**: 显示重试按钮，支持手动重试
2. **认证错误**: 自动跳转到登录页面
3. **数据验证错误**: 显示具体错误信息
4. **服务器错误**: 显示友好的错误提示

## UI界面 设计
1. 主页MainActivity + 底部导航栏  +  列表Fragment
2. 采用 Google Material Design 风格, 界面美观
3. 颜色采用 Material Design 颜色

## 性能优化

### 内存优化
- 使用ViewHolder模式优化RecyclerView
- 及时释放不需要的对象引用
- 使用弱引用避免内存泄漏

### 网络优化
- 实现请求缓存机制
- 使用连接池复用连接
- 支持请求取消和重试

### 图片优化
- 使用Glide进行图片加载和缓存
- 根据ImageView尺寸加载合适大小的图片
- 支持图片压缩和格式转换

### 启动优化
- 延迟初始化非关键组件
- 使用异步加载用户数据
- 优化布局层级减少渲染时间