// com.contentflow.auth.mapper.UserMapper.java
package com.contentflow.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.contentflow.auth.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}