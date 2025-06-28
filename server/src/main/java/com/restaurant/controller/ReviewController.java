package com.restaurant.controller;

import com.restaurant.model.Review;
import com.restaurant.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public List<Review> getAllReviews() {
        log.info("查询所有评价");
        return reviewService.list();
    }

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody Review review) {
        log.info("创建新评价: {}", review);
        // 数据库层面有唯一约束 UNIQUE INDEX `order_id`(`order_id` ASC) 保证一个订单只能评价一次
        // 在业务代码里检查可以提供更友好的提示
        Review existingReview = reviewService.getByOrderId(review.getOrderId());
        if (existingReview != null) {
            log.warn("订单 {} 已存在评价，无法重复提交", review.getOrderId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("该订单已经评价过了");
        }
        boolean saved = reviewService.save(review);
        if (saved) {
            log.info("评价创建成功, ID: {}", review.getReviewId());
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        } else {
            log.error("创建评价失败: {}", review);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("创建评价失败");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Integer id) {
        log.info("查询评价, ID: {}", id);
        Review review = reviewService.getById(id);
        if (review != null) {
            return ResponseEntity.ok(review);
        } else {
            log.warn("未找到评价, ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public List<Review> getReviewsByUserId(@PathVariable Integer userId) {
        log.info("查询用户 {} 的所有评价", userId);
        return reviewService.getByUserId(userId);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Review> getReviewByOrderId(@PathVariable Integer orderId) {
        log.info("查询订单 {} 的评价", orderId);
        Review review = reviewService.getByOrderId(orderId);
        if (review != null) {
            return ResponseEntity.ok(review);
        } else {
            log.warn("未找到订单 {} 的评价", orderId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/store/{storeId}")
    public List<Review> getReviewsByStoreId(@PathVariable Integer storeId) {
        log.info("查询店铺 {} 的所有评价", storeId);
        return reviewService.getByStoreId(storeId);
    }

    @PutMapping
    public ResponseEntity<?> updateReview(@RequestBody Review review) {
        log.info("更新评价, ID: {}, 数据: {}", review.getReviewId(), review);
        boolean updated = reviewService.updateById(review);
        if (updated) {
            log.info("评价更新成功, ID: {}", review.getReviewId());
            return ResponseEntity.ok(reviewService.getById(review.getReviewId()));
        } else {
            log.error("更新评价失败, ID: {}", review.getReviewId());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer id) {
        log.info("删除评价, ID: {}", id);
        boolean deleted = reviewService.removeById(id);
        if (deleted) {
            log.info("评价删除成功, ID: {}", id);
            return ResponseEntity.noContent().build();
        } else {
            log.error("删除评价失败, ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
} 