package com.fastcampus.userservice.exception

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import mu.KotlinLogging
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Configuration
class GlobalExceptionHandler(private val objcetMapper: ObjectMapper): ErrorWebExceptionHandler {

    private val logger = KotlinLogging.logger{}

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> = mono {    // mono 키워드로 감싸면 coroutine 형식으로 사용가능하다.
        logger.error { ex.message }

        // 지정한 Exception 발생 시 지정 code 및 message를 출력하고
        // 그 외 기타 Exception 발생 시 500 에러로 처리
        val errorResponse = if (ex is ServerException) {
            ErrorResponse(code = ex.code, message = ex.message)
        }
        else {
            ErrorResponse(code = 500, message = "Internal Server Error")
        }

        with(exchange.response) {
            statusCode = HttpStatus.OK                              // 에러 종류에 상관없이 화면에 출력될 수 있게 HttpStatus는 OK(성공)으로 처리
            headers.contentType = MediaType.APPLICATION_JSON        // 헤더 컨텐츠 타입은 JSON으로 지정

            val dataBuffer = bufferFactory().wrap(objcetMapper.writeValueAsBytes(errorResponse))
            // Byte타입으로 변환된 errorResponse를 mono타입으로 형변
            // .awiitSingle() 사용해 coroutine으로 변환
            writeWith(dataBuffer.toMono()).awaitSingle()
        }
    }


}