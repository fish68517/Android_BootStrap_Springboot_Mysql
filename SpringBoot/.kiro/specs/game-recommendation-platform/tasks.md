# Implementation Plan

- [x] 1. 配置项目基础设施





  - 更新pom.xml添加Spring Security依赖
  - 配置application.yml数据库连接和MyBatis-Plus设置
  - 创建项目包结构（controller, service, mapper, entity, dto, config, util）
  - _Requirements: All_

- [x] 2. 实现实体类和数据访问层





  - [x] 2.1 创建所有实体类


    - 创建User, Game, Comment, CommentLike, UserFavorite, Recommendation, Preference, UserPreference, WithdrawalRequest实体类
    - 使用MyBatis-Plus注解配置表映射
    - 使用Lombok简化代码
    - _Requirements: 1.1, 1.2, 2.1, 3.1, 4.1, 5.1, 6.1, 7.1, 8.1, 9.1, 10.1, 11.1, 12.1_

  - [x] 2.2 创建所有Mapper接口


    - 创建UserMapper, GameMapper, CommentMapper, CommentLikeMapper, UserFavoriteMapper, RecommendationMapper, PreferenceMapper, UserPreferenceMapper, WithdrawalRequestMapper
    - 继承BaseMapper获取基础CRUD方法
    - 添加自定义查询方法
    - _Requirements: 1.1, 1.2, 2.1, 3.1, 4.1, 5.1, 6.1, 7.1, 8.1, 9.1, 10.1, 11.1, 12.1_

- [x] 3. 实现DTO和工具类






  - [x] 3.1 创建DTO类

    - 创建UserRegisterDTO, UserLoginDTO, GameSubmitDTO, CommentDTO
    - 添加必要的验证注解
    - _Requirements: 1.1, 1.2, 5.1, 8.1_


  - [x] 3.2 创建工具类


    - 创建PasswordUtil用于密码加密（BCrypt）
    - 创建ResponseUtil用于统一响应格式
    - _Requirements: 1.2, 1.4_

  - [x] 3.3 创建异常类


    - 创建BusinessException基类
    - 创建具体异常类（UserNotFoundException, UnauthorizedException等）
    - 创建GlobalExceptionHandler全局异常处理器
    - _Requirements: 1.5, 2.3, 6.3, 8.5_

- [x] 4. 实现用户管理功能




  - [x] 4.1 实现UserService


    - 实现用户注册方法（验证用户名唯一性，密码加密）
    - 实现用户登录方法（验证凭据）
    - 实现更新用户资料方法
    - 实现删除用户方法（级联删除关联数据）
    - 实现查询所有用户方法（管理员）
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 3.1, 3.2, 3.3, 3.4, 3.5_

  - [x] 4.2 实现UserController


    - 实现注册页面显示和注册处理
    - 实现登录页面显示和登录处理（创建session）
    - 实现退出登录（清除session）
    - 实现个人资料页面显示和更新
    - 实现账号删除功能
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 3.1, 3.2, 3.3, 3.4, 3.5_

  - [x] 4.3 创建用户相关Thymeleaf模板


    - 创建register.html注册页面
    - 创建login.html登录页面
    - 创建profile.html个人资料页面
    - 使用Bootstrap样式
    - _Requirements: 1.1, 1.2, 1.3, 3.1, 3.2, 3.3_

- [x] 5. 实现偏好管理功能





  - [x] 5.1 实现PreferenceService


    - 实现获取所有偏好选项方法
    - 实现设置用户偏好方法（批量插入）
    - 实现获取用户偏好方法
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_

  - [x] 5.2 创建偏好相关Controller和模板


    - 在UserController中添加偏好设置方法
    - 创建preferences.html偏好设置页面
    - 首次登录时提示设置偏好
    - _Requirements: 4.1, 4.2, 4.3, 4.4_

