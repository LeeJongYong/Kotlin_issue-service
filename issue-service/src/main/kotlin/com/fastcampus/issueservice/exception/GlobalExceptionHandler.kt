package com.fastcampus.issueservice.exception

import com.auth0.jwt.exceptions.TokenExpiredException
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice   // 해당 애플리케이션 내 컨트롤러에서 발생하는 모든 Exception을 감지한다.
class GlobalExceptionHandler {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(ServerException::class)
    fun handleServerException(ex: ServerException): ErrorResponse {
        logger.error {ex.message}
        return ErrorResponse(ex.code, ex.message)
    }

    // 토큰 정보 만료 Exception
    // 아래 Exception은 custom Exception과는 무관한 Exception이므로 별도로 핸들러 정의를 해줘야한다.
    @ExceptionHandler(TokenExpiredException::class)
    fun handleTokenExpiredException(ex: TokenExpiredException): ErrorResponse {
        logger.error {ex.message}
        return ErrorResponse(401, "Token Expired Error")
    }

    // custom Exception 이외의 일반적인 Exception처리를 위한 핸들러.
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ErrorResponse {
        logger.error {ex.message}
        // ex.message를 서버 응답으로 사용하면 개발 정보가 노출될 위험이 있다.(logger에서만 확인할 것!!!)
        return ErrorResponse(500, "Internal Server Error")
    }
}