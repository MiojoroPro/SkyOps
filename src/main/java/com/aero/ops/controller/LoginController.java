package com.aero.ops.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // Page de login
    @GetMapping("/login")
    public String login() {
        return "login/login"; // correspond à src/main/resources/templates/login/login.html
    }

    // Page d'accueil après login
    @GetMapping("/home")
    public String home() {
        return "views/home/home"; // correspond à src/main/resources/templates/views/home.html
    }
    
    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }
}
