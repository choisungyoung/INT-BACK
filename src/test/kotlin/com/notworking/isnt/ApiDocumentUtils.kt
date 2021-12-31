package com.notworking.isnt

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors.*


interface ApiDocumentUtils {
    companion object {
        // 1
        //2
        val documentRequest: OperationRequestPreprocessor?
            get() = preprocessRequest(
                modifyUris() // 1
                    .scheme("https")
                    .host("docs.api.com")
                    .removePort(),
                prettyPrint()
            ) //2

        // 3
        val documentResponse: OperationResponsePreprocessor?
            get() = preprocessResponse(prettyPrint())
    }
}