- [x] 6. 实现游戏发布和管理功能





  - [x] 6.1 实现GameService


    - 实现发布游戏方法（状态设为pending）
    - 实现修改游戏信息方法
    - 实现按状态查询游戏方法
    - 实现搜索游戏方法（按标题模糊查询）
    - 实现获取游戏详情方法
    - 实现管理员审核游戏方法（更新状态和审核信息）
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 6.1, 6.2, 6.3, 6.4, 6.5, 12.1, 12.2, 12.3, 12.4_

  - [x] 6.2 实现GameController


    - 实现游戏列表页面（支持搜索）
    - 实现游戏详情页面
    - 实现发布游戏页面和处理
    - 实现编辑游戏页面和处理
    - 实现"我的游戏"页面（发布者查看自己的游戏）
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 12.1, 12.2, 12.3, 12.4, 12.5_

  - [x] 6.3 创建游戏相关Thymeleaf模板


    - 创建list.html游戏列表页面（包含搜索框）
    - 创建detail.html游戏详情页面（显示图片、描述、评论）
    - 创建publish.html发布游戏页面
    - 创建my-games.html我的游戏页面
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 12.1, 12.3, 12.4, 12.5_

- [x] 7. 实现评论功能




  - [x] 7.1 实现CommentService


    - 实现发表评论方法（状态设为pending）
    - 实现删除评论方法（验证权限）
    - 实现管理员审核评论方法
    - 实现获取游戏已审核评论方法
    - 实现点赞评论方法（插入点赞记录）
    - 实现取消点赞方法（删除点赞记录）
    - 实现获取评论点赞数方法
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5, 9.1, 9.2, 9.3, 9.4, 9.5_

  - [x] 7.2 实现CommentController


    - 实现发表评论处理（POST请求）
    - 实现删除评论处理
    - 实现点赞评论处理（AJAX返回JSON）
    - 实现取消点赞处理（AJAX返回JSON）
    - _Requirements: 8.1, 8.2, 8.5, 9.1, 9.2, 9.3, 9.4_

  - [x] 7.3 在游戏详情页面集成评论功能


    - 在detail.html中添加评论列表显示
    - 添加评论发表表单
    - 添加点赞按钮（使用jQuery AJAX）
    - 显示评论点赞数
    - _Requirements: 8.1, 8.2, 9.1, 9.2, 9.3, 9.4, 12.5_

- [x] 8. 实现收藏功能





  - [x] 8.1 实现FavoriteService


    - 实现收藏游戏方法（插入收藏记录）
    - 实现取消收藏方法（删除收藏记录）
    - 实现获取用户收藏列表方法
    - 实现检查是否已收藏方法
    - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5_

  - [x] 8.2 实现FavoriteController


    - 实现添加收藏处理（AJAX返回JSON）
    - 实现取消收藏处理（AJAX返回JSON）
    - 实现收藏列表页面
    - _Requirements: 10.1, 10.2, 10.3_

  - [x] 8.3 创建收藏相关模板和前端交互


    - 创建list.html收藏列表页面
    - 在游戏详情页面添加收藏按钮
    - 在游戏列表页面显示收藏状态
    - 使用jQuery实现收藏/取消收藏交互
    - _Requirements: 10.1, 10.2, 10.3, 10.4_

- [x] 9. 实现推荐功能





  - [x] 9.1 实现RecommendationService


    - 实现管理员创建推荐方法
    - 实现获取用户推荐列表方法
    - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5_

  - [x] 9.2 在AdminController中添加推荐管理


    - 实现推荐管理页面显示
    - 实现创建推荐处理
    - _Requirements: 7.1, 7.2, 7.3_

  - [x] 9.3 创建推荐相关模板


    - 创建recommendations.html推荐管理页面
    - 在用户首页显示推荐列表
    - _Requirements: 7.3, 7.5_

