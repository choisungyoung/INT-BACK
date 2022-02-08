package com.notworking.isnt.repository

import com.notworking.isnt.model.Developer
import com.notworking.isnt.model.Recommend
import com.notworking.isnt.model.Solution
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecommendRepository : JpaRepository<Recommend, Long> {
    fun findAllBySolutionAndDeveloper(solution: Solution, developer: Developer): Recommend?

    fun findAllByDeveloper(developer: Developer): List<Recommend>
}