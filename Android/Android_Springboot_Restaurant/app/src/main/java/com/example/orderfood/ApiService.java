package com.example.orderfood;

import com.example.orderfood.model.CartItem;
import com.example.orderfood.model.Coupon;
import com.example.orderfood.model.Dish;
import com.example.orderfood.model.DishCategory;
import com.example.orderfood.model.Order;
import com.example.orderfood.model.OrderDetail;
import com.example.orderfood.model.OrderResponse;
import com.example.orderfood.model.PaymentResponse;
import com.example.orderfood.model.PointExchange;
import com.example.orderfood.model.PointProduct;
import com.example.orderfood.model.Review;
import com.example.orderfood.model.Store;
import com.example.orderfood.model.User;
import com.example.orderfood.model.PaymentStatusResponse;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiService {
    // 用户相关接口
    @POST("/api/users")
    Call<Boolean> register(@Body User user);



    @POST("/api/users/login")
    Call<User> login(@Query("nickname") String nickname, @Query("password") String password);

    @GET("/api/users/{id}")
    Call<User> getUserInfo(@Path("id") Integer id);

    @PUT("/api/users")
    Call<Boolean> updateUserInfo(@Body User user);

    @DELETE("/api/users/{id}")
    Call<Boolean> deleteUser(@Path("id") Integer id);

    // 菜品相关接口
    @GET("/api/dishes")
    Call<List<Dish>> getAllDishes();

    @GET("/api/dishes/{id}")
    Call<Dish> getDishById(@Path("id") Integer id);

    @GET("/api/dishes/category/{categoryId}")
    Call<List<Dish>> getDishesByCategory(@Path("categoryId") Integer categoryId);

    // 订单相关接口
    @POST("/api/orders")
    Call<OrderResponse> createOrder(@Body Order order);

    @GET("/api/orders/user/{userId}")
    Call<List<Order>> getUserOrders(@Path("userId") Integer userId);

    @GET("/api/orders/{id}")
    Call<Order> getOrderById(@Path("id") Integer id);

    // 优惠券相关接口
    @GET("/api/coupons")
    Call<List<Coupon>> getAllCoupons();

    @POST("/api/coupons")
    Call<Boolean> postCoupon(@Body Coupon coupon);

    @DELETE("/api/coupons/{id}")
    Call<Boolean> deleteCoupon(@Path("id") Integer id);

    @GET("/api/user-coupons/user/{userId}")
    Call<List<Coupon>> getUserCoupons(@Path("userId") Integer userId);


    @GET("/api/point-products")
    Call<List<PointProduct>> listPointProducts();


    @POST("/api/point-exchanges")
    Call<Boolean> savePointExchange(@Body PointExchange pointExchange);





    @GET("/api/stores")
    Call<List<Store>> getAllStores();

    @GET("/api/dish-categories")
    Call<List<DishCategory>> getAllDishCategories();

    @GET("/api/stores/categories/storeId/{id}")
    Call<List<DishCategory>> getCategoriesByStoreId(@Path("id") Integer id);

    // 支付相关接口
    @POST("/api/payment/{orderId}/{paymentMethod}")
    Call<PaymentResponse> getPaymentParams(String orderId, int paymentMethod);

    @GET("payment/status/{orderId}")
    Call<PaymentStatusResponse> checkPaymentStatus(@Path("orderId") String orderId);

    @POST("/api/cart/add")
    Call<Boolean> addToCart(@Body CartItem cartItem);

    @DELETE("/api/cart/remove/{cartItemId}/{userId}")
    Call<Boolean> removeFromCart(@Path("cartItemId") Integer cartItemId, @Path("userId") Integer userId);

    @GET("/api/cart/user/{userId}")
    Call<List<CartItem>> getCartItems(@Path("userId") Integer userId);

    // 获取购物车列表
    @GET("/api/cart/list")
    Call<List<CartItem>> getCartItems(@Query("userId") int userId);

    // 更新购物车商品数量
    @PUT("/api/cart/quantity")
    Call<Void> updateCartQuantity(
        @Query("userId") int userId,
        @Query("cartItemId") int cartItemId,
        @Query("quantity") int quantity,
        @Query("totalPrice") double totalPrice
    );

    // 从购物车中移除商品

    // 清空购物车
    @DELETE("/api/cart/clear")
    Call<Void> clearCart(@Query("userId") int userId);

    @DELETE("/api/cart/clear/cartId")
    Call<Void> clearCartByCartId(@Query("userId") int userId, @Query("cartId") List<Integer> cartId);

    // 提交订单
    @POST("/api/orders")
    Call<Integer> submitOrder(@Body Order order);

    @PUT("/api/orders")
    Call<Boolean> update(@Body Order order);

    @POST("/api/reviews")
    Call<Review> createReview(@Body Review review);

    @GET("/api/reviews/order/{orderId}")
    Call<Review> getReviewByOrderId(@Path("orderId") int orderId);

    @GET("/api/reviews/user/{userId}")
    Call<List<Review>> getUserReviews(@Path("userId") int userId);

    @GET("/api/orders/store/{storeId}")
    Call<List<Order>> getOrdersByStore(@Path("storeId") int storeId);
}