package com.notworking.isnt.repository

import com.notworking.isnt.model.Recommend
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecommendRepository : JpaRepository<Recommend, Long> {
}