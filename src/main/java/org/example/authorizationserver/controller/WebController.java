package org.example.authorizationserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 用于提供前端应用的Controller
 */
@Controller
public class WebController {

    /**
     * 当浏览器访问 /login 路径时，转发到静态资源根目录下的 index.html 文件。
     * 这是Vue等单页应用的入口。
     * @return 转发路径
     */
    @GetMapping(value = "/login")
    public String loginPage() {
        return "forward:/index.html";
    }
}
