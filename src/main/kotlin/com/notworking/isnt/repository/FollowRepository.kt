package com.notworking.isnt.repository

import com.notworking.isnt.model.Developer
import com.notworking.isnt.model.Follow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FollowRepository : JpaRepository<Follow, Long> {
    fun findAllByFromDeveloperAndToDeveloper(fromDeveloper: Developer, toDeveloper: Developer): Follow?

    fun countByToDeveloper(toDeveloper: Developer): Int
}