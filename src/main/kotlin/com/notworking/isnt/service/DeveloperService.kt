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

    fun existsDeveloperByUserId(userId: String): Boolean

    fun existsDeveloperByName(name: String): Boolean

    fun followDeveloper(fromUserId: String, toUserId: String)

    fun findFollowersByUserId(userId: String): Int

    fun existsFollowByUserId(fromUserId: String?, toUserId: String?): Boolean
    
    fun checkAuthNumByUserId(userId: String, authNum: Int): Boolean
}
