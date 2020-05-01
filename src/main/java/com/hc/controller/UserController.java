package com.hc.controller;

import com.alibaba.fastjson.JSONObject;
import com.hc.anno.LoginToken;
import com.hc.anno.PassToken;
import com.hc.common.Result;
import com.hc.bean.User;
import com.hc.service.UserService;
import com.hc.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author: 梁云亮
 * @Date 2020/4/21 17:03
 * @Description:
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    //登录
    @RequestMapping("/login")
    public Result login(HttpServletResponse response, String username, String password) {
//        /**设置响应头允许ajax跨域访问**/
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        /*星号表示所有的异域请求都可以接受，*/
//        response.setHeader("Access-Control-Allow-Methods", "GET,POST");

        List<User> userList = userService.selectByUsername(username);
        if (userList == null || userList.size() == 0) {
            log.info("登录失败,用户不存在");
            return Result.FAIL("登录失败,用户不存在");
        }
        String role = "admin";
        for (User user : userList) {
            if (user.getPassword().equals(password)) {
                log.info("登录成功");
                String token = JwtTokenUtil.createJWT(user.getId()+"",username,role);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("token",token);
                return Result.SUCCESS(jsonObject);
            }
        }
        log.info("登录失败,密码错误");
        return Result.FAIL("登录失败,密码错误");
    }

    @LoginToken
    @RequestMapping("/getUserById")
    public User getUserById(HttpServletResponse response,Integer userId) {
        log.info("访问需要登录才能访问的受保护的资源");
//        /**设置响应头允许ajax跨域访问**/
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        /*星号表示所有的异域请求都可以接受，*/
//        response.setHeader("Access-Control-Allow-Methods", "GET,POST");

        User user = userService.selectByPrimaryKey(userId);
        return user;
    }

    // 不加注解的话默认不验证，登录接口一般是不验证的。
    // getMessage()加上了登录注解，说明该接口必须登录获取token后，在请求头中加上token并通过验证才可以访问
    @PassToken
    @GetMapping("/getMsg")
    public String getMsg() {
        log.info("访问不需要登录就能访问的资源");
        return "不需要通过验证";
    }
}
