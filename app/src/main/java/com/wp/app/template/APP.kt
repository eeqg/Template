package com.wp.app.template

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.text.TextUtils
import android.view.Gravity
import android.widget.Toast
import androidx.multidex.MultiDex
import com.wp.app.resource.basic.BasicApp
import com.wp.app.template.comm.AppCache

/**
 * Created by wp on 2020/4/6.
 */
class APP : BasicApp() {

    companion object {
        lateinit var INSTANCE: Application

        /**
         * 获取资源文件访问对象
         */
        @JvmStatic
        fun getResource(): Resources {
            return INSTANCE.resources
        }

        @JvmStatic
        fun toast(text: String, gravity: Int) {
            if (TextUtils.isEmpty(text)) {
                return
            }
            val toast = Toast.makeText(INSTANCE, text, Toast.LENGTH_SHORT)
            toast.setGravity(gravity, 0, 0)
            toast.show()
        }

        @JvmStatic
        fun toast(text: String) {
            toast(text, Gravity.CENTER)
        }

        @JvmStatic
        fun toast(resId: Int) {
            toast(getResource().getString(resId))
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        AppCache.appContext = this
//        AppCache.baseUrl = BuildConfig.BASE_URL + BaseConst.DOMAIN_APP
//        AppCache.baseH5Url = BuildConfig.BASE_URL + BaseConst.DOMAIN_H5
    }

    override fun requestLogin(context: Context, requestCode: Int) {
    }

    override fun onBasicActivityCreated(context: Context) {
    }

    override fun onBasicActivityResumed(context: Context) {
    }

    override fun onBasicActivityPaused(context: Context) {
    }
}