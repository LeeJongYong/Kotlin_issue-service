package com.fastcampus.issueservice.service

import com.fastcampus.issueservice.domain.Issue
import com.fastcampus.issueservice.domain.IssueRepository
import com.fastcampus.issueservice.domain.enums.IssueStatus
import com.fastcampus.issueservice.exception.NotFoundException
import com.fastcampus.issueservice.model.IssueRequest
import com.fastcampus.issueservice.model.IssueResponse
import org.hibernate.annotations.NotFound
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

@Service
class issueService(
    private val issueRepository: IssueRepository,
) {

    @Transactional
    fun create(userId: Long, request: IssueRequest) : IssueResponse  {

        val issue = Issue(
            summary = request.summary,
            description = request.description,
            userId = userId,
            type = request.type,
            priority = request.priority,
            status = request.status,
        )

        return IssueResponse(issueRepository.save(issue))

    }

    @Transactional(readOnly = true)
    fun getAll(status: IssueStatus ) =
        issueRepository.findAllByStatusOrderByCreatedAtDesc(status)
            ?.map { IssueResponse(it) }     // map을 통해 건마다 issueResponse 타입으로 변환

    @Transactional(readOnly = true)
    fun get(id: Long): IssueResponse {
        // .findByIdOrNull : Id를 조회한 후 있으면 Id 리턴 없으면 null 리턴
        val issue = issueRepository.findByIdOrNull(id) ?: throw NotFoundException("Issue not Exist!")
        return IssueResponse(issue)
    }

    @Transactional
    fun edit(userId: Long, id: Long, request: IssueRequest): IssueResponse{
        // Id가 존재하는지 확
        val issue: Issue = issueRepository.findByIdOrNull(id) ?: throw NotFoundException("Issue not Exist!")

        return with(issue) {
            summary = request.summary
            description = request.description
            this.userId = userId
            type = request.type
            priority = request.priority
            status = request.status
            IssueResponse(issueRepository.save(this))
        }
    }

    fun delete(id: Long) {

        issueRepository.deleteById(id)

    }


}