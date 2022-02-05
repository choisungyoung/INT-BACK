package com.notworking.isnt.repository

import com.notworking.isnt.model.Issue
import com.notworking.isnt.support.type.DocType
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback

@Disabled
@SpringBootTest
internal class HashtagRepositorySupportTest(
    @Autowired var issueRepository: IssueRepository,
    @Autowired var hashtagRepository: HashtagRepository,
) {

    @Disabled
    @Rollback(value = false)
    @Test
    fun saveHashtagTest() {
        var hashtags = mutableListOf<String>("test1", "test2", "test3")
        var issue = Issue(null, "hashcode test", "hashcode content test", DocType.TEXT)

        hashtags.forEach {
            // 해시태그 추가

            //hashtagRepository.save(hashtag)(Hashtag(null, it))
        }

        issueRepository.save(issue)
    }

}