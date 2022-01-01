package com.notworking.isnt.repository

import com.notworking.isnt.model.Issue
import com.notworking.isnt.model.QIssue
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

}