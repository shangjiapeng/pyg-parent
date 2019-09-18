package com.pyg.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录相关
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    /**
     * 得到用户名
     * @return
     */
    @RequestMapping("/username")
    public Map<String, Object> username() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("loginName", username);
        return map;
    }


}
