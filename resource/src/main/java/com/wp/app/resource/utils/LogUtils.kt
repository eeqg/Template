package com.wp.app.resource.utils

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

/**
 * 日志工具类
 */
object LogUtils {
    var isShowToast = true
    private val mIsFormat = true
    private val TAG = "BaseProject"

    init {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
            .methodCount(1)         // (Optional) How many method line to show. Default 2
            .methodOffset(1)        // (Optional) Hides internal method calls up to offset. Default 5
            // .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
            .tag(TAG)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()

        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
    }

    @JvmStatic
    fun d(tag: String, msg: String) {
        if (mIsFormat) {
            Logger.t(tag).d(msg)
        } else {
            Log.d(tag, msg)
        }

    }

    @JvmStatic
    fun d(msg: Any) {
        if (mIsFormat) {
            Logger.d(msg)
        } else {
            Log.d(TAG, msg.toString() + "")
        }

    }

    @JvmStatic
    fun i(msg: String) {
        i(TAG, msg)
    }

    @JvmStatic
    fun i(tag: String, msg: String) {
        if (mIsFormat) {
            Logger.t(tag).i(msg)
        } else {
            Log.i(tag, msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (mIsFormat) {
            Logger.t(tag).w(msg)
        } else {
            Log.w(tag, msg)
        }
    }

    @JvmStatic
    fun e(tag: String, msg: String) {
        if (TextUtils.isEmpty(msg))
            return

        if (mIsFormat) {
            Logger.t(tag).e(msg)
        } else {
            Log.e(tag, msg)
        }
    }

    @JvmStatic
    fun e(msg: String, throwable: Throwable) {
        if (mIsFormat) {
            Logger.e(msg)
        } else {
            Log.e(TAG, msg)
        }
    }

    @JvmStatic
    fun json(json: String) {
        if (mIsFormat) {
            Logger.json(json)
        } else {
            Log.d(TAG, json)
        }
    }

    /**
     * 带toast的error日志输出
     *
     * @param act act
     * @param msg 日志
     */
    fun errorWithToast(act: Activity, msg: String) {
        if (mIsFormat) {
            Logger.e(msg)
        } else {
            Log.e(TAG, msg)
        }
        showToast(act, msg)
    }

    /**
     * 带toast的error日志输出
     *
     * @param act act
     * @param msg 日志
     */
    fun errorWithToast(act: Activity, msg: String, throwable: Throwable) {
        if (mIsFormat) {
            Logger.e(throwable, msg)
        } else {
            Log.e(TAG, msg)
        }
        showToast(act, msg)
    }

    /**
     * 带toast的debug日志输出
     *
     * @param act act
     * @param msg 日志
     */
    fun debugWithToast(act: Activity, msg: String) {
        if (mIsFormat) {
            Logger.d(msg)
        } else {
            Log.d(TAG, msg)
        }
        showToast(act, msg)
    }

    /**
     * 带toast的debug日志输出
     */
    fun onClick() {
        if (mIsFormat) {
            Logger.d("*** onClick ***")
        } else {
            Log.d(TAG, "*** onClick ***")
        }
    }

    /**
     * 带toast的debug日志输出
     *
     * @param msg 日志
     */
    fun onClick(msg: String) {
        if (mIsFormat) {
            Logger.d("*** onClick ***$msg")
        } else {
            Log.d(TAG, "*** onClick ***$msg")
        }
    }

    /**
     * 带toast的debug日志输出
     *
     * @param act act
     * @param msg 日志
     */
    fun onClickWithToast(act: Activity, msg: String) {
        if (mIsFormat) {
            Logger.d("*** onClick ***$msg")
        } else {
            Log.d(TAG, "*** onClick ***$msg")
        }
        showToast(act, msg)
    }

    /**
     * toast，带判断isShowToast和isDebugMode
     *
     * @param msg 内容
     */
    private fun showToast(context: Context, msg: String) {
        if (isShowToast) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    fun test(msg: String) {
        if (mIsFormat) {
            Logger.d("test ==> $msg")
        } else {
            Log.d(TAG, "test ==> $msg")
        }
    }

    fun testWithOutFormat(msg: String) {
        Log.i("test", msg)
    }
}
