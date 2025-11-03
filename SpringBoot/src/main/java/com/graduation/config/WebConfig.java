package com.graduation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${file.upload-dir:src/main/resources/static/image}")
    private String uploadDir;
    /**
     * 解决 sb-admin-2 主题无法加载的问题
     * 将 "classpath:/templates/static/admin_theme/" 目录映射为 "/admin_theme/**"
     * * 这样，在 HTML 中就可以通过 /admin_theme/css/sb-admin-2.min.css 来访问了
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/admin_theme/**")
                .addResourceLocations("classpath:/templates/static/admin_theme/");
    }

    /**
     * 配置跨域支持
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")  // 实际部署时应当限制为特定域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

}