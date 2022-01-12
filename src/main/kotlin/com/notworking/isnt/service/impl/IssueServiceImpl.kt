package com.notworking.isnt.service.impl

import com.notworking.isnt.model.Hashtag
import com.notworking.isnt.model.Issue
import com.notworking.isnt.model.IssueHashtag
import com.notworking.isnt.repository.HashtagRepository
import com.notworking.isnt.repository.IssueHashtagRepository
import com.notworking.isnt.repository.IssueRepository
import com.notworking.isnt.repository.support.IssueRepositorySupport
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.service.IssueService
import com.notworking.isnt.service.SolutionService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IssueServiceImpl(
    val issueRepository: IssueRepository,
    val issueRepositorySupport: IssueRepositorySupport,
    val solutionServicce: SolutionService,
    val developerService: DeveloperService,
    val hashtagRepository: HashtagRepository,
    val issueHashtagRepository: IssueHashtagRepository

) :
    IssueService {

    override fun findAllIssue(): List<Issue> {
        return issueRepository.findAll()
    }

    override fun findAllIssue(pageable: Pageable): Page<Issue> {
        return issueRepository.findAll(pageable)
    }


    override fun findAllIssueByTitleContent(pageable: Pageable, query: String?): Page<Issue> {
        var page: Page<Issue>
        if (query == null) {
            page = issueRepository.findAll(pageable)
        } else {
            page = issueRepositorySupport.findAllIssueByTitleContentContains(pageable, query)
        }

        page.content.forEach {
            it.issueHashtags = issueHashtagRepository.findAllByIssueId(it.id)
        }
        return page
    }


    override fun findAllLatestOrder(): List<Issue> {
        return issueRepositorySupport.findWithDeveloper();
    }

    @Transactional
    override fun findIssue(id: Long): Issue {
        //이슈 조회
        var issue = issueRepository.findById(id).orElseThrow { BusinessException(Error.ISSUE_NOT_FOUND) }

        // 솔루션 조회, 첫 10개
        issue.solutions = solutionServicce.findAllSolutionWithComment(
            PageRequest.of(
                0,
                10
            ), id
        ).content

        // 조회수 증가
        // TODO: 방문기록 확인하는 로직 추가하기
        issue.increaseHit()

        // 해시태그조회
        issue.issueHashtags = issueHashtagRepository.findAllByIssueId(issue.id)

        return issue
    }

    @Transactional
    override fun saveIssue(issue: Issue, email: String, hashtags: List<String>): Issue {
        var developer = developerService.findDeveloperByEmail(email)

        // 없는 작성자일 경우
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        // 작성사 등록
        issue.developer = developer

        hashtags.forEach {
            // 해시태그 조회
            var hashtag = hashtagRepository.findByName(it)
            if (hashtag == null) {
                // 없을 경우 추가
                hashtag = hashtagRepository.save(Hashtag(null, name = it))
            }
            var issueHashtag = IssueHashtag(null)
            issueHashtag.issue = issue
            issueHashtag.hashtag = hashtag
            issue.addIssueHashtag(issueHashtag)
        }
        issueRepository.save(issue)
        return issue
    }

    @Transactional
    override fun updateIssue(newIssue: Issue, hashtags: List<String>) {

        // 이슈 조회
        var issue = newIssue.id?.let {
            issueRepository.getById(it)
        }
        issue ?: throw BusinessException(Error.ISSUE_NOT_FOUND)
        issue.update(newIssue)

        // 기존 해시태그 조회
        var issueHashtags = issueHashtagRepository.findAllByIssueId(issue.id)
        issueHashtags.forEach {
            issueHashtagRepository.deleteById(it.id!!)
            if (issueHashtagRepository.countByHashtagId(it.hashtag.id) == 0) {
                hashtagRepository.deleteById(it.hashtag.id!!)
            }
        }
        // 새로운 해시태그 추가
        hashtags.forEach {
            // 해시태그 조회
            var hashtag = hashtagRepository.findByName(it)
            if (hashtag == null) {
                // 없을 경우 추가
                hashtag = hashtagRepository.save(Hashtag(null, name = it))
            }
            var issueHashtag = IssueHashtag(null)
            issueHashtag.issue = issue
            issueHashtag.hashtag = hashtag
            issue.addIssueHashtag(issueHashtag)
        }
    }

    @Transactional
    override fun deleteIssue(id: Long) {
        solutionServicce.findAllSolution(id).forEach {
            solutionServicce.deleteSolution(it.id ?: throw BusinessException(Error.SOLUTION_NOT_FOUND))
        }
        issueRepository.deleteById(id)
    }
}