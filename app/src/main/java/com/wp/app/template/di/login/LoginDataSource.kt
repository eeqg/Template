package com.wp.app.template.di.login

import com.wp.app.resource.basic.net.BasicBean
import com.wp.app.resource.basic.net.BasicNetwork
import com.wp.app.resource.utils.LogUtils
import com.wp.app.template.di.model.LoginBean
import io.reactivex.Observable

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    private var service: LoginService =
        BasicNetwork("baseUrl").createService(LoginService::class.java)

    fun login(username: String, password: String): Observable<LoginBean> {
        LogUtils.d("-----currentThread : ${Thread.currentThread().name}")
//        Thread.sleep(2000)
        val fakeUser = LoginBean(java.util.UUID.randomUUID().toString(), username)
        return Observable.just(fakeUser)
    }

    fun logout(params: HashMap<Any, Any>): Observable<BasicBean> {
        // TODO: revoke authentication
        return service.logout(BasicNetwork.mapToRequestBody(params))
    }
}

