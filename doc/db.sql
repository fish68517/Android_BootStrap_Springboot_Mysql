-- 推荐使用 InnoDB 存储引擎，因为它支持事务和外键约束。
-- SET default_storage_engine=InnoDB;

-- =============================================
-- 1. 部门表
[cite_start]-- 用于管理组织架构 [cite: 25]。
[cite_start]-- 对应 Web 端的 "部门管理" 功能 [cite: 35]。
-- =============================================
CREATE TABLE `departments` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL COMMENT '部门名称',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_name` (`name`)
) COMMENT='部门信息表';

-- =============================================
-- 2. 角色表
-- 定义用户角色及其权限。
[cite_start]-- 系统预设了系统管理员、仓库管理员、操作员等角色 [cite: 12]。
-- =============================================
CREATE TABLE `roles` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称 (例如: 系统管理员, 仓库管理员)',
  UNIQUE KEY `uk_role_name` (`role_name`)
) COMMENT='用户角色表';

-- =============================================
-- 3. 用户表
-- 存储用户账户信息。
[cite_start]-- 支持用户登录、修改密码和用户管理功能 [cite: 23, 25]。
-- =============================================
CREATE TABLE `users` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL COMMENT '登录用户名',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '加密后的密码',
  `full_name` VARCHAR(100) COMMENT '用户全名',
  `role_id` INT UNSIGNED NOT NULL COMMENT '关联角色表的外键',
  `department_id` INT UNSIGNED COMMENT '关联部门表的外键',
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '标记用户账户是否启用',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_department_id` (`department_id`),
  CONSTRAINT `fk_users_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_users_department` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL
) COMMENT='系统用户账户表';

-- =============================================
-- 4. 产品表 (商品表)
-- 存储仓库中所有货品的基础信息。
-- 是库存查询、出入库管理的基础。
-- =============================================
CREATE TABLE `products` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `sku` VARCHAR(100) NOT NULL COMMENT 'SKU (Stock Keeping Unit), 产品的唯一标识符',
  `name` VARCHAR(200) NOT NULL COMMENT '产品名称',
  `description` TEXT COMMENT '产品详细描述',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_sku` (`sku`),
  KEY `idx_name` (`name`)
) COMMENT='产品主数据表';

-- =============================================
-- 5. 入库单表
[cite_start]-- 管理入库任务，通常由Excel批量导入创建 [cite: 25]。
-- 这是一个入库批次的头信息。
-- =============================================
CREATE TABLE `inbound_orders` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `order_number` VARCHAR(50) NOT NULL COMMENT '入库单的唯一编号',
  [cite_start]`created_by_user_id` INT UNSIGNED NOT NULL COMMENT '创建该订单的用户 (仓库管理员) [cite: 15]',
  `status` ENUM('待处理', '处理中', '已完成') NOT NULL DEFAULT '待处理' COMMENT '入库单状态',
  `notes` TEXT COMMENT '额外备注',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_order_number` (`order_number`),
  KEY `idx_created_by_user_id` (`created_by_user_id`),
  CONSTRAINT `fk_inbound_orders_user` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT
) COMMENT='入库单 (任务) 表';

-- =============================================
-- 6. 库存表
-- 系统的核心表，代表了实际的库存。每条记录都是一个特定批次的产品。
[cite_start]-- 二维码将唯一标识此表中的一条记录，用于所有扫码操作 [cite: 5]。
-- =============================================
CREATE TABLE `inventory` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `product_id` INT UNSIGNED NOT NULL COMMENT '关联产品表的外键',
  `batch_code` VARCHAR(100) NOT NULL COMMENT '批次唯一编码, 用于生成二维码',
  `inbound_order_id` INT UNSIGNED COMMENT '此批次所属的入库单',
  `quantity` INT NOT NULL DEFAULT 0 COMMENT '该批次的当前库存数量',
  `received_at` DATETIME COMMENT '批次被实际扫码入库的时间',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_batch_code` (`batch_code`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_inbound_order_id` (`inbound_order_id`),
  CONSTRAINT `fk_inventory_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_inventory_inbound_order` FOREIGN KEY (`inbound_order_id`) REFERENCES `inbound_orders` (`id`) ON DELETE SET NULL
) COMMENT='库存及批次表';

-- =============================================
-- 7. 异动日志表
-- 记录每一次库存变动（入库、出库、调整），用于审计和报表。
[cite_start]-- App的扫码操作会填充此表，为数据统计提供依据 [cite: 25]。
-- =============================================
CREATE TABLE `transaction_logs` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `inventory_id` INT UNSIGNED NOT NULL COMMENT '关联的库存批次ID',
  [cite_start]`user_id` INT UNSIGNED NOT NULL COMMENT '执行此操作的用户 (操作员) [cite: 18]',
  `type` ENUM('入库', '出库', '调整') NOT NULL COMMENT '异动类型',
  `quantity_change` INT NOT NULL COMMENT '库存变化数量 (+表示入库, -表示出库)',
  `quantity_after_transaction` INT NOT NULL COMMENT '本次异动后, 批次的库存数量',
  `notes` VARCHAR(255) COMMENT '可选备注, 例如: 盘点调整原因',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY `idx_inventory_id` (`inventory_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_logs_inventory` FOREIGN KEY (`inventory_id`) REFERENCES `inventory` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_logs_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT
) COMMENT='库存异动日志表';

