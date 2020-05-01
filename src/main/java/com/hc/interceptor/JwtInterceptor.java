package com.hc.interceptor;

import com.hc.anno.LoginToken;
import com.hc.anno.PassToken;
import com.hc.bean.User;
import com.hc.common.CustomException;
import com.hc.common.ResultCode;
import com.hc.service.UserService;
import com.hc.utils.JwtConst;
import com.hc.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 获取Token信息并验证
 */
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    //    主要流程:
    //1.从 http 请求头中取出 token，
    //2.判断是否映射到方法
    //3.检查是否有passtoken注释，有则跳过认证
    //4.检查有没有需要用户登录的注解，有则需要取出并验证
    //5.认证通过则可以访问，不通过会报相关错误信息
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();

        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                log.info("当前所访问的资源不需要登录");
                return true;
            }
        }
        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(LoginToken.class)) {
            LoginToken loginToken = method.getAnnotation(LoginToken.class);
            if (loginToken.required()) {
                // 从 http 请求头中取出 token
                String authHeaderKey = JwtConst.token;
                String token = request.getHeader(authHeaderKey);
                if (token == null || token.length() == 0) {
                    token = request.getParameter(authHeaderKey);
                }
                if (token == null || token.length() == 0) {
                    log.info("用户未登录，请先登录");
                    throw new CustomException(ResultCode.USER_NOT_LOGGED_IN);
                }
                String userId = JwtTokenUtil.getUserId(token);

                User user = userService.selectByPrimaryKey(Integer.parseInt(userId));
                if (user == null) {
                    log.info("用户不存在，请重新登录");
                    throw new RuntimeException("用户不存在，请重新登录");
                }
                log.info("用户存在，登录成功");
                return true;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
