package com.notworking.isnt.service.impl

import com.notworking.isnt.model.Developer
import com.notworking.isnt.repository.DeveloperRepository
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class DeveloperServiceImpl(val developerRepository: DeveloperRepository, val passwordEncoder: PasswordEncoder) :
    DeveloperService {

    @Transactional
    override fun saveDeveloper(developer: Developer) {
        developer.let {
            it.pwd = passwordEncoder.encode(it.pwd)
            developerRepository.save(it)
        }
    }

    override fun findAllDeveloper(): List<Developer> = developerRepository.findAll().toList()

    override fun findDeveloperByEmail(email: String): Developer? = developerRepository.findByEmail(email)

    @Transactional
    override fun updateDeveloper(newDeveloper: Developer) {
        var developer: Developer? = newDeveloper.email?.let { developerRepository.findByEmail(it) }

        // null일 경우 예외처리
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        developer.update(newDeveloper)
    }

    @Transactional
    override fun deleteDeveloper(email: String) {
        var developer: Developer? = developerRepository.findByEmail(email)

        // null일 경우 예외처리
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        developerRepository.delete(developer)
    }

}