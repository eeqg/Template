package com.wp.app.template.ui.login

import android.util.Patterns
import com.wp.app.resource.basic.BasicViewModel
import com.wp.app.resource.basic.net.ModelLiveData
import com.wp.app.template.di.login.LoginRepository
import com.wp.app.template.di.model.LoginBean
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LoginViewModel(private val loginRepository: LoginRepository) : BasicViewModel() {

    val loginLiveData: ModelLiveData<LoginBean> by lazy {
        ModelLiveData<LoginBean>()
    }

    fun login(username: String, password: String) {
        loginRepository.login(username, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginLiveData.dispose(this))
    }

    fun logout() {

    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}
