package com.example.emoswx.aop;

import com.example.emoswx.common.util.R;
import com.example.emoswx.config.shiro.ThreadLocalToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ZZy
 * @date 2022/1/9 18:58
 * @description
 */

@Aspect
@Component
public class TokenAspect {

    @Autowired
    private ThreadLocalToken threadLocalToken;

    /**
     * 定义切入点
     * */
    @Pointcut("execution(public * com.example.emoswx.controller.*.*(..))")
    public void  aspect() {

    }

    @Around("aspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable{
        R r = (R) point.proceed();
        String token = threadLocalToken.getToken();
        //如果threadLocal中存在token，则更新token
        if (threadLocalToken.getToken() != null) {
            r.put("token", token);//响应中添加token
            //TODO 清除token?why?
            threadLocalToken.clear();
        }
        return r;
    }

}
