package com.wp.app.resource.basic

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.wp.app.resource.R
import com.wp.app.resource.basic.net.StatusInfo
import com.wp.app.resource.basic.net.DataListener
import com.wp.app.resource.common.LoadingDialog
import com.wp.app.resource.utils.LogUtils
import com.wp.app.resource.utils.NetworkUtils

/**
 * Created by wp on 2019/10/28.
 */
abstract class BasicFragment<B : ViewDataBinding> : Fragment(), BasicViewImp, DataListener {
    protected lateinit var dataBinding: B
    protected lateinit var mActivity: Activity
    private lateinit var loadingDialog: LoadingDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, contentView, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(context)

        onInit()
        initView()
        subscribe()
    }

    protected open fun subscribe() {}

    override fun dataStart() {
    }

    override fun dataStop() {
         hideLoading();
    }

    override fun dataOther(statusInfo: StatusInfo?) {
        LogUtils.d("-----dataOther")
        promptMessage("请先登录")
        BasicApp.INSTANCE.requestLogin(mActivity, 99)
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
}