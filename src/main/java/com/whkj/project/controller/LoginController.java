package com.whkj.project.controller;

import com.whkj.project.common.service.ValidationOfCaptcha;
import com.whkj.project.entity.UserEntity;
import com.whkj.project.service.LoginLogService;
import com.whkj.project.utils.MD5Util;
import com.whkj.project.utils.RestResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;


@RestController
public class LoginController {

    @Autowired
    LoginLogService loginLogService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ValidationOfCaptcha validation;

    /**
     * 登录方法
     * @return
     */
    @PostMapping(value = "/login")
    public RestResult login(String username,
                        String password,
                        @RequestParam(value = "rememberMe",defaultValue = "false") Boolean rememberMe,
                        @RequestParam(value = "captcha") String captcha,
                        HttpServletRequest request){
        String data = (String) redisTemplate.opsForValue().get("captcha_" + request.getSession().getId());
        validation.check(captcha,data,request);
        Subject subject = SecurityUtils.getSubject();
        password = MD5Util.encrypt(username.toLowerCase(), password);
        UsernamePasswordToken token = new UsernamePasswordToken(username, password,rememberMe);
        subject.login(token);
        return RestResult.ok();
    }

    /**
     * 注销方法
     */
    @GetMapping(value = "/logout")
    public RestResult logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return RestResult.ok();
    }


    /**
     * 查询日志列表方法
     */

    @GetMapping(value = "/getLoginLog")
    public RestResult getLoginLog(){
        return RestResult.ok(loginLogService.getLoginLog());
    }








}
