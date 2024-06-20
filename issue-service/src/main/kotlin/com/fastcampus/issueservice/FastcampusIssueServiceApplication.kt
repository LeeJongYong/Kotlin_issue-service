package com.fastcampus.issueservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
// @EnableJpaAuditing : EventLister를 Spring Boot에서 실행시켜주기 위한 Annotation
@EnableJpaAuditing
class FastcampusIssueServiceApplication

fun main(args: Array<String>) {
    runApplication<FastcampusIssueServiceApplication>(*args)
}
