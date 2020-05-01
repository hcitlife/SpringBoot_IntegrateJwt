package com.hc.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.hc.mapper.UserMapper;
import com.hc.bean.User;
import com.hc.service.UserService;

import java.util.List;

/**
 * @Author: 梁云亮
 * @Date 2020/4/21 16:58
 * @Description:
 */   
@Service
public class UserServiceImpl implements UserService{

    @Resource
    private UserMapper userMapper;



    @Override
    public User selectByPrimaryKey(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<User> selectByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
}
