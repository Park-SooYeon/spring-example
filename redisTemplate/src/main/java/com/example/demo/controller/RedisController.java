package com.example.demo.controller;

import com.example.demo.model.RedisModel;
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

    @GetMapping
    public Mono<String> getRedisModel(@Param("name") String name) {
        return longtimeService.getRedisModel(name)
                .map(redisModel -> redisModel.getName() + " : " + redisModel.getMessage());
    }

    @PostMapping
    public Mono<Void> putRedisModel(@RequestBody RedisModel redisModel) {
        return longtimeService.putRedisModel(redisModel);
    }

}
