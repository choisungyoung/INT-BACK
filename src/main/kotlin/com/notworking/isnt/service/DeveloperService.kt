package com.notworking.isnt.service

import com.notworking.isnt.model.Developer

interface DeveloperService {

    fun saveDeveloper(developer: Developer): Unit

    fun findAllDeveloper(): List<Developer>

    fun findDeveloperByName(name: String): Developer?

    fun findDeveloperByEmail(email: String): Developer?

    fun updateDeveloper(developer: Developer): Developer?

    fun updatePasswordDeveloper(email: String, password: String)

    fun deleteDeveloper(email: String)

    fun existsDeveloperByEmail(Email: String): Boolean

    fun existsDeveloperByName(name: String): Boolean

    fun followDeveloper(fromEmail: String, toEmail: String)

    fun findFollowersByEmail(email: String): Int

    fun existsFollowByEmail(fromEmail: String?, toEmail: String?): Boolean

    fun checkAuthNumByEmail(email: String, authNum: Int): Boolean
}
