package com.notworking.isnt.service.dto

import com.notworking.isnt.model.Issue
import com.notworking.isnt.model.Solution

data class IssueSolution(
    var issue: Issue,
    var solutions: List<Solution>
)