package com.fastcampus.issueservice.domain.enums

enum class IssueType {

    BUG, TASK;

    companion object {

        // operator 키워드를 사용하면 연산자가 된다.
        operator fun invoke (type: String) = valueOf(type.uppercase())

    }

}

fun main(){

    // operator 키워드 선언 후 invoke 함수 사용 시 함수명을 생략한 채로 사용 가능하다.
//    val type = IssueType.of("BUG")
    val type = IssueType("BUG")

}