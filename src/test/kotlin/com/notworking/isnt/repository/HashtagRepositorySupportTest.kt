package com.notworking.isnt.repository

import com.notworking.isnt.model.Hashtag
import com.notworking.isnt.model.Issue
import com.notworking.isnt.model.IssueHashtag
import com.notworking.isnt.support.type.DocType
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback

@SpringBootTest
internal class HashtagRepositorySupportTest(
    @Autowired var issueRepository: IssueRepository,
    @Autowired var hashtagRepository: HashtagRepository,
    @Autowired var issueHashtagRepository: IssueHashtagRepository,
) {

    @Disabled
    @Rollback(value = false)
    @Test
    fun saveHashtagTest() {
        var hashtags = mutableListOf<String>("test1", "test2", "test3")
        var issue = Issue(null, "hashcode test", "hashcode content test", DocType.TEXT)

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
    }

    @Disabled
    @Rollback(value = false)
    @Test
    fun deleteHashtagTest() {
        var issueHashtags = issueHashtagRepository.findAllByIssueId(378);
        issueHashtags.forEach {
            issueHashtagRepository.deleteById(it.id!!)
            if (issueHashtagRepository.countByHashtagId(it.hashtag.id) == 0) {
                hashtagRepository.deleteById(it.hashtag.id!!)
            }
        }
    }

    @Test
    fun findHashtagTest() {
        
    }
}