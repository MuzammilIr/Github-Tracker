
package com.example.gitpulse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/";
    }

    @GetMapping("/")
    public String homePage() {
        return "home";
    }
}
