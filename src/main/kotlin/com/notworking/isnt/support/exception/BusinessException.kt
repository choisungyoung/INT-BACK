package com.notworking.isnt.support.exception

import com.notworking.isnt.support.type.Error

class BusinessException(
    var error: Error,
) : RuntimeException(error.message)