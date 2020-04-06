package com.wp.app.resource.basic.net

import java.io.Serializable

class StatusInfo : Serializable {

    var statusCode: Int = 0

    var statusMessage: String? = null

    val isSuccessful: Boolean
        get() = statusCode == STATUS_SUCCESS

    val isOther: Boolean
        get() = statusCode == STATUS_TOKEN_TIMEOUT || statusCode == STATUS_TOKEN_NOT_FOUND

    val isTokenTimeout: Boolean
        get() = statusCode == STATUS_TOKEN_TIMEOUT

    val isTokenNotFound: Boolean
        get() = statusCode == STATUS_TOKEN_NOT_FOUND && statusMessage == "参数： token 不能为空"

    constructor() {}

    constructor(statusCode: Int) {
        this.statusCode = statusCode
    }

    companion object {
        /** 成功  */
        val STATUS_SUCCESS = 200
        /** Token超时  */
        val STATUS_TOKEN_TIMEOUT = 402
        /** Token未设置  */
        val STATUS_TOKEN_NOT_FOUND = 1006
        /** 自定义错误  */
        val STATUS_CUSTOM_ERROR = 2001
    }
}
