package com.notworking.isnt

import com.notworking.isnt.model.Developer
import com.notworking.isnt.model.Issue
import com.notworking.isnt.model.Solution
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.service.IssueService
import com.notworking.isnt.service.SolutionService
import com.notworking.isnt.support.type.DocType
import mu.KotlinLogging
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Transactional
@SpringBootTest
class TestData(
    @Autowired var developerService: DeveloperService,
    @Autowired var issueService: IssueService,
    @Autowired var solutionService: SolutionService,
) {
    @Disabled
    @Test
    @Rollback(value = false)
    fun saveTestData() {
        var issueId: Long = 0
        developerService.saveDeveloper(
            Developer(
                id = null,
                userId = "test",
                email = "test@naver.com",
                pwd = "aa12345^",
                name = "test",
                introduction = "안녕하세요",
                gitUrl = "test git url",
                webSiteUrl = "test web site url",
                pictureUrl = "testUrl",
                point = 0,
                popularity = 0,
            )
        )

        for (i: Int in 1..24)
            issueId = issueService.saveIssue(
                Issue(
                    id = null,
                    title = "Test Title" + i,
                    content = "Test content" + i,
                    docType = DocType.TEXT
                ),
                "test@naver.com", mutableListOf("spring", "test", "junit")
            ).id!!

        for (i: Int in 1..24)
            solutionService.saveSolution(
                Solution(
                    id = null,
                    content = "Test content" + i,
                    docType = DocType.TEXT,
                ),
                "test@naver.com",
                issueId = issueId
            )
    }

    @Disabled
    @Test
    @Rollback(value = false)
    fun saveIssueTest() {
        issueService.saveIssue(
            Issue(
                id = null,
                title = "Test Title",
                content = """Test content
                <h2>TEST</h2><h2>우리도이제 이런 툴이 생겻어</h2><div data-language=\"javascript\" class=\"toastui-editor-ww-code-block-highlighting\"><pre class=\"language-javascript\"><code data-language=\"javascript\" class=\"language-javascript\"><span class=\"token keyword\">const</span> a <span class=\"token operator\">=</span> <span class=\"token punctuation\">[</span><span class=\"token string\">'1'</span><span class=\"token punctuation\">,</span><span class=\"token string\">'2'</span><span class=\"token punctuation\">,</span><span class=\"token string\">'3'</span><span class=\"token punctuation\">]</span></code></pre></div>
                """.trimMargin(),
                docType = DocType.TEXT
            ),
            "test@naver.com",
            mutableListOf("spring", "test", "junit")
        )
    }
}
