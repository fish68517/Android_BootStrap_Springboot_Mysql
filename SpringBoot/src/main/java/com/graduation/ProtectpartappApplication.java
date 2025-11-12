package com.graduation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.ExceptionHandler;

@SpringBootApplication
@MapperScan("com.graduation.mapper")
public class ProtectpartappApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProtectpartappApplication.class, args);
    }
} 