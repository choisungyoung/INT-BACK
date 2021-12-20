package com.notworking.int.controller.dto

import com.notworking.int.model.Developer
import com.notworking.int.support.type.Role

data class LoginDTO(
    var username: String,
    var password: String
)