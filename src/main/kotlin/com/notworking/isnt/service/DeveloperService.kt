package com.notworking.isnt.service

import com.notworking.isnt.model.Developer

interface DeveloperService {

    fun saveDeveloper(developer: Developer): Unit

    fun findAllDeveloper(): List<Developer>

    fun findDeveloperByName(name: String): Developer?

    fun findDeveloperByUserId(userId: String): Developer?

    fun updateDeveloper(developer: Developer): Developer?

    fun updatePasswordDeveloper(userId: String, password: String)

    fun deleteDeveloper(userId: String)

    fun existDeveloperByUserId(userId: String): Boolean

    fun existDeveloperByName(name: String): Boolean
}
