# 评价功能 Postman 调试指南

本文档用于指导如何使用 Postman 测试新增的评价（Review）功能的 API 接口。

## 准备工作

1.  确保后端服务已启动。
2.  打开 Postman。
3.  基础 URL: `http://localhost:8080/api/reviews`

## 1. 创建评价 (Create)

创建一个新的评价。一个订单只能被评价一次。

- **Method**: `POST`
- **URL**: `http://localhost:8080/api/reviews`
- **Headers**:
    - `Content-Type`: `application/json`
- **Body** (raw, JSON):

  ```json
  {
      "orderId": 31,
      "userId": 1,
      "storeId": 1,
      "rating": 5,
      "comment": "Postman 测试：味道好极了，服务周到！"
  }
  ```
  **注意**: `orderId` 必须是一个数据库中存在且尚未被评价的订单ID。 `userId` 和 `storeId` 也应该是数据库中已存在的主键。

- **预期成功响应 (201 Created)**:
  ```json
  {
      "reviewId": 4, // ID 会自动生成
      "orderId": 31,
      "userId": 1,
      "storeId": 1,
      "rating": 5,
      "comment": "Postman 测试：味道好极了，服务周到！",
      "createdAt": "2024-07-28 15:30:00" // 创建时间会自动生成
  }
  ```
- **预期失败响应 (409 Conflict)** - 如果订单已评价:
  ```
  该订单已经评价过了
  ```

## 2. 查询评价 (Read)

### 2.1 根据评价ID查询

- **Method**: `GET`
- **URL**: `http://localhost:8080/api/reviews/1`
  **注意**: URL 结尾的 `1` 是 `review_id`。

- **预期成功响应 (200 OK)**:
  ```json
  {
      "reviewId": 1,
      "orderId": 29,
      "userId": 1,
      "storeId": 1,
      "rating": 5,
      "comment": "这家餐厅的招牌奶茶味道真不错，配送也很快，下次还会再点！",
      "createdAt": "2025-03-10 10:00:00"
  }
  ```

### 2.2 根据用户ID查询

查询某个用户的所有评价。

- **Method**: `GET`
- **URL**: `http://localhost:8080/api/reviews/user/1`
  **注意**: URL 结尾的 `1` 是 `user_id`。

- **预期成功响应 (200 OK)**:
  ```json
  [
      {
          "reviewId": 1,
          "orderId": 29,
          "userId": 1,
          "storeId": 1,
          "rating": 5,
          "comment": "这家餐厅的招牌奶茶味道真不错，配送也很快，下次还会再点！",
          "createdAt": "2025-03-10 10:00:00"
      },
      {
          "reviewId": 2,
          "orderId": 30,
          "userId": 1,
          "storeId": 1,
          "rating": 4,
          "comment": "黑糖珍珠奶茶很好喝，但是感觉珍珠有点少。总体还行。",
          "createdAt": "2025-03-11 11:30:00"
      }
  ]
  ```

### 2.3 根据订单ID查询

查询某个订单的评价。

- **Method**: `GET`
- **URL**: `http://localhost:8080/api/reviews/order/29`
  **注意**: URL 结尾的 `29` 是 `order_id`。

- **预期成功响应 (200 OK)**:
  ```json
  {
      "reviewId": 1,
      "orderId": 29,
      "userId": 1,
      "storeId": 1,
      "rating": 5,
      "comment": "这家餐厅的招牌奶茶味道真不错，配送也很快，下次还会再点！",
      "createdAt": "2025-03-10 10:00:00"
  }
  ```
  
### 2.4 查询订单详情时包含评价

查询订单详情时，如果该订单有评价，会自动包含评价信息。

- **Method**: `GET`
- **URL**: `http://localhost:8080/api/orders/29`
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
      "createdAt": "Jan 28, 2025 9:40:13 PM",
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

## 3. 更新评价 (Update)

- **Method**: `PUT`
- **URL**: `http://localhost:8080/api/reviews`
- **Headers**:
    - `Content-Type`: `application/json`
- **Body** (raw, JSON):
  ```json
  {
      "reviewId": 1,
      "rating": 4,
      "comment": "更新后的评价：味道依然很好，但希望包装能更好一点。"
  }
  ```
  **注意**: `reviewId` 是必须的。请求体中可以只包含要更新的字段。

- **预期成功响应 (200 OK)**:
  ```json
  {
      "reviewId": 1,
      "orderId": 29,
      "userId": 1,
      "storeId": 1,
      "rating": 4,
      "comment": "更新后的评价：味道依然很好，但希望包装能更好一点。",
      "createdAt": "2025-03-10 10:00:00"
  }
  ```

## 4. 删除评价 (Delete)

- **Method**: `DELETE`
- **URL**: `http://localhost:8080/api/reviews/1`
  **注意**: URL 结尾的 `1` 是 `review_id`。

- **预期成功响应 (204 No Content)**:
  Response body 为空。

- **预期失败响应 (404 Not Found)** - 如果评价不存在:
  Response body 为空。 