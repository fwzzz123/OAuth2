// src/main/java/org/example/authorizationserver/service/impl/UsersServiceImpl.java
package org.example.authorizationserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.authorizationserver.entity.Users;
import org.example.authorizationserver.mapper.UsersMapper;
import org.example.authorizationserver.service.IUsersService;
import org.springframework.stereotype.Service;

/**
 * 用户表服务实现类
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {

    @Override
    public Users findByUsername(String username) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.getOne(queryWrapper);
    }
}