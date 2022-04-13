package com.notworking.isnt.service

import com.notworking.isnt.repository.DeveloperRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserCustomService(var developerRepository: DeveloperRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails? {
        return developerRepository.findByEmail(username)
        //return developerRepository.findByUserId(username)
    }
}