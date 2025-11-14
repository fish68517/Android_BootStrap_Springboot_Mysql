package com.graduation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${file.upload-dir:src/main/resources/static/img}")
    private String uploadDir;

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    /**
     * 配置静态资源处理器
     * 配置静态资源访问路径，包括Bootstrap (sb-admin-2)相关资源和图片上传目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置sb-admin-2主题资源
        registry.addResourceHandler("/admin_theme/**")
                .addResourceLocations("classpath:/static/admin_theme/");
        
        // 配置CSS资源
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        
        // 配置JavaScript资源
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        
        // 配置图片资源（包括上传的图片）
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/");
        
        // 配置image目录（兼容现有图片路径）
        registry.addResourceHandler("/image/**")
                .addResourceLocations("classpath:/static/image/");
        
        // 配置vendor资源（Bootstrap, jQuery等第三方库）
        registry.addResourceHandler("/vendor/**")
                .addResourceLocations("classpath:/static/admin_theme/vendor/");
        
        // 配置外部上传目录（如果需要）
        // registry.addResourceHandler("/uploads/**")
        //         .addResourceLocations("file:" + uploadDir + "/");
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

    /**
     * 配置拦截器
     * 注意：由于使用了Spring Security，这个拦截器主要用于额外的session验证
     * Spring Security已经处理了大部分认证逻辑
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**")
                // =======================================================
                // 【【【关键修改：排除所有 API 请求】】】
                // 告诉拦截器不要管 /api/ 开头的任何请求
                // API 的安全由 SecurityConfig 中的 apiSecurityFilterChain 负责
                .excludePathPatterns("/api/**")
                .excludePathPatterns(
                    "/user/register",
                    "/user/login",
                    "/game/list",
                    "/game/detail/**",
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/image/**",
                    "/vendor/**",
                    "/admin_theme/**",
                    "/",
                    "/error"
                );
    }

}