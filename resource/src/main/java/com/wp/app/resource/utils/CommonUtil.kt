package com.wp.app.resource.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.text.TextUtils
import android.widget.TextView
import com.wp.app.resource.basic.BasicApp
import java.io.File
import java.io.UnsupportedEncodingException

/**
 * Created by wp on 2019/11/25.
 */
object CommonUtil {

    fun openApp(context: Context, pkgName: String, clazzName: String) {
        val intent = Intent()
        val cmp = ComponentName(pkgName, clazzName)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.component = cmp
        context.startActivity(intent)
    }

    fun openSettingsApp(context: Context) {
        val localIntent: Intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
        localIntent.data = Uri.fromParts("package", context.packageName, null)
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(localIntent)
    }

    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     *
     * @param phoneNum 电话号码
     */
    fun callPhone(context: Context, phoneNum: String) {
        val intent = Intent(Intent.ACTION_DIAL);
        val data = Uri.parse("tel:$phoneNum");
        intent.data = data;
        context.startActivity(intent)
    }

    /**
     * textView 添加中划线
     */
    fun middleLineAction(textView: TextView) {
        textView.paint.isAntiAlias = true//抗锯齿
        // textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        // textView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
        textView.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG// 设置中划线并加清晰
    }

    fun underLineAction(textView: TextView) {
        textView.paint.isAntiAlias = true//抗锯齿
        textView.paint.flags = Paint.UNDERLINE_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG// 设置中划线并加清晰
    }

    fun isAppInstall(packageName: String): Boolean {
        if (TextUtils.isEmpty(packageName)) {
            return false
        }
        var info: android.content.pm.ApplicationInfo? = null
        return try {
            info = BasicApp.INSTANCE.packageManager.getApplicationInfo(packageName, 0)
            info != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 更新系统图库
     */
    fun sendImageChangeBroadcast(imgPath: String) {
        if (TextUtils.isEmpty(imgPath)) return
        val file = File(imgPath)
        if (file.exists() && file.isFile) {
            MediaScannerConnection.scanFile(
                BasicApp.INSTANCE,
                arrayOf(file.absolutePath),
                null,
                null
            )
        }
    }

    /**
     * 获取手机相册路径
     */
    fun getCameraPath(): String {
        val cameraPath = Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Camera/"
        val file = File(cameraPath)
        if (!file.exists()) {
            file.mkdirs()
        }
        return cameraPath
    }

    /**
     * URLDecoder 解码
     */
    fun getURLDecoderString(str: String?): String {
        var result = ""
        if (null == str) {
            return ""
        }
        try {
            result = java.net.URLDecoder.decode(str, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return result
    }
}