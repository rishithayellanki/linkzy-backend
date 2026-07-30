package com.practice.url_shortner.service;
import com.practice.url_shortner.controller.GreetRequest;
import org.springframework.stereotype.Service;

@Service

public class GreetService {
    public String buildGreetingMessage(String name, int age) {
        return "Hello " + name + ", you are " + age + " years old!";
    }

    public String buildGreetingFromRequest(GreetRequest request) {
        return "Hello " + request.getName() + ", you are " + request.getAge() + " years old! (via POST)";
    }
}
