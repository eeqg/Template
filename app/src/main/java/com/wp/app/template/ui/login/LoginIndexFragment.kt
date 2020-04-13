package com.wp.app.template.ui.login

import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.wp.app.resource.basic.BasicFragment
import com.wp.app.resource.basic.net.DataObserver
import com.wp.app.resource.basic.net.StatusInfo
import com.wp.app.resource.common.ToolbarAction
import com.wp.app.template.R
import com.wp.app.template.databinding.FragmentLoginIndexBinding
import com.wp.app.template.di.model.LoginBean

/**
 * Created by wp on 2020/4/13.
 */
class LoginIndexFragment : BasicFragment<FragmentLoginIndexBinding>() {
    private lateinit var loginViewModel: LoginViewModel

    override fun getContentView(): Int {
        return R.layout.fragment_login_index
    }

    override fun onInit() {
        loginViewModel =
            ViewModelProvider(this, LoginViewModelFactory()).get(LoginViewModel::class.java)
    }

    override fun initView() {
        dataBinding.apply {
            leftAction = ToolbarAction.createBack(context)

            login.setOnClickListener {
                loginViewModel.login(
                    dataBinding.username.text.toString(),
                    dataBinding.password.text.toString()
                )
            }
        }
    }

    private fun updateUiWithUser(model: LoginBean) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(
            mActivity,
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

                    findNavController().navigate(R.id.action_to_register)
                }

                override fun dataStatus(statusInfo: StatusInfo?) {
                }
            })
    }
}