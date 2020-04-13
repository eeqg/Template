package com.wp.app.template.ui.splash

import android.webkit.JavascriptInterface
import com.wp.app.resource.basic.BasicDialogFragment
import com.wp.app.resource.utils.AppPreferences
import com.wp.app.template.R
import com.wp.app.template.comm.Const
import com.wp.app.template.databinding.DialogPricacyBinding

/**
 * Created by wp on 2019/12/21.
 */
class PrivacyDialog : BasicDialogFragment<DialogPricacyBinding>() {
    override fun getContentView(): Int {
        return R.layout.dialog_pricacy
    }

    override fun onInit() {
        setCanceledTouchOutside(false)
    }

    override fun initView() {
        dataBinding.webView.loadUrl("file:///android_asset/privacy_tips.html")
        dataBinding.webView.settings.javaScriptEnabled = true
        this.dataBinding.webView.addJavascriptInterface(object : Any() {
            @JavascriptInterface
            fun startProtocol() {
                activity!!.runOnUiThread {
//                    CommonWebActivity.startActivity(
//                        activity!!, AppCache.baseH5Url + BaseConst.AGREE_MENT_URL,
//                        resources.getString(R.string.user_agreement)
//                    )
                }
            }

            @JavascriptInterface
            fun startPrivacy() {
                activity!!.runOnUiThread {
//                    CommonWebActivity.startActivity(
//                        activity!!, AppCache.baseH5Url + BaseConst.PRIVATE_GUIDE_URL,
//                        resources.getString(R.string.private_guide)
//                    )
                }
            }
        }, "android")

        dataBinding.tvDisagree.setOnClickListener { dismiss() }
        dataBinding.tvAgree.setOnClickListener {
            AppPreferences.putBoolean(context!!, Const.HAVE_SHOW_PRIVACY, true)
            dismiss()
        }
    }
}
