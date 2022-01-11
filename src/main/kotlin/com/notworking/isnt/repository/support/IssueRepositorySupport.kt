package com.notworking.isnt.repository.support

import com.notworking.isnt.model.Issue
import com.notworking.isnt.model.QDeveloper.developer
import com.notworking.isnt.model.QIssue
import com.notworking.isnt.model.QIssue.issue
import com.notworking.isnt.model.QSolution.solution
import com.notworking.isnt.repository.IssueRepository
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import javax.annotation.Resource

@Repository
class IssueRepositorySupport(
    @Resource(name = "jpaQueryFactory")
    var query: JPAQueryFactory
) : QuerydslRepositorySupport(IssueRepository::class.java) {

    fun findWithDeveloper(): List<Issue> {
        var issue = QIssue.issue;
        return query.selectFrom(issue).fetchAll().fetch()
    }

    /**
     * OneToMany join
     * */
    fun findIssueByIdOtoM(issueId: Long): Issue? {
        return query.selectFrom(issue)
            .distinct()
            .innerJoin(issue.developer, developer).fetchJoin()
            .leftJoin(issue.solutions, solution).fetchJoin()
            .where(issue.id.eq(issueId))
            .limit(10)
            .orderBy(solution.createdDate.asc())
            .fetchOne()
    }

    /**
     * 이슈 검색(제목 + 내용)
     * */
    fun findAllIssueByTitleContentContains(pageable: Pageable, searchQuery: String): Page<Issue> {

        var result = query.selectFrom(issue)
            .where(issue.title.contains(searchQuery).or(issue.content.contains(searchQuery)))
            .orderBy(issue.createdDate.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetchResults()

        return PageImpl(result.results, pageable, result.total)
    }
}