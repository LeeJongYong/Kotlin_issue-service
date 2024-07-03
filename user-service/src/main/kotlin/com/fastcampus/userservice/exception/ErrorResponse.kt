package com.fastcampus.userservice.exception

data class ErrorResponse(
    val code: Int,              // 에러코드
    val message: String,        // 에러메시지
)
