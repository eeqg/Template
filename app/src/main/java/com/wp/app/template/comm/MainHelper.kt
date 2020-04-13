package com.wp.app.template.comm

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.wp.app.resource.utils.LaunchUtil
import com.wp.app.template.di.model.LoginBean
import com.wp.app.template.ui.login.LoginActivity
import java.util.*

/**
 * Created by wp on 2019/3/22.
 */
class MainHelper private constructor() {

    companion object {
        val instance: MainHelper by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            //singleton
            MainHelper()
        }
    }

    val fragmentList: List<Fragment>
        get() {
            val fragments = ArrayList<Fragment>()
//            fragments.add(HomeFragment())
//            fragments.add(JobsFragment())
//            fragments.add(MineFragment())
            return fragments
        }

    @JvmOverloads
    fun backHome(activity: Activity, pageIndex: Int = 0) {
//        val params = HashMap<String, Any>()
//        params[BaseConst.INTENT_TYPE] = pageIndex
//        LaunchUtil.launchActivity(activity, MainActivity::class.java, params)
    }

    fun isLogin(): Boolean {
        return LoginBean.read() != null
    }

    fun needLogin(context: Context): Boolean {
        if (!isLogin()) {
            LaunchUtil.launchActivity(context, LoginActivity::class.java)
            return true
        }
        return false
    }

    fun requestLogin(context: Context) {
        if (!isLogin()) {
            LaunchUtil.launchActivity(context, LoginActivity::class.java)
        }
    }

    fun onLogin(loginBean: LoginBean) {
        loginBean.save()
//        EventBusManager.post(Const.EVENT_KEY_MSG_LOGIN)
    }

    fun onLogout(context: Context) {
        LoginBean.remove()
//        EventBusManager.post(Const.EVENT_KEY_MSG_LOGOUT)
    }

    /**
     * 检查是否拥有指定的所有权限
     *
     * @return
     */
    fun checkPermission(context: Context, permissions: Array<String>): ArrayList<String> {
        val permissionsList = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //没有权限
                permissionsList.add(permission)
            }
        }
        return permissionsList
    }

    fun formatH5Url(url: String): String {
        if (TextUtils.isEmpty(url)) {
            return url
        }
        var fullUrl = AppCache.baseH5Url + url
        val loginBean = LoginBean.read() ?: return fullUrl
        if (!fullUrl.contains("?")) {
            fullUrl = "$fullUrl?"
        } else {
            fullUrl = "$fullUrl&"
        }
        return fullUrl + String.format("userId=%s", loginBean.userId)
    }
}
