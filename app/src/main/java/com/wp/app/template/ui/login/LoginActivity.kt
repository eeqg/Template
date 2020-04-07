package com.wp.app.template.ui.login

import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.wp.app.resource.basic.BasicActivity
import com.wp.app.resource.basic.net.DataObserver
import com.wp.app.resource.basic.net.StatusInfo
import com.wp.app.template.R
import com.wp.app.template.databinding.ActivityLoginBinding
import com.wp.app.template.di.model.LoginBean

class LoginActivity : BasicActivity<ActivityLoginBinding>() {

    private lateinit var loginViewModel: LoginViewModel

    override fun getContentView(): Int {
        return R.layout.activity_login
    }

    override fun onInit() {
        loginViewModel =
            ViewModelProvider(this, LoginViewModelFactory()).get(LoginViewModel::class.java)
    }

    override fun initView() {
        dataBinding.login.setOnClickListener {
            loginViewModel.login(
                dataBinding.username.text.toString(),
                dataBinding.password.text.toString()
            )
        }
    }

    private fun updateUiWithUser(model: LoginBean) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun subscribe() {
        super.subscribe()

        loginViewModel.loginLiveData.observe(this,
            object : DataObserver<LoginBean>(this) {
                override fun dataResult(basicBean: LoginBean) {
                    updateUiWithUser(basicBean)
                    finish()
                }

                override fun dataStatus(statusInfo: StatusInfo?) {
                }
            })
    }
}