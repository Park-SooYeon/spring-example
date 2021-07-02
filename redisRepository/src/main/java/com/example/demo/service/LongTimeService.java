package com.example.demo.service;

import com.example.demo.model.Person;
import com.example.demo.model.PersonRedisRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LongTimeService {

    @Autowired
    private PersonRedisRepository repository;


    public void putRedisModel(Person person) {
        repository.save(person);
    }
}
