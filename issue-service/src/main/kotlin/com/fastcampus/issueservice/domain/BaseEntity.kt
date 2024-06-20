package com.fastcampus.issueservice.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

// @MappedSuperClass : 부모 Entity의 공통 속성을 정의하고 하위 Entity에서 상속받아 사용할 수 있음
@MappedSuperclass
// @EntityListeners : 해당 Entity의 특정 이벤트 발생 시 콜백을 처리하기 위한 Annotation
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity (

    // @CreatedDate : Entity가 생성될 때 자동으로 값을 할당
    @CreatedDate
    var createdAt: LocalDateTime ?= null,

    // @LastModifiedDate : Entity가 수정될 때마다 자동으로 값을 할
    @LastModifiedDate
    var updatedAt: LocalDateTime ?= null,

)