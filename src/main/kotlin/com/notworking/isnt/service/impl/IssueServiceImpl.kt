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


    override fun findAllIssue(pageable: Pageable, query: String?, category: String?): Page<Tuple> {

        return issueRepositorySupport.findAllIssuePage(pageable, query, category)
    }

    override fun findAllIssueByEmail(pageable: Pageable, email: String?): Page<Tuple> {

        return issueRepositorySupport.findAllIssuePageByEmail(pageable, email)
    }

    override fun findAllLatestOrder(): List<Issue> {
        return issueRepositorySupport.findWithDeveloper();
    }

    @Transactional
    override fun findIssue(id: Long): Issue {
        //?????? ??????
        var issue = issueRepository.findById(id).orElseThrow { BusinessException(Error.ISSUE_NOT_FOUND) }

        // ????????? ??????, ??? 10???
        issue.solutions = solutionService.findAllSolutionByIssueId(
            PageRequest.of(
                0,
                10
            ), id
        ).content

        // ????????? ??????
        // TODO: ???????????? ???????????? ?????? ????????????
        issue.increaseHit()

        return issue
    }

    @Transactional
    override fun findIssueTemp(email: String): IssueTemp? {
        //?????? ??????
        var issueTemp = issueTempRepository.findByDeveloperEmail(email)

        return issueTemp
    }

    @Transactional
    override fun saveIssue(issue: Issue, email: String, hashtags: List<String>?): Issue {
        var developer = developerService.findDeveloperByEmail(email)

        // ?????? ???????????? ??????
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        // ????????? ??????
        issue.developer = developer


        hashtags?.forEach {
            var hashtag = Hashtag(null, it)
            hashtag.issue = issue
            issue.hashtags.add(hashtag)
        }

        issueRepository.save(issue)
        issueTempRepository.deleteAllByDeveloper(developer)     //???????????? ?????? ??????

        return issue
    }

    @Transactional
    override fun saveIssueTemp(issue: IssueTemp, email: String): IssueTemp {
        var developer = developerService.findDeveloperByEmail(email)

        // ?????? ???????????? ??????
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        // ????????? ??????
        issue.developer = developer

        issueTempRepository.deleteAllByDeveloper(developer)
        issueTempRepository.save(issue)

        return issue
    }

    @Transactional
    override fun updateIssue(newIssue: Issue, hashtags: List<String>?) {

        // ?????? ??????
        var issue = newIssue.id?.let {
            issueRepository.getById(it)
        }
        issue ?: throw BusinessException(Error.ISSUE_NOT_FOUND)
        issue.update(newIssue)

        // ?????? ???????????? ??????
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