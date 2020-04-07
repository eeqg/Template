package com.wp.app.template.di.login

import com.wp.app.resource.basic.net.BasicBean
import com.wp.app.template.di.model.LoginBean
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by wp on 2020/4/6.
 */
interface LoginService {
    @POST("memberApi/phoneRegister")
    fun phoneRegister(@Body params: RequestBody): Observable<LoginBean>

    @POST("memberApi/phoneLogin")
    fun phoneLogin(@Body params: RequestBody): Observable<LoginBean>

    @POST("memberApi/sendCode")
    fun getVerifyCode(@Body params: RequestBody): Observable<BasicBean>

    @POST("memberApi/logout")
    fun logout(@Body params: RequestBody): Observable<BasicBean>
}