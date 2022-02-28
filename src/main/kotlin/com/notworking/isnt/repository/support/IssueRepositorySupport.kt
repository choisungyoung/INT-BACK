package com.notworking.isnt.repository.support

import com.notworking.isnt.model.Issue
import com.notworking.isnt.model.QDeveloper.developer
import com.notworking.isnt.model.QHashtag.hashtag
import com.notworking.isnt.model.QIssue
import com.notworking.isnt.model.QIssue.issue
import com.notworking.isnt.model.QSolution.solution
import com.notworking.isnt.repository.IssueRepository
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.Tuple
import com.querydsl.jpa.JPAExpressions.select
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

    fun findAllIssuePage(pageable: Pageable, searchQuery: String?): Page<Tuple> {
        val builder = BooleanBuilder()
        if (searchQuery != null) {
            builder.and(issue.title.contains(searchQuery).or(issue.content.contains(searchQuery)))
        }

        var result = query.selectDistinct(
            issue,
            select(solution.id.count()).from(solution).where(solution.issue.eq(issue)),
            select(solution.id.count()).from(solution).where(solution.issue.eq(issue).and(solution.adoptYn.isTrue)),
        )
            .from(issue)
            .leftJoin(issue.hashtags, hashtag)
            .fetchJoin()
            .where(builder)
            .orderBy(issue.createdDate.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetchResults()

        return PageImpl(result.results, pageable, result.total)
    }

    fun findAllIssuePageByUserId(pageable: Pageable, userId: String?): Page<Tuple> {
        val builder = BooleanBuilder()

        if (userId != null) {
            builder.and(issue.developer.userId.eq(userId))
        }

        var result = query.selectDistinct(
            issue,
            select(solution.id.count()).from(solution).where(solution.issue.eq(issue)),
            select(solution.id.count()).from(solution).where(solution.issue.eq(issue).and(solution.adoptYn.isTrue)),
        )
            .from(issue)
            .where(builder)
            .orderBy(issue.createdDate.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetchResults()

        return PageImpl(result.results, pageable, result.total)
    }

}