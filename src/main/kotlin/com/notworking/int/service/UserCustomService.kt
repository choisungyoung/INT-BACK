package com.notworking.int.service

import com.notworking.int.repository.DeveloperRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserCustomService(var developerRepository: DeveloperRepository): UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails? {
x`z        return developerRepository.findByEmail(username!!);
    }
}