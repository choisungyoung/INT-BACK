package com.notworking.isnt.service.impl

import com.notworking.isnt.model.Hashtag
import com.notworking.isnt.model.Issue
import com.notworking.isnt.model.IssueTemp
import com.notworking.isnt.repository.HashtagRepository
import com.notworking.isnt.repository.IssueRepository
import com.notworking.isnt.repository.IssueTempRepository
import com.notworking.isnt.repository.support.IssueRepositorySupport
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.service.IssueService
import com.notworking.isnt.service.SolutionService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import com.querydsl.core.Tuple
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IssueServiceImpl(
    val issueRepository: IssueRepository,
    val issueRepositorySupport: IssueRepositorySupport,
    val issueTempRepository: IssueTempRepository,
    val solutionService: SolutionService,
    val developerService: DeveloperService,
    val hashtagRepository: HashtagRepository,
) :
    IssueService {

    override fun findAllIssue(): List<Issue> {
        return issueRepository.findAll()
    }

    override fun findAllIssue(pageable: Pageable): Page<Issue> {
        return issueRepository.findAll(pageable)
    }


    override fun findAllIssue(pageable: Pageable, query: String?): Page<Tuple> {

        return issueRepositorySupport.findAllIssuePage(pageable, query)
    }

    override fun findAllIssueByUserId(pageable: Pageable, userId: String?): Page<Tuple> {

        return issueRepositorySupport.findAllIssuePageByUserId(pageable, userId)
    }

    override fun findAllLatestOrder(): List<Issue> {
        return issueRepositorySupport.findWithDeveloper();
    }

    @Transactional
    override fun findIssue(id: Long): Issue {
        //이슈 조회
        var issue = issueRepository.findById(id).orElseThrow { BusinessException(Error.ISSUE_NOT_FOUND) }

        // 솔루션 조회, 첫 10개
        issue.solutions = solutionService.findAllSolutionByIssueId(
            PageRequest.of(
                0,
                10
            ), id
        ).content

        // 조회수 증가
        // TODO: 방문기록 확인하는 로직 추가하기
        issue.increaseHit()

        return issue
    }

    @Transactional
    override fun findIssueTemp(userId: String): IssueTemp? {
        //이슈 조회
        var issueTemp = issueTempRepository.findByDeveloperUserId(userId)

        return issueTemp
    }

    @Transactional
    override fun saveIssue(issue: Issue, userId: String, hashtags: List<String>?): Issue {
        var developer = developerService.findDeveloperByUserId(userId)

        // 없는 작성자일 경우
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        // 작성사 등록
        issue.developer = developer


        hashtags?.forEach {
            var hashtag = Hashtag(null, it)
            hashtag.issue = issue
            issue.hashtags.add(hashtag)
        }

        issueRepository.save(issue)
        issueTempRepository.deleteAllByDeveloper(developer)     //임시저장 이슈 삭제

        return issue
    }

    @Transactional
    override fun saveIssueTemp(issue: IssueTemp, userId: String): IssueTemp {
        var developer = developerService.findDeveloperByUserId(userId)

        // 없는 작성자일 경우
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        // 작성사 등록
        issue.developer = developer

        issueTempRepository.deleteAllByDeveloper(developer)
        issueTempRepository.save(issue)

        return issue
    }

    @Transactional
    override fun updateIssue(newIssue: Issue, hashtags: List<String>?) {

        // 이슈 조회
        var issue = newIssue.id?.let {
            issueRepository.getById(it)
        }
        issue ?: throw BusinessException(Error.ISSUE_NOT_FOUND)
        issue.update(newIssue)

        // 기존 해시태그 삭제
        issue.hashtags.forEach {
            hashtagRepository.delete(it)
        }
        issue.deleteHashtags()

        hashtags?.forEach {
            var hashtag = Hashtag(null, it)
            hashtag.issue = issue
            hashtagRepository.save(hashtag)
        }
    }

    @Transactional
    override fun deleteIssue(id: Long) {
        var issue = issueRepository.getById(id)

        issue.solutions = solutionService.findAllSolution(issue.id!!).toMutableList()
        issue.solutions.forEach {
            solutionService.deleteSolution(it.id!!)
        }
        issueRepository.delete(issue)
    }
}