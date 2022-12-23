package com.example.payhere.shared.configuration;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
@RequiredArgsConstructor
public class QuerydslConfiguration {

    @PersistenceContext
    private final EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory(){

        return new JPAQueryFactory(em);
    }
}