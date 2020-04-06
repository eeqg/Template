package com.wp.app.resource.basic

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast

/**
 * Created by wp on 2019/10/11.
 */
abstract class BasicApp : Application() {

    companion object {
        lateinit var INSTANCE: BasicApp

        /**
         * 获取资源文件访问对象
         */
        @JvmStatic
        fun getResource(): Resources {
            return INSTANCE.resources
        }

        @JvmStatic
        fun getScreenWidth(): Int {
            val windowManager = INSTANCE.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
            val display = windowManager!!.defaultDisplay
            val displayMetrics = DisplayMetrics()
            display.getMetrics(displayMetrics)
            val isPortrait = displayMetrics.widthPixels < displayMetrics.heightPixels
            return if (isPortrait) displayMetrics.widthPixels else displayMetrics.heightPixels
        }

        @JvmStatic
        fun getScreenHeight(): Int {
            val windowManager = INSTANCE.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
            val display = windowManager!!.defaultDisplay
            val displayMetrics = DisplayMetrics()
            display.getMetrics(displayMetrics)
            val isPortrait = displayMetrics.widthPixels < displayMetrics.heightPixels
            return if (isPortrait) displayMetrics.heightPixels else displayMetrics.widthPixels
        }

        fun toast(text: String) {
            toast(text, Gravity.CENTER)
        }

        fun toast(text: String, gravity: Int) {
            if (TextUtils.isEmpty(text)) {
                return
            }
            val toast = Toast.makeText(INSTANCE, text, Toast.LENGTH_SHORT)
            toast.setGravity(gravity, 0, 0)
            toast.show()
        }
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

    }

    abstract fun requestLogin(context: Context, requestCode: Int)

    abstract fun onBasicActivityCreated(context: Context)

    abstract fun onBasicActivityResumed(context: Context)

    abstract fun onBasicActivityPaused(context: Context)
}