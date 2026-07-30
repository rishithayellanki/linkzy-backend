package com.practice.url_shortner.controller;

import com.practice.url_shortner.service.GreetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetController{
    @Autowired
    private GreetService greetService;
    @GetMapping("/greet")
    public String greet(@RequestParam String name,@RequestParam int age){

        return greetService.buildGreetingMessage(name, age);
    }
    @PostMapping("/greet")
    public String greetPost(@RequestBody GreetRequest request) {
        return greetService.buildGreetingFromRequest(request);
    }
}