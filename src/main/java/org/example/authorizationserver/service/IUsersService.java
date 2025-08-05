// src/main/java/org/example/authorizationserver/service/IUsersService.java
package org.example.authorizationserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.authorizationserver.entity.Users;

/**
 * 用户表服务类
 */
public interface IUsersService extends IService<Users> {
    /**
     * 根据用户名查找用户
     */
    Users findByUsername(String username);
}