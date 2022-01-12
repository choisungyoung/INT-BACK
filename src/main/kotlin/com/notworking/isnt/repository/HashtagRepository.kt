package com.notworking.isnt.repository

import com.notworking.isnt.model.Hashtag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HashtagRepository : JpaRepository<Hashtag, Long> {
    fun findByName(name: String?): Hashtag?

}