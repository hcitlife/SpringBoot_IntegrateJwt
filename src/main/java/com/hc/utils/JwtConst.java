package com.hc.utils;

/**
 * @Author: 梁云亮
 * @Date 2020/4/22 21:05
 * @Description:
 */
public interface JwtConst {
    // 代表这个JWT的接收对象
    String clientId = "098f6bcd4621d373cade4e832627b4f6";
    // 密钥, 经过Base64加密, 可自行替换
    String secret = "MDk4ZjZiY2Q0NjIxZD";
    // JWT的签发主体，存入issuer
    String name = "restapiuser";
    // 过期时间，单位毫秒
    long expires = 60000;
    // 从客户端传递过来的token的名称
    String token = "token";
}
