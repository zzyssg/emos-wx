package com.example.emoswx.config.shiro;

import org.springframework.stereotype.Component;

/**
 * @author ZZy
 * @date 2022/1/9 17:21
 * @description
 */
@Component
public class ThreadLocalToken {

    private ThreadLocal local = new ThreadLocal();

    public void setToken(String token) {
        //TODO Threadlocal对象里只能添加一个对象吗？
        local.set(token);
    }

    public String getToken() {
        return (String) local.get();
    }

    public void clear() {
        local.remove();
    }
}
