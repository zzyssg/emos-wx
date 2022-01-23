package com.example.emoswx.config.shiro;

import com.example.emoswx.db.pojo.TbUser;
import com.example.emoswx.exception.EmosException;
import com.example.emoswx.service.UserService;
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

import java.util.Set;

/**
 * @author ZZy
 * @date 2022/1/9 17:14
 * @description
 */
@Component
public class OAuth2Realm extends AuthorizingRealm {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;


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
        TbUser user = (TbUser) principalCollection.getPrimaryPrincipal();
        int userId = user.getId();
        //TODO 查询用户的权限列表
        Set<String> permissions = userService.searchUserPermissions(userId);
        //TODO 将权限列表添加到info对象中
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permissions);
        return info;
    }

    /**
     * 认证（登录时调用）
     * */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //从令牌中获取userId，然后检测该账户是否被冻结
        String accessToken = (String) token.getPrincipal();
        int userId = jwtUtil.getUserId(accessToken);
        TbUser user = userService.searchById(userId);
        if (user == null) {
            throw new EmosException("账号被锁定，请联系系统管理员");
        }
        //向info对象中添加用户信息、token字符串
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user,accessToken,getName());
        return info;
    }
}
