package com.notworking.isnt.service.impl

import com.notworking.isnt.model.EmailAuth
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.service.EmailAuthService
import com.notworking.isnt.service.MailService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import com.notworking.isnt.support.type.Message
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class MailServiceImpl(
    val javaMailSender: JavaMailSender,
    val developerService: DeveloperService,
    val emailAuthService: EmailAuthService,
) : MailService {

    val INTERVAL_TIME = 5L

    @Transactional
    override fun sendFindPasswordMail(email: String) {

        var developer = developerService.findDeveloperByEmail(email)
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        if (developer.email == "")
            throw BusinessException(Error.AUTH_GIT_HUB)


        var authNum = (100000..999999).random()

        // 이메일 인증정보 저장 
        emailAuthService.saveEmailAuth(
            EmailAuth(
                id = null,
                email = email,
                authNum = authNum,
                isAuth = false,
                expiredDate = LocalDateTime.now().plusMinutes(INTERVAL_TIME),     //5분
            )
        )

        var simpleMailMessage = SimpleMailMessage()
        simpleMailMessage.setTo(developer.email)
        simpleMailMessage.setSubject(Message.AUTH_EMAIL_TITLE.message)
        simpleMailMessage.setText(String.format(Message.AUTH_FIND_PASSWORD_EMAIL_MESSAGE.message, authNum))

        javaMailSender.send(simpleMailMessage)
    }

    @Transactional
    override fun sendSignUpMail(email: String) {

        var authNum = (100000..999999).random()

        // 이메일 인증정보 저장
        emailAuthService.saveEmailAuth(
            EmailAuth(
                id = null,
                email = email,
                authNum = authNum,
                isAuth = false,
                expiredDate = LocalDateTime.now().plusMinutes(INTERVAL_TIME),     //5분
            )
        )

        // 인증메일 전송
        var simpleMailMessage = SimpleMailMessage()
        simpleMailMessage.setTo(email)
        simpleMailMessage.setSubject(Message.AUTH_EMAIL_TITLE.message)
        simpleMailMessage.setText(String.format(Message.AUTH_SIGN_UP_EMAIL_MESSAGE.message, authNum))

        javaMailSender.send(simpleMailMessage)
    }
}