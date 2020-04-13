package com.wp.app.template.di.model

import com.wp.app.resource.basic.net.BasicBean
import com.wp.app.resource.utils.AppPreferences
import com.wp.app.template.APP

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
class LoginBean : BasicBean() {
    var userId: String? = ""
    var displayName: String? = ""

    @Synchronized
    fun save() {
        INSTANCE = this

        AppPreferences.putObject(APP.INSTANCE, "login", INSTANCE)
    }

    companion object {
        @Volatile
        private var INSTANCE: LoginBean? = null

        /**
         * 读取用户信息
         *
         * @return 用户信息
         */
        @Synchronized
        fun read(): LoginBean? {
            if (INSTANCE != null) {
                return INSTANCE
            }
            INSTANCE = AppPreferences.getObject(APP.INSTANCE, "login", LoginBean::class.java)
            return INSTANCE
        }

        /**
         * 删除用户信息
         */
        @Synchronized
        fun remove() {
            INSTANCE = null
            AppPreferences.putObject(APP.INSTANCE, "login", null)
        }
    }
}
