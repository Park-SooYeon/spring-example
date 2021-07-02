package com.example.demo.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@RedisHash("people")
public class Person {
    @Id
    String id;
    String name;
    String address;

    @Builder
    public Person(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }
}
