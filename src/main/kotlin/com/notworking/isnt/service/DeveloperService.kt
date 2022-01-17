package com.notworking.isnt.service

import com.notworking.isnt.model.Developer

interface DeveloperService {

    fun saveDeveloper(developer: Developer): Unit

    fun findAllDeveloper(): List<Developer>

    fun findDeveloperByUserId(email: String): Developer?

    fun updateDeveloper(developer: Developer)

    fun deleteDeveloper(email: String)
}
