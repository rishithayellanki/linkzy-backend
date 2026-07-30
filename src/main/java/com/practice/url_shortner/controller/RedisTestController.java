package com.practice.url_shortner.controller;

import com.practice.url_shortner.service.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/redis-test")
public class RedisTestController {

    @Autowired
    private RedisCacheService redisCacheService;

    @PostMapping("/set")
    public String setValue(@RequestParam String key, @RequestParam String value) {
        redisCacheService.saveValue(key, value);
        return "Saved: " + key + " -> " + value;
    }

    @GetMapping("/get/{key}")
    public String getValue(@PathVariable String key) {
        String value = redisCacheService.getValue(key);
        if (value == null) {
            return "Key not found: " + key;
        }
        return key + " -> " + value;
    }

    @PostMapping("/set-with-expiry")
    public String setWithExpiry(@RequestParam String key, @RequestParam String value, @RequestParam long seconds) {
        redisCacheService.saveValueWithExpiry(key, value, seconds);
        return "Saved: " + key + " -> " + value + " (expires in " + seconds + " seconds)";
    }

    @DeleteMapping("/delete/{key}")
    public String deleteValue(@PathVariable String key) {
        redisCacheService.deleteValue(key);
        return "Deleted: " + key;
    }

    @GetMapping("/exists/{key}")
    public String checkExists(@PathVariable String key) {
        boolean exists = redisCacheService.keyExists(key);
        return key + " exists: " + exists;
    }
}
