package com.wp.app.template.ui.splash

import android.Manifest
import com.wp.app.resource.basic.BasicActivity
import com.wp.app.resource.basic.BasicDialogFragment
import com.wp.app.resource.common.AlertDialog
import com.wp.app.resource.common.RxTimerHelper
import com.wp.app.resource.utils.AppPreferences
import com.wp.app.resource.utils.LaunchUtil
import com.wp.app.resource.utils.LogUtils
import com.wp.app.resource.utils.PermissionUtils
import com.wp.app.template.R
import com.wp.app.template.comm.Const
import com.wp.app.template.databinding.ActivitySplashBinding
import com.wp.app.template.ui.MainActivity

class SplashActivity : BasicActivity<ActivitySplashBinding>() {

    private var dineDialog: AlertDialog? = null

    override fun getContentView(): Int {
        return R.layout.activity_splash
    }

    override fun onInit() {
    }

    override fun initView() {
        checkPrivacy()
    }

    private fun checkPrivacy() {
        val haveShowPrivacy = AppPreferences.getBoolean(
            mActivity,
            Const.HAVE_SHOW_PRIVACY,
            false
        )
        if (!haveShowPrivacy) {
            showPrivacyDialog()
            return
        }
        requestPermissions()
    }

    /**
     * 请求权限
     */
    private fun requestPermissions() {
        PermissionUtils.build(this)
            .setOnPermissionListener(object : PermissionUtils.OnPermissionListener() {
                override fun onGranted() {
                    LogUtils.d("-----onGranted-----")
                    RxTimerHelper.get(this@SplashActivity)
                        .timer(2000) {
                            getStarted()
                        }
                }

                override fun onDenied() {
                    if (dineDialog == null) {
                        dineDialog = AlertDialog(this@SplashActivity)
                            .setContent(R.string.permission_denied_tip)
                            .setPositiveClickListener(
                                R.string.re_permission
                            ) { requestPermissions() }
                            .setNegativeClickListener(
                                R.string.quit
                            ) { finish() }
                    }
                    dineDialog?.show()
                }

                override fun onRationale(vararg permissions: String) {
                    this.requestPermission(*permissions)
                }
            })
            .requestPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
    }

    private fun showPrivacyDialog() {
        val dialog = PrivacyDialog()
        dialog.setOnDismissListener(object : BasicDialogFragment.OnDismissListener {
            override fun onDismiss() {
                val haveShowPrivacy =
                    AppPreferences.getBoolean(mActivity, Const.HAVE_SHOW_PRIVACY, false)
                LogUtils.d("-----onDismiss()--$haveShowPrivacy")
                if (!haveShowPrivacy) {
                    finish()
                } else {
                    requestPermissions()
                }
            }
        })
        dialog.show(supportFragmentManager, "privacy")
    }

    private fun getStarted() {
        toHome()
    }

    private fun toHome() {
        LaunchUtil.launchActivity(this, MainActivity::class.java)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        PermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }
}
