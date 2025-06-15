package com.restaurant.controller;

import com.restaurant.model.DishCategory;
import com.restaurant.service.DishCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dish-categories")
public class DishCategoryController {
    
    @Autowired
    private DishCategoryService categoryService;

    @GetMapping
    public List<DishCategory> list() {
        return categoryService.list();
    }

    @GetMapping("/{id}")
    public DishCategory getById(@PathVariable Integer id) {
        return categoryService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody DishCategory category) {
        return categoryService.save(category);
    }

    @PutMapping
    public boolean update(@RequestBody DishCategory category) {
        return categoryService.updateById(category);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return categoryService.removeById(id);
    }
} 