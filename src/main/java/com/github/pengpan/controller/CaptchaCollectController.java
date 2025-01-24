package com.github.pengpan.controller;

import com.github.pengpan.service.CaptchaCollectService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * <p>
 * 验证码采集表 前端控制器
 * </p>
 *
 * @author pengpan
 * @since 2025-01-23 22:50:49
 */
@Controller
@RequestMapping("/captchaCollect")
public class CaptchaCollectController {

    @Resource
    private CaptchaCollectService captchaCollectService;

    @GetMapping("/index")
    @ResponseBody
    public Object index() {
        return captchaCollectService.list();
    }
}
