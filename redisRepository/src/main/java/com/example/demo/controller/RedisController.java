package com.example.demo.controller;

import com.example.demo.model.Person;
import com.example.demo.service.LongTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {

    private final LongTimeService longtimeService;

    @PostMapping
    public void putRedisModel(@RequestBody Person person) {
        longtimeService.putRedisModel(person);
    }

}
