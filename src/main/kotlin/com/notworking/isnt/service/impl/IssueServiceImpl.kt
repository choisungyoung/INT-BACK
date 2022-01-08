package com.notworking.isnt.service.impl

import com.notworking.isnt.model.Issue
import com.notworking.isnt.repository.IssueRepository
import com.notworking.isnt.repository.IssueRepositorySupport
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.service.IssueService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IssueServiceImpl(

    val issueRepository: IssueRepository,
    val issueRepositorySupport: IssueRepositorySupport,
    val developerService: DeveloperService

) :
    IssueService {

    override fun findAllIssue(): List<Issue> {
        return issueRepository.findAll()
    }

    override fun findAllIssue(pageable: Pageable): Page<Issue> {
        return issueRepository.findAll(pageable)
    }

    override fun findAllLatestOrder(): List<Issue> {
        return issueRepositorySupport.findWithDeveloper();
    }

    override fun findIssue(id: Long): Issue {
        var issue = issueRepository.findById(id).orElseThrow { BusinessException(Error.ISSUE_NOT_FOUND) }
        issue.solutions = issueRepositorySupport.findSolutionByIssueId(id)
        return issue
    }

    @Transactional
    override fun saveIssue(issue: Issue, email: String): Issue {
        var developer = developerService.findDeveloperByEmail(email)

        // 없는 작성자일 경우
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        issue.developer = developer
        issueRepository.save(issue)
        return issue
    }

    @Transactional
    override fun updateIssue(newIssue: Issue) {
        var issue: Issue? = newIssue.id?.let {
            issueRepository.getById(it)
        }

        issue ?: throw BusinessException(Error.ISSUE_NOT_FOUND)
        issue.update(newIssue)

    }

    @Transactional
    override fun deleteIssue(id: Long) {
        issueRepository.deleteById(id)
    }
}