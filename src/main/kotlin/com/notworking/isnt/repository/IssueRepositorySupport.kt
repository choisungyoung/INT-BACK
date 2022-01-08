package com.notworking.isnt.repository

import com.notworking.isnt.model.Issue
import com.notworking.isnt.model.QDeveloper.developer
import com.notworking.isnt.model.QIssue
import com.notworking.isnt.model.QIssue.issue
import com.notworking.isnt.model.QSolution.solution
import com.notworking.isnt.model.Solution
import com.querydsl.jpa.impl.JPAQueryFactory
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
    fun findSolutionByIssueId(issueId: Long): MutableList<Solution> {
        return query.selectFrom(solution)
            .distinct()
            .innerJoin(solution.issue, issue).fetchJoin()
            .where(issue.id.eq(issueId))
            .limit(10)
            .orderBy(solution.createdDate.asc())
            .fetch()
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
}