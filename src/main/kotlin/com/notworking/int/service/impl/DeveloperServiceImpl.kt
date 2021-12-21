package com.notworking.int.service.impl

import com.notworking.int.model.Developer
import com.notworking.int.repository.DeveloperRepository
import com.notworking.int.service.DeveloperService
import lombok.RequiredArgsConstructor
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class DeveloperServiceImpl(val developerRepository: DeveloperRepository, val passwordEncoder: PasswordEncoder) : DeveloperService {

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
        var developer : Developer? = newDeveloper.id?.let { developerRepository.findById(it).get() }
        developer?.apply {
            update(newDeveloper)
        }
    }

    @Transactional
    override fun deleteDeveloper(id : Long) {
        var developer : Developer = developerRepository.findById(id).get()
        developerRepository.delete(developer)
    }

}