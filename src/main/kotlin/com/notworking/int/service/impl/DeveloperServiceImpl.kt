package com.notworking.int.service.impl

import com.notworking.int.model.Developer
import com.notworking.int.repository.DeveloperRepository
import com.notworking.int.service.DeveloperService
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class DeveloperServiceImpl(private val developerRepository: DeveloperRepository) : DeveloperService {

    @Transactional
    override fun saveDeveloper(developer: Developer) {
        developerRepository.save(developer)
    }

    override fun findAllDeveloper(): List<Developer> = developerRepository.findAll().toList()

    override fun findDeveloperByEmail(email: String): Developer? = developerRepository.findByEmail(email).orElse(null)

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