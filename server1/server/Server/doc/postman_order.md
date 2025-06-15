# 订单功能 Postman 调试指南

本文档用于指导如何使用 Postman 测试订单（Order）功能的 API 接口。

## 准备工作

1.  确保后端服务已启动。
2.  打开 Postman。
3.  基础 URL: `http://localhost:8080/api/orders`

## 1. 创建订单 (Create)

创建一个新订单。

- **Method**: `POST`
- **URL**: `http://localhost:8080/api/orders`
- **Headers**:
    - `Content-Type`: `application/json`
- **Body** (raw, JSON):
  
  ```json
  {
      "userId": 1,
      "storeId": 1,
      "totalAmount": 54.00,
      "deliveryFee": 0.00,
      "status": 0,
      "orderType": 1,
      "cartItems": "1:1:{容量=中份, 甜度=标准糖};6:1:{饮用=冷饮}",
      "remark": "少冰，谢谢"
  }
  ```
  **注意**: `userId` 和 `storeId` 必须是数据库中已存在的主键。`cartItems` 描述了购物车内容，格式为 `dishId:quantity:{options};...`。

- **预期成功响应 (200 OK or 201 Created)**:
  响应体可能包含新创建的订单对象，包含一个由后端生成的 `orderId` 和 `orderNo`。
  ```json
  {
    "orderId": 55,
    "userId": 1,
    "storeId": 1,
    "orderNo": "a1b2c3d4-e5",
    "totalAmount": 54.00,
    "deliveryFee": 0.00,
    "status": 0,
    "orderType": 1,
    "deliveryAddress": null,
    "paymentMethod": null,
    "createdAt": "2025-06-08T14:30:00",
    "cartItems": "1:1:{容量=中份, 甜度=标准糖};6:1:{饮用=冷饮}",
    "remark": "少冰，谢谢",
    "orderDetailsItems": [],
    "review": null
  }
  ```

## 2. 查询订单 (Read)

### 2.1 根据订单ID查询

- **Method**: `GET`
- **URL**: `http://localhost:8080/api/orders/29`
  **注意**: URL 结尾的 `29` 是 `order_id`。

- **预期成功响应 (200 OK)**:
  ```json
  {
      "orderId": 29,
      "userId": 1,
      "storeId": 1,
      "orderNo": "d17e0688-8",
      "totalAmount": 86.00,
      "deliveryFee": 0.00,
      "status": 1,
      "orderType": 2,
      "deliveryAddress": null,
      "paymentMethod": null,
      "createdAt": "2025-01-28 21:40:13",
      "cartItems": "1:2:{容量=小份, 甜度=三分糖, 饮用=冷饮};7:1:{容量=大份}",
      "orderDetailsItems": [],
      "review": {
          "reviewId": 1,
          "orderId": 29,
          "userId": 1,
          "storeId": 1,
          "rating": 5,
          "comment": "这家餐厅的招牌奶茶味道真不错，配送也很快，下次还会再点！",
          "createdAt": "2025-03-10 10:00:00"
      }
  }
  ```

### 2.2 根据用户ID查询

查询某个用户的所有订单。

- **Method**: `GET`
- **URL**: `http://localhost:8080/api/orders/user/1`
  **注意**: URL 结尾的 `1` 是 `user_id`。

- **预期成功响应 (200 OK)**:
  ```json
  [
      {
          "orderId": 29,
          "userId": 1,
          "storeId": 1,
          "orderNo": "d17e0688-8",
          "totalAmount": 86.00,
          "status": 1,
          "createdAt": "2025-01-28 21:40:13",
          "review": {
              "reviewId": 1,
              "rating": 5,
              "comment": "这家餐厅的招牌奶茶味道真不错，配送也很快，下次还会再点！"
          }
      },
      {
          "orderId": 30,
          "userId": 1,
          "storeId": 1,
          "orderNo": "a33b7876-f",
          "totalAmount": 56.00,
          "status": 1,
          "createdAt": "2025-01-28 21:40:13",
          "review": null
      }
  ]
  ```

## 3. 更新订单 (Update)

通常用于更新订单状态，例如从未支付到已支付，或取消订单。

- **Method**: `PUT`
- **URL**: `http://localhost:8080/api/orders`
- **Headers**:
    - `Content-Type`: `application/json`
- **Body** (raw, JSON):
  ```json
  {
      "orderId": 55,
      "status": 1
  }
  ```
  **注意**: `orderId` 是必须的。请求体中可以只包含要更新的字段。`status` 的值 - `0`:待支付, `1`:已支付, `2`:制作中, `3`:已完成, `4`:已取消。

- **预期成功响应 (200 OK)**:
  ```
  true
  ```

- **预期失败响应 (404 Not Found)** - 如果 `orderId` 不存在:
  可能返回错误信息或 `false`。
