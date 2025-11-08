/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : game_projct

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 08/11/2025 14:20:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for comment_likes
-- ----------------------------
DROP TABLE IF EXISTS `comment_likes`;
CREATE TABLE `comment_likes`  (
  `user_id` int NOT NULL COMMENT '点赞用户ID',
  `comment_id` int NOT NULL COMMENT '被点赞的评论ID',
  `liked_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`user_id`, `comment_id`) USING BTREE,
  INDEX `comment_id`(`comment_id` ASC) USING BTREE,
  CONSTRAINT `comment_likes_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `comment_likes_ibfk_2` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`comment_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评论点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment_likes
-- ----------------------------
INSERT INTO `comment_likes` VALUES (5, 1, '2025-11-03 15:41:01');

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `game_id` int NOT NULL COMMENT '游戏ID',
  `user_id` int NOT NULL COMMENT '发表评论的用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `status` enum('pending','approved','rejected') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending' COMMENT '评论审核状态',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发表时间',
  `reviewed_by_admin_id` int NULL DEFAULT NULL COMMENT '审核管理员ID',
  PRIMARY KEY (`comment_id`) USING BTREE,
  INDEX `game_id`(`game_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `reviewed_by_admin_id`(`reviewed_by_admin_id` ASC) USING BTREE,
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`game_id`) REFERENCES `games` (`game_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `comments_ibfk_3` FOREIGN KEY (`reviewed_by_admin_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '游戏评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comments
-- ----------------------------
INSERT INTO `comments` VALUES (1, 1, 4, '这款策略游戏太棒了，深度十足！', 'approved', '2025-11-03 15:41:01', 1);
INSERT INTO `comments` VALUES (2, 4, 5, '很休闲，适合打发时间。', 'pending', '2025-11-03 15:41:01', NULL);
INSERT INTO `comments` VALUES (3, 1, 5, '广告内容，xxx。', 'rejected', '2025-11-03 15:41:01', 1);

-- ----------------------------
-- Table structure for games
-- ----------------------------
DROP TABLE IF EXISTS `games`;
CREATE TABLE `games`  (
  `game_id` int NOT NULL AUTO_INCREMENT,
  `publisher_id` int NOT NULL COMMENT '发布者ID (关联用户表)',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '游戏名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '游戏信息描述',
  `cover_image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '游戏封面图片URL',
  `other_image_urls` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '其他游戏图片URL列表, 用英文逗号 , 分隔',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `submitted_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `reviewed_by_admin_id` int NULL DEFAULT NULL COMMENT '审核管理员ID',
  `reviewed_at` timestamp NULL DEFAULT NULL COMMENT '审核时间',
  PRIMARY KEY (`game_id`) USING BTREE,
  INDEX `publisher_id`(`publisher_id` ASC) USING BTREE,
  INDEX `reviewed_by_admin_id`(`reviewed_by_admin_id` ASC) USING BTREE,
  CONSTRAINT `games_ibfk_1` FOREIGN KEY (`publisher_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `games_ibfk_2` FOREIGN KEY (`reviewed_by_admin_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '游戏信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of games
-- ----------------------------
INSERT INTO `games` VALUES (1, 2, '星际远征', '一款科幻背景的策略游戏。', '/image/xingji_cover.jpg', '/image/xingji_1.jpg,/image/xingji_2.jpg,/image/xingji_3.jpg', 'approved', '2025-11-08 13:43:41', 1, NULL);
INSERT INTO `games` VALUES (2, 2, '秘境守护者', '一款奇幻RPG游戏，等待审核。', '/image/mijing_cover.png', NULL, 'pending', '2025-11-08 13:43:41', NULL, NULL);
INSERT INTO `games` VALUES (3, 2, '城市天际线MOD', '非官方作品，已被退回。', NULL, NULL, 'rejected', '2025-11-08 13:43:41', 1, NULL);
INSERT INTO `games` VALUES (4, 3, '开心农场乐园', '休闲模拟经营游戏。', '/image/farm_cover.jpg', '/image/farm_1.jpg,/image/farm_2.jpg', 'approved', '2025-11-08 13:43:41', 1, NULL);

-- ----------------------------
-- Table structure for preferences
-- ----------------------------
DROP TABLE IF EXISTS `preferences`;
CREATE TABLE `preferences`  (
  `preference_id` int NOT NULL AUTO_INCREMENT,
  `preference_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '偏好名称 (如: RPG, 策略, 射击)',
  PRIMARY KEY (`preference_id`) USING BTREE,
  UNIQUE INDEX `preference_name`(`preference_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '偏好定义表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of preferences
-- ----------------------------
INSERT INTO `preferences` VALUES (5, '休闲 (Casual)');
INSERT INTO `preferences` VALUES (4, '模拟经营 (Simulation)');
INSERT INTO `preferences` VALUES (3, '第一人称射击 (FPS)');
INSERT INTO `preferences` VALUES (2, '策略 (Strategy)');
INSERT INTO `preferences` VALUES (1, '角色扮演 (RPG)');

-- ----------------------------
-- Table structure for recommendations
-- ----------------------------
DROP TABLE IF EXISTS `recommendations`;
CREATE TABLE `recommendations`  (
  `recommendation_id` int NOT NULL AUTO_INCREMENT,
  `admin_id` int NOT NULL COMMENT '操作推荐的管理员ID',
  `user_id` int NOT NULL COMMENT '被推荐的用户ID',
  `game_id` int NOT NULL COMMENT '被推荐的游戏ID',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '推荐理由',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`recommendation_id`) USING BTREE,
  INDEX `admin_id`(`admin_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `game_id`(`game_id` ASC) USING BTREE,
  CONSTRAINT `recommendations_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `recommendations_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `recommendations_ibfk_3` FOREIGN KEY (`game_id`) REFERENCES `games` (`game_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员推荐表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recommendations
-- ----------------------------
INSERT INTO `recommendations` VALUES (1, 1, 4, 1, '根据您的偏好，这款策略游戏很适合您。', '2025-11-03 15:41:01');
INSERT INTO `recommendations` VALUES (2, 1, 4, 2, '根据您的偏好，', '2025-11-08 13:26:16');

-- ----------------------------
-- Table structure for user_favorites
-- ----------------------------
DROP TABLE IF EXISTS `user_favorites`;
CREATE TABLE `user_favorites`  (
  `user_id` int NOT NULL COMMENT '用户ID',
  `game_id` int NOT NULL COMMENT '游戏ID',
  `favorited_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`user_id`, `game_id`) USING BTREE,
  INDEX `game_id`(`game_id` ASC) USING BTREE,
  CONSTRAINT `user_favorites_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_favorites_ibfk_2` FOREIGN KEY (`game_id`) REFERENCES `games` (`game_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_favorites
-- ----------------------------
INSERT INTO `user_favorites` VALUES (4, 1, '2025-11-03 15:41:01');
INSERT INTO `user_favorites` VALUES (5, 4, '2025-11-03 15:41:01');

-- ----------------------------
-- Table structure for user_preferences
-- ----------------------------
DROP TABLE IF EXISTS `user_preferences`;
CREATE TABLE `user_preferences`  (
  `user_id` int NOT NULL COMMENT '用户ID',
  `preference_id` int NOT NULL COMMENT '偏好ID',
  PRIMARY KEY (`user_id`, `preference_id`) USING BTREE,
  INDEX `preference_id`(`preference_id` ASC) USING BTREE,
  CONSTRAINT `user_preferences_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_preferences_ibfk_2` FOREIGN KEY (`preference_id`) REFERENCES `preferences` (`preference_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户偏好关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_preferences
-- ----------------------------
INSERT INTO `user_preferences` VALUES (4, 1);
INSERT INTO `user_preferences` VALUES (4, 2);
INSERT INTO `user_preferences` VALUES (5, 4);
INSERT INTO `user_preferences` VALUES (5, 5);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号（手机或邮箱）',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密后的密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `role` enum('user','publisher','admin') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '用户分类',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', 'admin', '超级管理员', 'admin', '2025-11-03 15:41:01');
INSERT INTO `users` VALUES (2, 'publisher_a@example.com', 'hashed_password_pub_a', '游戏厂商A', 'publisher', '2025-11-03 15:41:01');
INSERT INTO `users` VALUES (3, 'publisher_b@example.com', 'hashed_password_pub_b', '游戏工作室B', 'publisher', '2025-11-03 15:41:01');
INSERT INTO `users` VALUES (4, 'user_wang@example.com', 'hashed_password_user_wang', '玩家小王', 'user', '2025-11-03 15:41:01');
INSERT INTO `users` VALUES (5, 'user_li@example.com', 'hashed_password_user_li', '玩家小李', 'user', '2025-11-03 15:41:01');

-- ----------------------------
-- Table structure for withdrawal_requests
-- ----------------------------
DROP TABLE IF EXISTS `withdrawal_requests`;
CREATE TABLE `withdrawal_requests`  (
  `request_id` int NOT NULL AUTO_INCREMENT,
  `game_id` int NOT NULL COMMENT '申请撤回的游戏ID',
  `publisher_id` int NOT NULL COMMENT '申请的发布者ID',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '结束游戏推荐信息 (撤回理由)',
  `status` enum('pending','approved','rejected') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending' COMMENT '撤回审核状态',
  `requested_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `reviewed_by_admin_id` int NULL DEFAULT NULL COMMENT '审核管理员ID',
  `reviewed_at` timestamp NULL DEFAULT NULL COMMENT '审核时间',
  PRIMARY KEY (`request_id`) USING BTREE,
  INDEX `game_id`(`game_id` ASC) USING BTREE,
  INDEX `publisher_id`(`publisher_id` ASC) USING BTREE,
  INDEX `reviewed_by_admin_id`(`reviewed_by_admin_id` ASC) USING BTREE,
  CONSTRAINT `withdrawal_requests_ibfk_1` FOREIGN KEY (`game_id`) REFERENCES `games` (`game_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `withdrawal_requests_ibfk_2` FOREIGN KEY (`publisher_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `withdrawal_requests_ibfk_3` FOREIGN KEY (`reviewed_by_admin_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '游戏撤回审核表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of withdrawal_requests
-- ----------------------------
INSERT INTO `withdrawal_requests` VALUES (1, 3, 2, '内容涉及版权问题，申请撤回并删除数据。', 'pending', '2025-11-03 15:41:01', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
