package com.wp.app.template.ui.comm

import android.webkit.JavascriptInterface

import androidx.fragment.app.FragmentActivity

import com.wp.app.resource.utils.LogUtils

import java.lang.ref.WeakReference

/**
 * Created by wp on 2019/7/3.
 */
class AndroidInterface(activity: FragmentActivity) {
    private val mActivityReference: WeakReference<FragmentActivity> = WeakReference(activity)

    /**
     * 分享礼包gra
     */
    @JavascriptInterface
    fun shareGiftOrder(json: String) {
        LogUtils.d("-----json = $json")
        mActivityReference.get()
    }
}