- [x] 10. 实现撤回申请功能




  - [x] 10.1 实现WithdrawalService


    - 实现提交撤回申请方法
    - 实现获取待审核撤回申请方法
    - 实现管理员审核撤回方法（通过时删除游戏及关联数据）
    - _Requirements: 11.1, 11.2, 11.3, 11.4, 11.5_

  - [x] 10.2 在GameController中添加撤回申请功能


    - 在"我的游戏"页面添加撤回申请按钮
    - 实现撤回申请提交处理
    - _Requirements: 11.1, 11.2_

  - [x] 10.3 在AdminController中添加撤回审核功能


    - 实现待审核撤回列表页面
    - 实现审核撤回处理
    - _Requirements: 11.3, 11.4, 11.5_

  - [x] 10.4 创建撤回相关模板


    - 创建review-withdrawals.html撤回审核页面
    - 在my-games.html中添加撤回申请表单
    - _Requirements: 11.1, 11.2, 11.3, 11.5_

- [x] 11. 实现管理员后台功能




  - [x] 11.1 实现AdminController核心功能


    - 实现dashboard仪表板页面（集成提供的dashboard.html）
    - 实现待审核游戏列表和审核处理
    - 实现待审核评论列表和审核处理
    - 实现用户管理页面
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 3.4, 3.5, 6.1, 6.2, 6.3, 8.3, 8.4_

  - [x] 11.2 集成dashboard.html模板


    - 将提供的dashboard.html转换为Thymeleaf模板
    - 使用th:each渲染游戏轮播Banner
    - 集成搜索功能（th:action提交表单）
    - 显示推荐列表
    - 添加审核入口链接
    - _Requirements: 6.1, 6.2, 7.3, 12.1_

  - [x] 11.3 创建管理员审核相关模板


    - 创建review-games.html游戏审核页面
    - 创建review-comments.html评论审核页面
    - 创建users.html用户管理页面
    - 使用Bootstrap表格和按钮样式
    - _Requirements: 3.5, 6.1, 6.2, 6.3, 8.3, 8.4_

- [x] 12. 实现安全配置






  - [x] 12.1 配置Spring Security

    - 创建SecurityConfig配置类
    - 配置URL访问权限（公开、用户、发布者、管理员）
    - 配置登录和登出
    - 禁用CSRF（简化开发，生产环境需启用）
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_

  - [x] 12.2 实现基于Session的认证


    - 在UserController登录方法中创建session
    - 创建拦截器或过滤器验证session
    - 在Controller中从session获取当前用户
    - _Requirements: 1.4, 2.1, 2.2, 2.3_

- [x] 13. 创建首页和公共布局






  - [x] 13.1 创建公共布局组件

    - 创建header.html公共头部（导航栏）
    - 创建footer.html公共底部
    - 使用Thymeleaf fragment功能
    - _Requirements: 12.1, 12.2, 12.3_



  - [x] 13.2 创建首页





    - 创建index.html首页
    - 显示推荐游戏（已登录用户）
    - 显示热门游戏列表
    - 提供搜索入口
    - _Requirements: 7.3, 7.5, 12.1, 12.2, 12.3_

- [x] 14. 集成静态资源




  - [x] 14.1 配置静态资源目录


    - 将Bootstrap (sb-admin-2)相关CSS/JS文件放入static目录
    - 配置图片上传目录
    - 配置WebMvcConfig允许访问静态资源
    - _Requirements: All_


  - [x] 14.2 实现图片上传功能（可选）

    - 创建文件上传工具类
    - 在游戏发布页面添加图片上传功能
    - 保存图片到static/img目录
    - _Requirements: 5.2, 5.3_

- [ ] 15. 数据初始化和测试
  - [ ] 15.1 准备测试数据
    - 使用game_projct.sql初始化数据库
    - 验证所有表和数据正确导入
    - _Requirements: All_

  - [ ] 15.2 端到端功能测试
    - 测试用户注册和登录流程
    - 测试游戏发布和审核流程
    - 测试评论发布和审核流程
    - 测试收藏和点赞功能
    - 测试推荐功能
    - 测试撤回申请流程
    - 测试权限控制
    - _Requirements: All_
