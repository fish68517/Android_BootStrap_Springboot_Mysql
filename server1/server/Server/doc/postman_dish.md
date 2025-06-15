# 菜品功能 Postman 调试指南

本文档用于指导如何使用 Postman 测试菜品（Dish）功能的 API 接口。

## 准备工作

1.  确保后端服务已启动。
2.  打开 Postman。
3.  基础 URL: `http://localhost:8080/api/dishes`

## 1. 查询所有菜品 (List All)

- **Method**: `GET`
- **URL**: `http://localhost:8080/api/dishes`
- **预期成功响应 (200 OK)**:
  ```json
  [
      {
          "dishId": 1,
          "categoryId": 1,
          "storeId": 1,
          "dishName": "招牌奶茶",
          "price": 28.00,
          "description": "采用优质茶叶和新鲜牛奶精心调制...",
          "image": "jiushui_baixiangguo",
          "status": 1,
          "createdAt": "2025-01-28T21:40:13",
          "dishOptions": "{\"容量\": [\"小份\", \"中份\", \"大份\"], \"甜度\": [\"三分糖\", \"标准糖\", \"正常糖\"]}"
      },
      {
          "dishId": 2,
          "categoryId": 1,
          "storeId": 2,
          "dishName": "红茶拿铁",
          "price": 32.00,
          "description": "选用优质红茶与新鲜牛奶完美融合...",
          "image": "jiushui_natie",
          "status": 1,
          "createdAt": "2025-01-28T21:40:13",
          "dishOptions": "{\"容量\": [\"小份\", \"中份\", \"大份\"], \"饮用\": [\"热饮\", \"冷饮\"]}"
      }
  ]
  ```

## 2. 根据ID查询菜品 (Get by ID)

- **Method**: `GET`
- **URL**: `http://localhost:8080/api/dishes/1`
  **注意**: URL 结尾的 `1` 是 `dish_id`。

- **预期成功响应 (200 OK)**:
  ```json
  {
      "dishId": 1,
      "categoryId": 1,
      "storeId": 1,
      "dishName": "招牌奶茶",
      "price": 28.00,
      "description": "采用优质茶叶和新鲜牛奶精心调制...",
      "image": "jiushui_baixiangguo",
      "status": 1,
      "createdAt": "2025-01-28T21:40:13",
      "dishOptions": "{\"容量\": [\"小份\", \"中份\", \"大份\"], \"甜度\": [\"三分糖\", \"标准糖\", \"正常糖\"]}"
  }
  ```

## 3. 根据分类ID查询菜品 (Get by Category ID)

- **Method**: `GET`
- **URL**: `http://localhost:8080/api/dishes/category/1`
  **注意**: URL 结尾的 `1` 是 `category_id`。

- **预期成功响应 (200 OK)**:
  ```json
  [
      {
          "dishId": 1,
          "categoryId": 1,
          "storeId": 1,
          "dishName": "招牌奶茶",
          "price": 28.00,
          "description": "采用优质茶叶和新鲜牛奶精心调制...",
          "image": "jiushui_baixiangguo",
          "status": 1,
          "createdAt": "2025-01-28T21:40:13",
          "dishOptions": "{\"容量\": [\"小份\", \"中份\", \"大份\"], \"甜度\": [\"三分糖\", \"标准糖\", \"正常糖\"]}"
      }
  ]
  ```

## 4. 创建菜品 (Create)

- **Method**: `POST`
- **URL**: `http://localhost:8080/api/dishes`
- **Headers**:
    - `Content-Type`: `application/json`
- **Body** (raw, JSON):
  ```json
  {
      "categoryId": 4,
      "storeId": 1,
      "dishName": "特调手冲咖啡",
      "price": 45.00,
      "description": "精选日晒耶加雪菲，风味独特。",
      "image": "new_coffee_image",
      "status": 1,
      "dishOptions": "{\"风味\": [\"花果香\", \"酒香\"], \"饮用\": [\"热饮\"]}"
  }
  ```
- **预期成功响应 (200 OK)**:
  ```
  true
  ```

## 5. 更新菜品 (Update)

- **Method**: `PUT`
- **URL**: `http://localhost:8080/api/dishes`
- **Headers**:
    - `Content-Type`: `application/json`
- **Body** (raw, JSON):
  ```json
  {
      "dishId": 1,
      "price": 29.50,
      "description": "新描述：口感升级，更加顺滑！"
  }
  ```
  **注意**: `dishId` 是必须的。请求体中可以只包含要更新的字段。
- **预期成功响应 (200 OK)**:
  ```
  true
  ```

## 6. 删除菜品 (Delete)

- **Method**: `DELETE`
- **URL**: `http://localhost:8080/api/dishes/1`
  **注意**: URL 结尾的 `1` 是 `dish_id`。

- **预期成功响应 (200 OK)**:
  ```
  true
  ```
- **预期失败响应 (e.g., 500 Internal Server Error)** - 如果菜品关联了订单，可能无法删除，取决于后端约束。
