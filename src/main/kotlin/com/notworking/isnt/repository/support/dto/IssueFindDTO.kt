package com.notworking.isnt.repository.support.dto

import com.notworking.isnt.model.Developer


data class IssueFindDTO(
    var id: Long,
    var title: String,
    var content: String,
    var docType: String,
    var hits: Long,
    var recommendationCount: Long,
    var solutionCount: Long,
    var adoptYn: Boolean,
    var hashtags: List<String>,

    var developer: Developer,
    var modifiedDate: String,
) {
    /*
    @JsonProperty("isAdopt")
    fun isAdopt(isAdopt: Boolean) {
        this.isAdopt = isAdopt.toString()
    }*/
}