package com.wkr.user.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/info")
    public String info(){

        return "smart-user 返回用户信息";

    }

}