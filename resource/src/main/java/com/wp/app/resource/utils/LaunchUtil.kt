package com.wp.app.resource.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.fragment.app.Fragment
import java.io.Serializable
import java.util.*

/**
 * Created by wp on 2018/7/4.
 */

object LaunchUtil {

    @JvmOverloads
    fun launchActivity(
        context: Context,
        clazz: Class<out Activity>,
        params: HashMap<String, Any?>? = null
    ) {
        context.startActivity(createIntent(context, clazz, false, params))
    }

    @JvmOverloads
    fun launchActivityForResult(
        activity: Activity,
        clazz: Class<out Activity>,
        requestCode: Int,
        params: HashMap<String, Any?>? = null
    ) {
        activity.startActivityForResult(createIntent(activity, clazz, true, params), requestCode)
    }

    @JvmOverloads
    fun launchActivityForResult(
        fragment: Fragment,
        clazz: Class<out Activity>,
        requestCode: Int,
        params: HashMap<String, Any?>? = null
    ) {
        fragment.startActivityForResult(
            fragment.context?.let { createIntent(it, clazz, true, params) },
            requestCode
        )
    }

    private fun createIntent(
        context: Context,
        clazz: Class<out Activity>,
        forResult: Boolean,
        params: HashMap<String, Any?>?
    ): Intent {
        val intent = Intent(context, clazz)
        if (!forResult) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (params != null && params.size > 0) {
            val entrySet = params.entries
            for ((key, value) in entrySet) {
                if (value is String) {
                    intent.putExtra(key, value)
                }
                if (value is Boolean) {
                    intent.putExtra(key, value)
                }
                if (value is Int) {
                    intent.putExtra(key, value)
                }
                if (value is Float) {
                    intent.putExtra(key, value)
                }
                if (value is Double) {
                    intent.putExtra(key, value)
                }
                if (value is Parcelable) {
                    intent.putExtra(key, value)
                }
                if (value is Serializable) {
                    intent.putExtra(key, value)
                }
                if (value is ArrayList<*>) {
                    intent.putExtra(key, value as ArrayList<Parcelable>)
                }
            }
        }

        return intent
    }
}
