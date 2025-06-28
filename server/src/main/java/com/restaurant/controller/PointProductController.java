package com.restaurant.controller;

import com.restaurant.model.PointProduct;
import com.restaurant.service.PointProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/point-products")
public class PointProductController {
    
    @Autowired
    private PointProductService pointProductService;

    @GetMapping
    public List<PointProduct> list() {
        return pointProductService.list();
    }

    @GetMapping("/{id}")
    public PointProduct getById(@PathVariable Integer id) {
        return pointProductService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody PointProduct pointProduct) {
        return pointProductService.save(pointProduct);
    }

    @PutMapping
    public boolean update(@RequestBody PointProduct pointProduct) {
        return pointProductService.updateById(pointProduct);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return pointProductService.removeById(id);
    }
} 