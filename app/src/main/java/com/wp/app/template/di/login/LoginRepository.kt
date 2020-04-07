package com.wp.app.template.di.login

import com.wp.app.template.di.model.LoginBean
import io.reactivex.Observable

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    fun login(username: String, password: String): Observable<LoginBean> {
        return dataSource.login(username, password)
    }

    fun logout(params: HashMap<Any, Any>) {
        dataSource.logout(params)
    }
}
