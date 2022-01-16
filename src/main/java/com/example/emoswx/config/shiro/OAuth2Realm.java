package com.example.emoswx.config.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ZZy
 * @date 2022/1/9 17:14
 * @description
 */
@Component
public class OAuth2Realm extends AuthorizingRealm {

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public boolean supports(AuthenticationToken token) {
        //TODO 这是什么意思？
        return token instanceof OAuth2Token;
    }

    /**
     * 授权（验证权限时调用）
     * */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        AuthorizationInfo info = new SimpleAuthorizationInfo();
        //TODO 查询用户的权限列表
        //TODO 将权限列表添加到info对象中
        return info;
    }

    /**
     * 认证（登录时调用）
     * */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //TODO 从令牌中获取userId，然后检测该账户是否被冻结
        AuthenticationInfo info = new SimpleAuthenticationInfo();
        //TODO 向info对象中添加用户信息、token字符串
        return info;
    }
}
