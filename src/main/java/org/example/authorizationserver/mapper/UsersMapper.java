// src/main/java/org/example/authorizationserver/mapper/UsersMapper.java
package org.example.authorizationserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.authorizationserver.entity.Users;

/**
 * 用户表Mapper接口
 */
@Mapper
public interface UsersMapper extends BaseMapper<Users> {
}