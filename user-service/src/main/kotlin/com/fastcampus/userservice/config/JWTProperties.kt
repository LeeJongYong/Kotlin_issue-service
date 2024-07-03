package com.fastcampus.userservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

// @ConfigurationProperties : application.yml에 선언해둔 properties를 가져오기 위한 annotation
@ConfigurationProperties(prefix = "jwt")
// 클래스에 생성자 값이 추가됨.
@ConstructorBinding
data class JWTProperties(
    val issuer: String,
    val subject: String,
    val expiresTime: Long,
    val secret: String,
)