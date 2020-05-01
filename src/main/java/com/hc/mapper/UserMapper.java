package com.hc.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.hc.bean.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: 梁云亮
 * @Date 2020/4/21 16:58
 * @Description:
 */   
@Mapper
public interface UserMapper {
    User selectByPrimaryKey(Integer id);

    List<User> selectByUsername(@Param("username")String username);
}