package com.restaurant.controller;

import com.restaurant.mapper.DishMapper;
import com.restaurant.model.Dish;
import com.restaurant.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public boolean delete(@PathVariable Integer id) {
        return dishService.removeById(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<Dish> getByCategoryId(@PathVariable Integer categoryId) {
        List<Dish> dishes = dishMapper.getByCategoryId(categoryId);
        System.out.println(dishes);
        return dishes;
    }

} 