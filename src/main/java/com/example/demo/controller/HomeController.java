package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class HomeController {
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("title", "Dashboard");

        model.addAttribute("content", "home/index :: content");

        return "layout/main";
    }
}
