package com.example.demo.aop;

import com.example.demo.model.RedisModel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class RedisAspect {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Around(value = "execution(public * com.example.demo.service.LongTimeService.getRedisModel(..))")
    public Mono<RedisModel> redisModelAspect(ProceedingJoinPoint proceedingJoinPoint) {
        System.out.println("aspect 시작!");
        String name = (String) proceedingJoinPoint.getArgs()[0];
        System.out.println(name);
        return CacheMono
                .lookup(k -> reactiveRedisTemplate.opsForValue()
                        .get(name)
                        .map(message -> RedisModel.builder().name(name).message(message).build())
                        .map(Signal::next), name)
                // LongTimeService에서 동작을 하고 해당 데이터를 받음
                .onCacheMissResume(() -> {
                    try {
                        return (Mono<RedisModel>) proceedingJoinPoint.proceed();
                    } catch (Throwable throwable) {
                        return Mono.error(throwable);
                    }
                })
                // Service를 호출하고 받은 데이터가 존재한다면 redis에 저장
                .andWriteWith((k, sig) -> Mono.fromRunnable(() -> Optional.ofNullable((RedisModel) sig.get())
                        .filter(redisModel -> StringUtils.isNotEmpty(redisModel.getName()))
                        .ifPresent(redisModel -> reactiveRedisTemplate.opsForValue()
                                .set(redisModel.getName(), redisModel.getMessage()).subscribe())));

    }
}
