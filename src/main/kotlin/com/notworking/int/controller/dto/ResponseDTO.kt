package com.notworking.int.controller.dto


data class ResponseDTO(
    var code: Int?,
    var message: String?,
    var item: Any?
) {
    constructor() : this(null, null, null)
}
