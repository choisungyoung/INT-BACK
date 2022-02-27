package com.notworking.isnt.service.impl

import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.service.MailService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import com.notworking.isnt.support.type.Message
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MailServiceImpl(
    val javaMailSender: JavaMailSender,
    val developerService: DeveloperService
) : MailService {
    @Transactional
    override fun sendAuthMail(userId: String) {
        var developer = developerService.findDeveloperByUserId(userId)
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        if (developer.email == "")
            throw BusinessException(Error.AUTH_GIT_HUB)

        var authNum = (100000..999999).random()

        var simpleMailMessage = SimpleMailMessage()
        simpleMailMessage.setTo(developer.email)
        simpleMailMessage.setSubject(Message.AUTH_EMAIL_TITLE.message)
        simpleMailMessage.setText(String.format(Message.AUTH_EMAIL_MESSAGE.message, authNum))

        javaMailSender.send(simpleMailMessage)
        developer.authNum = authNum
    }

}