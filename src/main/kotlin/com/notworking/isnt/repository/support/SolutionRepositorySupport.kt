package com.notworking.isnt.repository.support

import com.notworking.isnt.model.Comment
import com.notworking.isnt.model.QComment.comment
import com.notworking.isnt.model.QIssue.issue
import com.notworking.isnt.model.QSolution.solution
import com.notworking.isnt.model.Solution
import com.notworking.isnt.repository.SolutionRepository
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import javax.annotation.Resource


@Repository
class SolutionRepositorySupport(
    @Resource(name = "jpaQueryFactory")
    var query: JPAQueryFactory
) : QuerydslRepositorySupport(SolutionRepository::class.java) {

    /**
     * 이슈의 솔루션 갯수 조회
     * */
    fun findSolutionCount(issueId: Long): Long {
        var result = query.selectFrom(solution)
            .distinct()
            .where(issue.id.eq(issueId))
            .fetchCount()

        return result
    }

    /**
     * 이슈의 솔루션 채택 존재 여부 조회
     * */
    fun findSolutionAdopt(issueId: Long): Long {
        var result = query.selectFrom(solution)
            .distinct()
            .where(issue.id.eq(issueId).and(solution.adoptYn.isTrue))
            .fetchCount()

        return result
    }

    /**
     * 최신순 솔루션 조회 (이슈상세에서 솔루션 추가조회시 사용)
     * */
    fun findSolutionByIssueId(pageable: Pageable, issueId: Long): Page<Solution> {
        var result = query.selectFrom(solution)
            .distinct()
            .where(issue.id.eq(issueId))
            .orderBy(solution.createdDate.asc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetchResults()

        return PageImpl<Solution>(result.results, pageable, result.total)
    }

    /**
     * 이슈의 모든 솔루션 조회 (이슈상세에서 솔루션 추가조회시 사용)
     * */
    fun findSolutionByIssueId(issueId: Long): List<Solution> {
        var result = query.selectFrom(solution)
            .distinct()
            .where(issue.id.eq(issueId))
            .fetch()

        return result
    }

    /**
     * ManyToOne join
     * */
    fun findCommentBySolutionId(soltuionId: Long): MutableList<Comment> {
        return query.selectFrom(comment)
            .distinct()
            .innerJoin(comment.solution, solution).fetchJoin()
            .where(solution.id.eq(soltuionId))
            .limit(5)
            .orderBy(comment.createdDate.asc())
            .fetch()
    }

}