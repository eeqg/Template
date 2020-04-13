package com.wp.app.template.ui.login

import com.wp.app.resource.basic.BasicFragment
import com.wp.app.resource.common.ToolbarAction
import com.wp.app.template.R
import com.wp.app.template.databinding.FragmentRegisterBinding

/**
 * Created by wp on 2020/4/13.
 */
class RegisterFragment : BasicFragment<FragmentRegisterBinding>() {
    override fun getContentView(): Int {
        return R.layout.fragment_register
    }

    override fun onInit() {
    }

    override fun initView() {
        dataBinding.apply {
            leftAction = ToolbarAction.createBack(context)
        }

    }
}