package com.example.emoswx.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author ZZy
 * @date 2022/1/9 19:04
 * @description
 */
@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    //TODO 此处的responbody注解的作用是什么？
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String validExceptionHandler(Exception e) {
        log.error("执行异常", e);
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            //错误信息返回给前台
            return exception.getBindingResult().getFieldError().getDefaultMessage();
        } else if (e instanceof EmosException) {
            EmosException exception = (EmosException) e;
            return exception.getMsg();
        } else if (e instanceof UnauthorizedException) {
            return "你不具有相关权限";
        } else {
            return "后端执行异常";
        }
    }
}
