package com.notworking.int.service

import com.notworking.int.model.Developer

interface DeveloperService {

    fun saveDeveloper(developer: Developer) : Unit

    fun findAllDeveloper() : List<Developer>

    fun findDeveloperByEmail(email : String) : Developer?

    fun updateDeveloper(developer: Developer)

    fun deleteDeveloper(id : Long)
}
