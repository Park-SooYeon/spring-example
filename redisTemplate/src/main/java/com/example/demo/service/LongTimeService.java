package com.example.demo.service;

import com.example.demo.model.RedisModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LongTimeService {

    // redisModel 정보를 저장할 List
    private List<RedisModel> redisModelList = new ArrayList<>();

    // 조회시, 5초가 걸리는 service
    public Mono<RedisModel> getRedisModel(String name) {
        return Mono.just(redisModelList)
                .map(redisModels -> redisModels.stream().filter(redisModel -> redisModel.getName().equals(name))
                        .findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .switchIfEmpty(Mono.empty())
                .delayElement(Duration.ofSeconds(3));
    }

    public Mono<Void> putRedisModel(RedisModel redisModel) {
        return Mono.just(redisModel)
                .filter(r -> StringUtils.isNotEmpty(r.getName()))
                .doOnNext(r -> redisModelList.add(r))
                .then();
    }
}
