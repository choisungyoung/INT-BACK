package com.notworking.isnt.service.impl

import com.notworking.isnt.model.Developer
import com.notworking.isnt.repository.DeveloperRepository
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeveloperServiceImpl(
    val developerRepository: DeveloperRepository,
    val passwordEncoder: PasswordEncoder
) :
    DeveloperService {

    @Transactional
    override fun saveDeveloper(developer: Developer) {
        developer.let {
            it.pwd = passwordEncoder.encode(it.pwd)
            developerRepository.save(it)
        }
    }

    override fun findAllDeveloper(): List<Developer> {
        return developerRepository.findAll().toList()
    }

    override fun findDeveloperByName(name: String): Developer? {
        return developerRepository.findByName(name)
    }

    override fun findDeveloperByUserId(userId: String): Developer? {
        return developerRepository.findByUserId(userId)
    }

    @Transactional
    override fun updateDeveloper(newDeveloper: Developer) {
        var developer: Developer? = newDeveloper.userId?.let { developerRepository.findByUserId(it) }

        // null일 경우 예외처리
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        developer.update(newDeveloper)
    }

    @Transactional
    override fun deleteDeveloper(userId: String) {
        var developer: Developer? = developerRepository.findByUserId(userId)

        // null일 경우 예외처리
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        developerRepository.delete(developer)
    }

    override fun existDeveloperByUserId(userId: String): Boolean {
        return developerRepository.existsByUserId(userId)
    }

    override fun existDeveloperByName(name: String): Boolean {
        return developerRepository.existsByName(name)
    }
}