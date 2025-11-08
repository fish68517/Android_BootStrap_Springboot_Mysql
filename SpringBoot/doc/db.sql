-- 设置客户端连接编码
SET NAMES utf8mb4;

-- ----------------------------
-- 1. 用户表 (users)
-- 对应需求: 账号, 用户分类, 账号管理
-- ----------------------------
DROP TABLE IF EXISTS `comment_likes`;
DROP TABLE IF EXISTS `comments`;
DROP TABLE IF EXISTS `user_favorites`;
DROP TABLE IF EXISTS `withdrawal_requests`;
DROP TABLE IF EXISTS `recommendations`;
DROP TABLE IF EXISTS `games`;
DROP TABLE IF EXISTS `user_preferences`;
DROP TABLE IF EXISTS `preferences`;
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `user_id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(100) NOT NULL UNIQUE COMMENT '账号（手机或邮箱）',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '加密后的密码',
  `nickname` VARCHAR(50) COMMENT '用户昵称',
  `role` ENUM('user', 'publisher', 'admin') NOT NULL DEFAULT 'user' COMMENT '用户分类',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';


-- ----------------------------
-- 2. 偏好表 (preferences)
-- 对应需求: 用户偏好收集 (定义可选的偏好)
-- ----------------------------
CREATE TABLE `preferences` (
  `preference_id` INT AUTO_INCREMENT PRIMARY KEY,
  `preference_name` VARCHAR(100) NOT NULL UNIQUE COMMENT '偏好名称 (如: RPG, 策略, 射击)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='偏好定义表';


-- ----------------------------
-- 3. 用户偏好关联表 (user_preferences)
-- 对应需求: 用户偏好收集 (存储用户的选择)
-- ----------------------------
CREATE TABLE `user_preferences` (
  `user_id` INT NOT NULL COMMENT '用户ID',
  `preference_id` INT NOT NULL COMMENT '偏好ID',
  PRIMARY KEY (`user_id`, `preference_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
  FOREIGN KEY (`preference_id`) REFERENCES `preferences`(`preference_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户偏好关联表';


-- ----------------------------
-- 4. 游戏表 (games)
-- 对应需求: 游戏发布管理, 修改游戏信息, 查看游戏信息
-- ----------------------------
CREATE TABLE `games` (
  `game_id` INT AUTO_INCREMENT PRIMARY KEY,
  `publisher_id` INT NOT NULL COMMENT '发布者ID (关联用户表)',
  `title` VARCHAR(255) NOT NULL COMMENT '游戏名称',
  `description` TEXT COMMENT '游戏信息描述',
  `status` ENUM('pending', 'approved', 'rejected', 'withdrawn') NOT NULL DEFAULT 'pending' COMMENT '审核状态',
  `submitted_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `reviewed_by_admin_id` INT NULL COMMENT '审核管理员ID',
  `reviewed_at` TIMESTAMP NULL COMMENT '审核时间',
  FOREIGN KEY (`publisher_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
  FOREIGN KEY (`reviewed_by_admin_id`) REFERENCES `users`(`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游戏信息表';


-- ----------------------------
-- 5. 游戏推荐表 (recommendations)
-- 对应需求: 推荐游戏 (管理员给用户的特定推荐)
-- ----------------------------
CREATE TABLE `recommendations` (
  `recommendation_id` INT AUTO_INCREMENT PRIMARY KEY,
  `admin_id` INT NOT NULL COMMENT '操作推荐的管理员ID',
  `user_id` INT NOT NULL COMMENT '被推荐的用户ID',
  `game_id` INT NOT NULL COMMENT '被推荐的游戏ID',
  `reason` TEXT COMMENT '推荐理由',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`admin_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
  FOREIGN KEY (`game_id`) REFERENCES `games`(`game_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员推荐表';


-- ----------------------------
-- 6. 游戏撤回审核表 (withdrawal_requests)
-- 对应需求: 撤回游戏推荐, 审核撤回信息
-- ----------------------------
CREATE TABLE `withdrawal_requests` (
  `request_id` INT AUTO_INCREMENT PRIMARY KEY,
  `game_id` INT NOT NULL COMMENT '申请撤回的游戏ID',
  `publisher_id` INT NOT NULL COMMENT '申请的发布者ID',
  `reason` TEXT NOT NULL COMMENT '结束游戏推荐信息 (撤回理由)',
  `status` ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'pending' COMMENT '撤回审核状态',
  `requested_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `reviewed_by_admin_id` INT NULL COMMENT '审核管理员ID',
  `reviewed_at` TIMESTAMP NULL COMMENT '审核时间',
  FOREIGN KEY (`game_id`) REFERENCES `games`(`game_id`) ON DELETE CASCADE,
  FOREIGN KEY (`publisher_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
  FOREIGN KEY (`reviewed_by_admin_id`) REFERENCES `users`(`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游戏撤回审核表';


-- ----------------------------
-- 7. 用户收藏表 (user_favorites)
-- 对应需求: 收藏游戏, 取消收藏游戏, 查看收藏游戏
-- ----------------------------
CREATE TABLE `user_favorites` (
  `user_id` INT NOT NULL COMMENT '用户ID',
  `game_id` INT NOT NULL COMMENT '游戏ID',
  `favorited_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`user_id`, `game_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
  FOREIGN KEY (`game_id`) REFERENCES `games`(`game_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';


-- ----------------------------
-- 8. 评论表 (comments)
-- 对应需求: 发表评论, 查看评论, 审核评论, 删除评论
-- ----------------------------
CREATE TABLE `comments` (
  `comment_id` INT AUTO_INCREMENT PRIMARY KEY,
  `game_id` INT NOT NULL COMMENT '游戏ID',
  `user_id` INT NOT NULL COMMENT '发表评论的用户ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `status` ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'pending' COMMENT '评论审核状态',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '发表时间',
  `reviewed_by_admin_id` INT NULL COMMENT '审核管理员ID',
  FOREIGN KEY (`game_id`) REFERENCES `games`(`game_id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
  FOREIGN KEY (`reviewed_by_admin_id`) REFERENCES `users`(`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游戏评论表';


-- ----------------------------
-- 9. 评论点赞表 (comment_likes)
-- 对应需求: 点赞评论, 取消点赞评论, 查看点赞的评论
-- ----------------------------
CREATE TABLE `comment_likes` (
  `user_id` INT NOT NULL COMMENT '点赞用户ID',
  `comment_id` INT NOT NULL COMMENT '被点赞的评论ID',
  `liked_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`user_id`, `comment_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
  FOREIGN KEY (`comment_id`) REFERENCES `comments`(`comment_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论点赞表';


-- ----------------------------
-- 插入模拟数据
-- ----------------------------

-- 1. 插入用户 (管理员, 发布者, 普通用户)
INSERT INTO `users` (`username`, `password_hash`, `nickname`, `role`) VALUES
('admin@example.com', 'hashed_password_admin', '超级管理员', 'admin'),
('publisher_a@example.com', 'hashed_password_pub_a', '游戏厂商A', 'publisher'),
('publisher_b@example.com', 'hashed_password_pub_b', '游戏工作室B', 'publisher'),
('user_wang@example.com', 'hashed_password_user_wang', '玩家小王', 'user'),
('user_li@example.com', 'hashed_password_user_li', '玩家小李', 'user');

-- 2. 插入偏好定义
INSERT INTO `preferences` (`preference_name`) VALUES
('角色扮演 (RPG)'),
('策略 (Strategy)'),
('第一人称射击 (FPS)'),
('模拟经营 (Simulation)'),
('休闲 (Casual)');

-- 3. 插入用户偏好 (模拟用户回答问题后的结果)
-- 玩家小王 喜欢 RPG 和 策略
INSERT INTO `user_preferences` (`user_id`, `preference_id`) VALUES
(4, 1),
(4, 2);
-- 玩家小李 喜欢 休闲 和 模拟经营
INSERT INTO `user_preferences` (`user_id`, `preference_id`) VALUES
(5, 4),
(5, 5);

-- 4. 插入游戏 (不同状态)
-- 游戏厂商A 发布了3款游戏
INSERT INTO `games` (`publisher_id`, `title`, `description`, `status`, `reviewed_by_admin_id`) VALUES
(2, '星际远征', '一款科幻背景的策略游戏。', 'approved', 1),
(2, '秘境守护者', '一款奇幻RPG游戏，等待审核。', 'pending', NULL),
(2, '城市天际线MOD', '非官方作品，已被退回。', 'rejected', 1);
-- 游戏工作室B 发布了1款游戏
INSERT INTO `games` (`publisher_id`, `title`, `description`, `status`, `reviewed_by_admin_id`) VALUES
(3, '开心农场乐园', '休闲模拟经营游戏。', 'approved', 1);

-- 5. 插入管理员推荐
-- 管理员 推荐 '星际远征' 给喜欢策略的 '玩家小王'
INSERT INTO `recommendations` (`admin_id`, `user_id`, `game_id`, `reason`) VALUES
(1, 4, 1, '根据您的偏好，这款策略游戏很适合您。');

-- 6. 插入游戏撤回申请
-- 游戏厂商A 申请撤回 (删除) '城市天际线MOD' (虽然它已经被拒了，但流程上可以申请)
INSERT INTO `withdrawal_requests` (`game_id`, `publisher_id`, `reason`, `status`) VALUES
(3, 2, '内容涉及版权问题，申请撤回并删除数据。', 'pending');

-- 7. 插入用户收藏
-- 玩家小王 收藏了 '星际远征'
INSERT INTO `user_favorites` (`user_id`, `game_id`) VALUES
(4, 1);
-- 玩家小李 收藏了 '开心农场乐园'
INSERT INTO `user_favorites` (`user_id`, `game_id`) VALUES
(5, 4);

-- 8. 插入评论 (不同状态)
-- 玩家小王 评论 '星际远征' (已通过)
INSERT INTO `comments` (`game_id`, `user_id`, `content`, `status`, `reviewed_by_admin_id`) VALUES
(1, 4, '这款策略游戏太棒了，深度十足！', 'approved', 1);
-- 玩家小李 评论 '开心农场乐园' (待审核)
INSERT INTO `comments` (`game_id`, `user_id`, `content`, `status`) VALUES
(4, 5, '很休闲，适合打发时间。', 'pending');
-- 玩家小李 评论 '星际远征' (违规，被拒绝)
INSERT INTO `comments` (`game_id`, `user_id`, `content`, `status`, `reviewed_by_admin_id`) VALUES
(1, 5, '广告内容，xxx。', 'rejected', 1);

-- 9. 插入评论点赞
-- 玩家小李 点赞了 玩家小王 的评论 (ID为1的评论)
INSERT INTO `comment_likes` (`user_id`, `comment_id`) VALUES
(5, 1);