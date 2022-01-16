package com.example.emoswx.config.shiro;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.emoswx.common.util.R;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;

import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author ZZy
 * @date 2022/1/9 17:51
 * @description
 */
@Component
@Scope("prototype")
public class OAuth2Filter extends AuthenticatingFilter {

    @Autowired
    private ThreadLocalToken threadLocalToken;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${emos.jwt.cache-expire}")
    private int cashExpired;


    /**
     * 拦截请求，把令牌字符串封装成令牌对象
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest,
                                              ServletResponse servletResponse) throws Exception {
        String token = getRequestToken((HttpServletRequest) servletRequest);
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return new OAuth2Token(token);
    }

    /**
     * 拦截请求，是否需要被shiro框架处理
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        //ajax提交application/json数据的时候，会发出Options的请求
        //这里要放行Options的请求，不需shiro处理
        HttpServletRequest req = (HttpServletRequest) request;
        if (req.getMethod().equals(RequestMethod.OPTIONS.name())) {
            return true;
        }
        //其他非option的请求需要被拦截
        return false;
    }

    /**
     * 处理应该被shiro处理的请求
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest,
                                     ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Content-Type", "text/html;charset=UTF-8");
        //允许跨域请求
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));

        //TODO 这里为什么先clear一下？
        threadLocalToken.clear();

        //获取token，若不存在，则返回401-未授权,“无效的令牌”
        String token = getRequestToken(request);
        //TODO isBlank和isEmpty的区别是什么？
        if (StringUtils.isBlank(token)) {
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            //TODO getWriter方法是输出在哪里？
            response.getWriter().print("无效的令牌");
            return false;
        }
        //token存在，查询令牌是否过期
        try {
            jwtUtil.verifyToken(token);
        } catch (TokenExpiredException e) {
            //token过期
            //token在redis中是否存在，若存在，则产生一个新的token，否则用户重新登录
            if (redisTemplate.hasKey(token)) {
                //redis先删除令牌
                redisTemplate.delete(token);
                int userId = jwtUtil.getUserId(token);
                token = jwtUtil.creatToken(userId);
                //将新token保存到redis中
                redisTemplate.opsForValue().set(token, userId + "", cashExpired, TimeUnit.DAYS);
                //将新token保存到线程里
                threadLocalToken.setToken(token);
            } else {
                //redis不存在令牌，让用户重新登录
                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                response.getWriter().print("令牌已过期");
                return false;
            }
        } catch (JWTDecodeException e) {
            //令牌无效
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            response.getWriter().print("无效的令牌");
            return false;
        }
        //TODO executeLogin是什么意思？框架里的？
        boolean bool = executeLogin(request, response);
        return bool;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest servletRequest, ServletResponse servletResponse) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Content-Type", "text/html;charset=UTF-8");
        response.setStatus(HttpStatus.SC_UNAUTHORIZED);
        //允许跨域请求
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        try {
            response.getWriter().print(e.getMessage());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return false;
    }

    /**
     * 获取请求头里的token
     */
    public String getRequestToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StringUtils.isEmpty(token)) {
            token = request.getParameter("token");
        }
        return token;
    }

    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        super.doFilterInternal(request, response, chain);
    }
}