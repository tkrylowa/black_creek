package ru.spring.tkrylova.blackcreek.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login() {
        return "account/login";
    }

    @GetMapping("/login-error")
    public String loginError() {
        return "account/login-error";
    }
}
