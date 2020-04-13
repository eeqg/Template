package com.wp.app.template.ui.comm

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient
import com.wp.app.resource.basic.BasicActivity
import com.wp.app.resource.utils.LaunchUtil
import com.wp.app.resource.utils.LogUtils
import com.wp.app.resource.utils.StatusBarUtil
import com.wp.app.template.R
import com.wp.app.template.comm.Const
import com.wp.app.template.comm.MainHelper
import com.wp.app.template.databinding.ActivityCommonWebBinding
import java.util.*

class CommonWebActivity : BasicActivity<ActivityCommonWebBinding>() {

    private var mAgentWeb: AgentWeb? = null
    private var mainUrl: String? = null
    private var pageTitle: String? = null
    private var titleColor: String? = null
    private var withoutUserInfo: Boolean = false
    private var fullScreen: Boolean = false

    private val mWebChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            if (TextUtils.isEmpty(pageTitle)) {
                val isShowTitle = intent.getBooleanExtra(Const.ARGS_DATA, true)
                if (isShowTitle) {
                    dataBinding.tvTitle.text = title
                }
            }
        }
    }

    override fun getContentView(): Int {
        return R.layout.activity_common_web
    }

    override fun onInit() {
        val bundle = intent.extras
        if (bundle != null) {
            mainUrl = bundle.getString(Const.ARGS_URL)
            pageTitle = bundle.getString(Const.ARGS_TITLE)
            titleColor = bundle.getString("titleColor")
            withoutUserInfo = bundle.getBoolean(Const.ARGS_TYPE)
            fullScreen = bundle.getBoolean("fullScreen")
        }
    }

    override fun initView() {
        observeTitleBar()
        observeBack()
        observeFinish()
        observeRefresh()
        observeWeb()
    }

    private fun observeTitleBar() {
        if (fullScreen) {
            // setTranslucentStatus();
            dataBinding.toolbar.visibility = View.GONE
            return
        }

        dataBinding.tvTitle.text = pageTitle

        if (!TextUtils.isEmpty(titleColor)) {
            val color = Color.parseColor(titleColor)
            StatusBarUtil.setColor(this, color, 0)
            dataBinding.toolbar.setBackgroundColor(color)
        }
    }

    private fun observeBack() {
        dataBinding.ivBack.setOnClickListener(View.OnClickListener { onBackPressed() })
    }

    private fun observeFinish() {
        this.dataBinding.ivFinish.setOnClickListener(View.OnClickListener { finish() })
    }

    private fun observeRefresh() {
        this.dataBinding.ivRefresh.setOnClickListener(View.OnClickListener { refresh() })
    }

    private fun observeWeb() {
        if (TextUtils.isEmpty(mainUrl)) {
            promptMessage("url is null ??")
            return
        }
        // mainUrl = MainHelper.getInstance().formatH5Path(mainUrl);
        LogUtils.d("-----mainUrl = " + mainUrl!!)
        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(
                dataBinding.flContent as FrameLayout,
                FrameLayout.LayoutParams(-1, -1)
            )
            .useDefaultIndicator(resources.getColor(R.color.colorAccent))
            .setWebViewClient(MyWebViewClient())
            .setWebChromeClient(mWebChromeClient)
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
            .interceptUnkownUrl() //拦截找不到相关页面的Scheme
            .createAgentWeb()
            .ready()
            .go(mainUrl)
        val androidInterface = AndroidInterface(this)
        mAgentWeb!!.jsInterfaceHolder.addJavaObject("shengqianapp", androidInterface)
    }

    private fun getMainUrl(): String? {
        return if (withoutUserInfo) {
            mainUrl
        }
        else MainHelper.instance.formatH5Url(mainUrl!!)
    }

    inner class MyWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            LogUtils.d("onPageStarted-----url = $url")
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
        }
    }

    private fun refresh() {
        if (mAgentWeb != null) {
            mAgentWeb!!.webCreator.webView.reload()
            // mAgentWeb.getUrlLoader().loadUrl(getMainUrl());
            mAgentWeb!!.webCreator.webView.scrollTo(0, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mAgentWeb != null) {
            mAgentWeb!!.webLifeCycle.onResume()
        }
    }

    override fun onPause() {
        if (mAgentWeb != null) {
            mAgentWeb!!.webLifeCycle.onPause()
        }
        super.onPause()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (mAgentWeb!!.handleKeyEvent(keyCode, event)) {
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (!mAgentWeb!!.back() && !fullScreen) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (mAgentWeb != null) {
            mAgentWeb!!.webLifeCycle.onDestroy()
        }
        super.onDestroy()
    }

    companion object {
        fun startActivity(activity: Activity, url: String, title: String) {
            val params = HashMap<String, Any?>()
            params[Const.ARGS_URL] = url
            params[Const.ARGS_TITLE] = title
            LaunchUtil.launchActivity(activity, CommonWebActivity::class.java, params)
        }

        fun startActivity(
            activity: Activity,
            url: String,
            title: String,
            withoutUserInfo: Boolean
        ) {
            val params = HashMap<String, Any?>()
            params[Const.ARGS_URL] = url
            params[Const.ARGS_TITLE] = title
            params[Const.ARGS_TYPE] = withoutUserInfo
            LaunchUtil.launchActivity(activity, CommonWebActivity::class.java, params)
        }

        fun startActivity(
            activity: Activity,
            url: String,
            title: String,
            withoutUserInfo: Boolean,
            showTitle: Boolean
        ) {
            val params = HashMap<String, Any?>()
            params[Const.ARGS_URL] = url
            params[Const.ARGS_TITLE] = title
            params[Const.ARGS_TYPE] = withoutUserInfo
            params[Const.ARGS_DATA] = showTitle
            LaunchUtil.launchActivity(activity, CommonWebActivity::class.java, params)
        }

        fun startActivityForResult(
            fragment: Fragment,
            url: String,
            title: String,
            requestCode: Int
        ) {
            val params = HashMap<String, Any?>()
            params[Const.ARGS_URL] = url
            params[Const.ARGS_TITLE] = title
            LaunchUtil.launchActivityForResult(
                fragment,
                CommonWebActivity::class.java,
                requestCode,
                params
            )
        }

        fun startActivity(activity: Activity, url: String, title: String, titleColor: String) {
            val params = HashMap<String, Any?>()
            params[Const.ARGS_URL] = url
            params[Const.ARGS_TITLE] = title
            params["titleColor"] = titleColor
            LaunchUtil.launchActivity(activity, CommonWebActivity::class.java, params)
        }

        fun startFullActivity(activity: Activity, url: String, title: String) {
            val params = HashMap<String, Any?>()
            params[Const.ARGS_URL] = url
            params[Const.ARGS_TITLE] = title
            params["fullScreen"] = true
            LaunchUtil.launchActivity(activity, CommonWebActivity::class.java, params)
        }
    }
}
