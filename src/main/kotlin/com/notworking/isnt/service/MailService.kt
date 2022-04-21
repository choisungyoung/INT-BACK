package com.notworking.isnt.service

interface MailService {
    fun sendFindPasswordMail(email: String)

    fun sendSignUpMail(email: String)
}
