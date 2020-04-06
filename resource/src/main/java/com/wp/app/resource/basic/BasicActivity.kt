package com.wp.app.resource.basic

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.wp.app.resource.R
import com.wp.app.resource.basic.net.DataListener
import com.wp.app.resource.basic.net.StatusInfo
import com.wp.app.resource.common.AlertDialog
import com.wp.app.resource.common.LoadingDialog
import com.wp.app.resource.common.ToolbarAction
import com.wp.app.resource.utils.CommonUtil
import com.wp.app.resource.utils.LogUtils
import com.wp.app.resource.utils.NetworkUtils
import com.wp.app.resource.utils.StatusBarUtil


abstract class BasicActivity<B : ViewDataBinding> : AppCompatActivity(), BasicViewImp,
    DataListener {

    protected lateinit var dataBinding: B
    protected lateinit var mActivity: AppCompatActivity
    private lateinit var loadingDialog: LoadingDialog

    private var isLightMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (savedInstanceState == null) {}
        dataBinding = DataBindingUtil.setContentView(this, contentView)
        mActivity = this

        loadingDialog = LoadingDialog(this)

        onInit()
        initView()
        subscribe()

        BasicApp.INSTANCE.onBasicActivityCreated(this)
    }

    protected open fun subscribe() {}

    protected fun setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    protected fun setLightMode() {
        isLightMode = true
        StatusBarUtil.setDarkMode(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, resources.getColor(R.color.colorWhite), 0)
        } else {
            StatusBarUtil.setColor(this, resources.getColor(R.color.colorPrimary), 0)
        }
    }

    protected fun setDarkMode() {
        isLightMode = false
        StatusBarUtil.setLightMode(this)
        StatusBarUtil.setColor(this, resources.getColor(R.color.colorPrimary), 0)
    }

    protected fun setStatusBarColor(resId: Int) {
        StatusBarUtil.setColor(this, resources.getColor(resId), 0)
    }

    protected fun promptMessage(resId: Int) {
        promptMessage(getString(resId))
    }

    protected fun promptMessage(msg: String?) {
        BasicApp.toast(msg ?: "", Gravity.CENTER)
    }

    fun promptMessage(statusInfo: StatusInfo?) {
        if (statusInfo == null) {
            if (!NetworkUtils.isConnected(mActivity)) {
                promptMessage(R.string.please_check_your_network)
            } else {
                promptMessage(R.string.network_request_error)
            }
        } else {
            promptMessage(statusInfo.statusMessage)
        }
    }

    fun promptFailure(statusInfo: StatusInfo?) {
        if (statusInfo == null) {
            if (!NetworkUtils.isConnected(mActivity)) {
                promptMessage(R.string.please_check_your_network)
            } else {
                promptMessage(R.string.network_request_error)
            }
        } else if (!statusInfo.isSuccessful) {
            promptMessage(statusInfo.statusMessage)
        }
    }

    protected fun showLoading() {
        if (!loadingDialog.isShowing) {
            loadingDialog.show()
        }
    }

    protected fun hideLoading() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }

    override fun dataStart() {

    }

    override fun dataStop() {
        hideLoading()
    }

    override fun dataOther(statusInfo: StatusInfo?) {
        LogUtils.d("-----dataOther")
        promptMessage("请先登录")
        BasicApp.INSTANCE.requestLogin(this, 99)
        finish()
    }

    protected fun createBack(): ToolbarAction {
        return if (isLightMode) ToolbarAction.createBack(
            this,
            R.mipmap.ic_fanhui2
        ) else ToolbarAction.createBack(this, R.mipmap.ic_fanhui)
    }

    protected fun checkPermission(permissions: Array<String>): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermissions(permissions)
        } else true
    }

    private fun checkSelfPermissions(permissions: Array<String>): Boolean {
//        LogUtils.d("-----checkSelfPermissions: $permissions")
        var flag = true
        val losePermissions: MutableList<String> = mutableListOf()
        for (p in permissions) {
//            LogUtils.d("-----p.check = ${ActivityCompat.checkSelfPermission(this, p)}")
            if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                flag = false
                losePermissions.add(p)
            }
        }
        if (losePermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, losePermissions.toTypedArray(), 9999)
        }
        return flag
    }

    override fun onResume() {
        super.onResume()
        BasicApp.INSTANCE.onBasicActivityResumed(this)
    }

    override fun onPause() {
        super.onPause()
        BasicApp.INSTANCE.onBasicActivityPaused(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        var hasAllGranted = true
        for (i in grantResults.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                hasAllGranted = false
//                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
//                    showPermissionDeniedDialog()
//                } else {
//                    //权限申请被拒绝 ，但用户未选择'不再提示'选项
//                }
                break
            }
        }
        if (hasAllGranted) {
            onPermissionsResult()
        } else {
            showPermissionDeniedDialog()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog(this)
            .setContent(R.string.permission_denied_tip)
            .setNegativeClickListener(R.string.cancel, null)
            .setPositiveClickListener(R.string.confirm) { CommonUtil.openSettingsApp(this) }
            .show()
    }

    protected open fun onPermissionsResult() {}
}
