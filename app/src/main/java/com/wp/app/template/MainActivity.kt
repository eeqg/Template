package com.wp.app.template

import com.wp.app.resource.basic.BasicActivity
import com.wp.app.resource.utils.LaunchUtil
import com.wp.app.template.databinding.MainActivityBinding
import com.wp.app.template.ui.login.LoginActivity
import com.wp.app.template.ui.main.MainFragment

class MainActivity : BasicActivity<MainActivityBinding>() {

    override fun getContentView(): Int {
        return R.layout.main_activity
    }

    override fun onInit() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment.newInstance())
            .commitNow()
    }

    override fun initView() {
        LaunchUtil.launchActivity(mActivity, LoginActivity::class.java)
    }
}
