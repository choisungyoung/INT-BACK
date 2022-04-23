package com.notworking.isnt.service

import com.notworking.isnt.model.EmailAuth

interface EmailAuthService {

    fun checkAuthNum(email: String, authNum: Int): Boolean

    fun recheckAuthNum(email: String, authNum: Int): Boolean

    fun findEmailAuthByEmail(email: String): EmailAuth?

    fun saveEmailAuth(emailAuth: EmailAuth)
}
