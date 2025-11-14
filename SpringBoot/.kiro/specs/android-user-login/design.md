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

## UI界面设计

### 1. 主界面架构 (MainActivity)

#### 布局结构
```xml
<!-- activity_main.xml -->
- CoordinatorLayout
  - FrameLayout (Fragment容器)
  - BottomNavigationView (底部导航栏)
    - 游戏列表 (Home)
    - 我的收藏 (Favorites) 
    - 个人中心 (Profile)
```

#### 底部导航栏设计
- 采用Material Design风格
- 三个主要Tab：游戏列表、我的收藏、个人中心
- 选中状态使用主色调高亮
- 图标使用Material Icons

### 2. 登录注册界面 (AuthActivity)

#### 布局结构
```xml
<!-- activity_auth.xml -->
- LinearLayout (垂直布局)
  - ImageView (应用Logo)
  - TabLayout (登录/注册切换)
  - ViewPager2 (登录注册Fragment容器)
    - LoginFragment
      - EditText (手机号输入)
      - LinearLayout (验证码输入区域)
        - EditText (验证码输入)
        - Button (获取验证码)
      - Button (登录按钮)
    - RegisterFragment
      - EditText (手机号输入)
      - LinearLayout (验证码输入区域)
        - EditText (验证码输入)
        - Button (获取验证码)
      - EditText (昵称输入)
      - Button (注册按钮)
```

### 3. 游戏列表界面 (GameListFragment)

#### 布局结构
```xml
<!-- fragment_game_list.xml -->
- CoordinatorLayout
  - AppBarLayout
    - Toolbar (标题栏)
    - TabLayout (游戏分类)
  - SwipeRefreshLayout (下拉刷新)
    - RecyclerView (游戏列表)
  - FloatingActionButton (搜索按钮)
```

#### 游戏列表项布局
```xml
<!-- item_game.xml -->
- MaterialCardView
  - ConstraintLayout
    - ImageView (游戏图标，左侧，圆角)
    - TextView (游戏名称，右上)
    - RatingBar (游戏评分，右中)
    - TextView (游戏简介，右下)
    - ImageView (收藏状态图标，右上角)
```

### 4. 游戏详情界面 (GameDetailActivity)

#### 布局结构
```xml
<!-- activity_game_detail.xml -->
- CoordinatorLayout
  - AppBarLayout
    - CollapsingToolbarLayout
      - ImageView (游戏封面图)
      - Toolbar
  - NestedScrollView
    - LinearLayout (垂直布局)
      - MaterialCardView (游戏基本信息)
        - LinearLayout
          - ImageView (游戏图标)
          - LinearLayout (名称和评分)
          - MaterialButton (收藏按钮)
      - MaterialCardView (游戏详细描述)
      - MaterialCardView (评论区域)
        - TextView (评论标题)
        - TextInputLayout (评论输入框)
        - MaterialButton (发表评论)
        - RecyclerView (评论列表)
```

### 5. 我的收藏界面 (FavoriteFragment)

#### 布局结构
```xml
<!-- fragment_favorite.xml -->
- LinearLayout (垂直布局)
  - SwipeRefreshLayout
    - RecyclerView (收藏游戏列表)
  - LinearLayout (空状态布局)
    - ImageView (空状态图标)
    - TextView (空状态提示文字)
    - MaterialButton (去发现游戏按钮)
```

### 6. 个人中心界面 (ProfileFragment)

#### 布局结构
```xml
<!-- fragment_profile.xml -->
- ScrollView
  - LinearLayout (垂直布局)
    - MaterialCardView (用户信息区域)
      - LinearLayout
        - ImageView (用户头像，圆形，可点击)
        - LinearLayout (用户基本信息)
          - TextView (用户昵称)
          - TextView (手机号)
          - TextView (注册时间)
    - MaterialCardView (功能菜单区域)
      - LinearLayout (我的收藏)
      - LinearLayout (我的评论)
      - LinearLayout (设置)
      - LinearLayout (关于我们)
    - MaterialButton (退出登录按钮)
```

### 7. Material Design颜色规范

#### 主色调
```xml
<!-- colors.xml -->
<color name="primary_color">#1976D2</color>        <!-- Material Blue 700 -->
<color name="primary_variant">#1565C0</color>      <!-- Material Blue 800 -->
<color name="secondary_color">#03DAC6</color>      <!-- Material Teal 200 -->
<color name="secondary_variant">#018786</color>    <!-- Material Teal 700 -->

<!-- 背景色 -->
<color name="background">#FAFAFA</color>           <!-- Material Grey 50 -->
<color name="surface">#FFFFFF</color>              <!-- White -->
<color name="error">#B00020</color>                <!-- Material Red -->

<!-- 文字颜色 -->
<color name="on_primary">#FFFFFF</color>           <!-- White on Primary -->
<color name="on_secondary">#000000</color>         <!-- Black on Secondary -->
<color name="on_background">#000000</color>        <!-- Black on Background -->
<color name="on_surface">#000000</color>           <!-- Black on Surface -->
<color name="on_error">#FFFFFF</color>             <!-- White on Error -->

<!-- 文字层级颜色 -->
<color name="text_high_emphasis">#DE000000</color>  <!-- 87% Black -->
<color name="text_medium_emphasis">#99000000</color> <!-- 60% Black -->
<color name="text_disabled">#61000000</color>       <!-- 38% Black -->
```

