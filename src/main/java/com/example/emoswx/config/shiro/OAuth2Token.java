package com.example.emoswx.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author ZZy
 * @date 2022/1/9 16:57
 * @description
 */
public class OAuth2Token implements AuthenticationToken {

    private String token;

    public OAuth2Token(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
