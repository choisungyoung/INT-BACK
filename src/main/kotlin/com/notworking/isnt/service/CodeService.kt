package com.notworking.isnt.service

import com.notworking.isnt.model.Code

interface CodeService {

    fun findCodeByCode(code: String): List<Code>

    fun saveCode(code: Code)

}