#### 尺寸规范
```xml
<!-- dimens.xml -->
<!-- 间距 -->
<dimen name="spacing_xs">4dp</dimen>
<dimen name="spacing_sm">8dp</dimen>
<dimen name="spacing_md">16dp</dimen>
<dimen name="spacing_lg">24dp</dimen>
<dimen name="spacing_xl">32dp</dimen>

<!-- 字体大小 -->
<dimen name="text_headline1">96sp</dimen>
<dimen name="text_headline2">60sp</dimen>
<dimen name="text_headline3">48sp</dimen>
<dimen name="text_headline4">34sp</dimen>
<dimen name="text_headline5">24sp</dimen>
<dimen name="text_headline6">20sp</dimen>
<dimen name="text_subtitle1">16sp</dimen>
<dimen name="text_subtitle2">14sp</dimen>
<dimen name="text_body1">16sp</dimen>
<dimen name="text_body2">14sp</dimen>
<dimen name="text_button">14sp</dimen>
<dimen name="text_caption">12sp</dimen>
<dimen name="text_overline">10sp</dimen>

<!-- 组件尺寸 -->
<dimen name="button_height">48dp</dimen>
<dimen name="fab_size">56dp</dimen>
<dimen name="app_bar_height">56dp</dimen>
<dimen name="bottom_nav_height">56dp</dimen>
<dimen name="card_corner_radius">8dp</dimen>
<dimen name="avatar_size">48dp</dimen>
<dimen name="game_icon_size">64dp</dimen>
```

### 8. Material Design主题样式

#### 应用主题
```xml
<!-- styles.xml -->
<style name="AppTheme" parent="Theme.MaterialComponents.Light.NoActionBar">
    <item name="colorPrimary">@color/primary_color</item>
    <item name="colorPrimaryVariant">@color/primary_variant</item>
    <item name="colorSecondary">@color/secondary_color</item>
    <item name="colorSecondaryVariant">@color/secondary_variant</item>
    <item name="android:colorBackground">@color/background</item>
    <item name="colorSurface">@color/surface</item>
    <item name="colorError">@color/error</item>
    <item name="colorOnPrimary">@color/on_primary</item>
    <item name="colorOnSecondary">@color/on_secondary</item>
    <item name="colorOnBackground">@color/on_background</item>
    <item name="colorOnSurface">@color/on_surface</item>
    <item name="colorOnError">@color/on_error</item>
</style>

<!-- 按钮样式 -->
<style name="Widget.App.Button" parent="Widget.MaterialComponents.Button">
    <item name="android:layout_height">@dimen/button_height</item>
    <item name="android:textSize">@dimen/text_button</item>
    <item name="android:textAllCaps">false</item>
    <item name="cornerRadius">@dimen/card_corner_radius</item>
</style>

<!-- 卡片样式 -->
<style name="Widget.App.CardView" parent="Widget.MaterialComponents.CardView">
    <item name="cardCornerRadius">@dimen/card_corner_radius</item>
    <item name="cardElevation">4dp</item>
    <item name="android:layout_margin">@dimen/spacing_sm</item>
</style>

<!-- 输入框样式 -->
<style name="Widget.App.TextInputLayout" parent="Widget.MaterialComponents.TextInputLayout.OutlinedBox">
    <item name="boxCornerRadiusTopStart">@dimen/card_corner_radius</item>
    <item name="boxCornerRadiusTopEnd">@dimen/card_corner_radius</item>
    <item name="boxCornerRadiusBottomStart">@dimen/card_corner_radius</item>
    <item name="boxCornerRadiusBottomEnd">@dimen/card_corner_radius</item>
</style>
```

### 9. 界面交互设计

#### 动画效果
- 页面切换：使用Material Motion过渡动画
- 按钮点击：涟漪效果 (Ripple Effect)
- 列表滚动：平滑滚动和弹性效果
- 加载状态：Material Design进度指示器

#### 反馈机制
- 成功操作：Snackbar提示
- 错误提示：Material Design错误样式
- 加载状态：骨架屏或进度条
- 空状态：友好的空状态插图和提示

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