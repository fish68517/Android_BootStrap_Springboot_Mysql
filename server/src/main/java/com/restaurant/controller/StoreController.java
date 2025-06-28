package com.restaurant.controller;

import com.restaurant.model.DishCategory;
import com.restaurant.model.Store;
import com.restaurant.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {
    
    @Autowired
    private StoreService storeService;

    @GetMapping
    public List<Store> list() {
        System.out.println("加载所有商店数据");
        return storeService.list();
    }

      @GetMapping("/categories/storeId/{id}")
      public List<DishCategory> listCategories(@PathVariable Integer id) {
          return storeService.listCategoriesByStoreId(id);
      }

    @GetMapping("/{id}")
    public Store getById(@PathVariable Integer id) {
        return storeService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody Store store) {
        return storeService.save(store);
    }

    @PutMapping
    public boolean update(@RequestBody Store store) {
        return storeService.updateById(store);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return storeService.removeById(id);
    }
} 