package com.notworking.isnt.service.impl

import com.notworking.isnt.model.EmailAuth
import com.notworking.isnt.repository.EmailAuthRepository
import com.notworking.isnt.service.EmailAuthService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class EmailAuthServiceImpl(

    val emailAuthRepository: EmailAuthRepository,

    ) : EmailAuthService {

    @Transactional
    override fun checkAuthNum(email: String, authNum: Int): Boolean {
        var emailAuth = emailAuthRepository.findByEmailAndExpiredDateGreaterThanEqual(email, LocalDateTime.now())
            ?: throw BusinessException(Error.AUTH_NOT_FOUND_AUTH_NUM)

        if (emailAuth.authNum != authNum) {
            throw BusinessException(Error.AUTH_INVALID_AUTH_NUM)
        }

        emailAuth.doAuth()      //인증완료 flag
        return true
    }

    override fun recheckAuthNum(email: String, authNum: Int): Boolean {
        var emailAuth = emailAuthRepository.findByEmailAndExpiredDateGreaterThanEqual(email, LocalDateTime.now())
            ?: throw BusinessException(Error.AUTH_NOT_FOUND_AUTH_NUM)

        if (emailAuth.authNum != authNum) {
            throw BusinessException(Error.AUTH_INVALID_AUTH_NUM)
        }
        
        //인증완료 flag 확인
        if (!emailAuth.isAuth) {
            throw BusinessException(Error.AUTH_NOT_AUTH_AUTH_NUM)
        }

        return true
    }

    override fun findEmailAuthByEmail(email: String): EmailAuth? {
        return emailAuthRepository.findByEmail(email)
    }

    @Transactional
    override fun saveEmailAuth(emailAuth: EmailAuth) {

        var selectEmailAuth = emailAuthRepository.findByEmail(emailAuth.email)

        if (selectEmailAuth == null) {
            emailAuthRepository.save(emailAuth)
        } else {
            selectEmailAuth.update(emailAuth)
        }

    }

}