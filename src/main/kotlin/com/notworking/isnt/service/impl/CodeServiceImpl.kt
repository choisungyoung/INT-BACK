package com.notworking.isnt.service.impl

import com.notworking.isnt.model.Code
import com.notworking.isnt.repository.CodeRepository
import com.notworking.isnt.service.CodeService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CodeServiceImpl(

    val codeRepository: CodeRepository,

    ) : CodeService {
    override fun findCodeByCode(code: String): List<Code> {
        return codeRepository.findByCode(code);
    }

    @Transactional
    override fun saveCode(code: Code) {
        codeRepository.save(code)
    }

}