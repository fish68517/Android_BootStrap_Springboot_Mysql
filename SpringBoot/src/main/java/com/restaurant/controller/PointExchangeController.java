package com.restaurant.controller;

import com.restaurant.model.PointExchange;
import com.restaurant.service.PointExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/point-exchanges")
public class PointExchangeController {
    
    @Autowired
    private PointExchangeService pointExchangeService;

    @GetMapping
    public List<PointExchange> list() {
        return pointExchangeService.list();
    }

    @GetMapping("/{id}")
    public PointExchange getById(@PathVariable Integer id) {
        return pointExchangeService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody PointExchange pointExchange) {
        return pointExchangeService.save(pointExchange);
    }

    @PutMapping
    public boolean update(@RequestBody PointExchange pointExchange) {
        return pointExchangeService.updateById(pointExchange);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return pointExchangeService.removeById(id);
    }
} 