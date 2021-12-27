package com.notworking.isnt.support.type


/**
 * 권한 enum
 */
enum class Role(val key: String, val title: String) {
    USER("ROLE_USER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "관리자");
}