package com.selfengineerjourney.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @GetMapping
    public String hello() {
        return "Hello World";
    }

    @GetMapping("/demo")
    public String auth() {
        return "Secured Ressource";
    }
}
