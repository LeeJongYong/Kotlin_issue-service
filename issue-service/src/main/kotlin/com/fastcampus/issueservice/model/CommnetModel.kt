package com.fastcampus.issueservice.model

import com.fastcampus.issueservice.domain.Comment

data class CommentRequest (
    val body: String,
)

data class CommentResponse(
    val id: Long,
    val issueId: Long,
    val userId: Long,
    val body: String,
    val userName: String,
)

// 확장함수를 이용한 DTO변환
fun Comment.toResponse() = CommentResponse(
    id = id!!,
    issueId = issue.id!! ,
    userId = userId,
    body = body,
    userName = userName,
)