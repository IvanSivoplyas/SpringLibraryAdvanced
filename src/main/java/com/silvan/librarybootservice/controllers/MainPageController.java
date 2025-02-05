package com.silvan.librarybootservice.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {
    @GetMapping("/mainpage")
    public String mainpage(){
        return "mainpage";
    }
}
