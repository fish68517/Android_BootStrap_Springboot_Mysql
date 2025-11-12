package com.graduation.exception;

import com.graduation.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理应用中的各种异常
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理业务异常
     * 对于AJAX请求返回JSON，对于页面请求返回错误页面
     */
    @ExceptionHandler(BusinessException.class)
    public Object handleBusinessException(BusinessException e) {
        log.error("业务异常: code={}, message={}", e.getCode(), e.getMessage(), e);
        
        // 判断是否为AJAX请求（简单判断，实际可以通过请求头判断）
        // 这里返回JSON响应，适用于AJAX请求
        return ResponseUtil.error(e.getCode(), e.getMessage());
    }
    

    
    /**
     * 处理参数验证异常（@Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationException(MethodArgumentNotValidException e) {
        log.error("参数验证失败", e);
        
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        
        return ResponseUtil.error("VALIDATION_ERROR", errorMessage);
    }
    
    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBindException(BindException e) {
        log.error("参数绑定失败", e);
        
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        
        return ResponseUtil.error("BIND_ERROR", errorMessage);
    }
    
    /**
     * 处理未授权异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handleUnauthorizedException(UnauthorizedException e) {
        log.error("未授权访问: {}", e.getMessage(), e);
        
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", e.getMessage());
        mav.addObject("code", e.getCode());
        return mav;
    }
    

    
    /**
     * 处理所有未捕获的异常（JSON响应）
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleExceptionForJson(Exception e) {
        log.error("系统异常", e);
        return ResponseUtil.error("SYSTEM_ERROR", "系统错误，请稍后重试");
    }
}
