package com.restaurant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/admin/index")
    public String index() {
        return "admin/index";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String users() {
        return "admin/users";
    }

    @GetMapping("/admin/orders")
    public String orders() {
        return "admin/orders";
    }

    @GetMapping("/admin/dishes")
    public String dishes() {
        return "admin/dishes";
    }

    @GetMapping("/admin/reviews")
    public String reviews() {
        return "admin/reviews";
    }
} 