package com.restaurant.controller;

import com.restaurant.mapper.DishMapper;
import com.restaurant.model.Dish;
import com.restaurant.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dishes")
public class DishController {
    
    @Autowired
    private DishService dishService;

    @Autowired
    private DishMapper dishMapper;

    @GetMapping
    public List<Dish> list() {
        return dishService.list();
    }

    @GetMapping("/{id}")
    public Dish getById(@PathVariable Integer id) {
        return dishService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody Dish dish) {
        return dishService.save(dish);
    }

    @PutMapping
    public boolean update(@RequestBody Dish dish) {
        return dishService.updateById(dish);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        try {
            boolean deleted = dishService.removeById(id);
            if (deleted) {
                return ResponseEntity.ok("删除成功");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到该菜品");
            }
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("该菜品存在关联的订单，无法删除");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/category/{categoryId}")
    public List<Dish> getByCategoryId(@PathVariable Integer categoryId) {
        List<Dish> dishes = dishMapper.getByCategoryId(categoryId);
        System.out.println(dishes);
        return dishes;
    }

} 