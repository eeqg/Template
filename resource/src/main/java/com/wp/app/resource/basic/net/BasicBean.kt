package com.wp.app.resource.basic.net

/**
 * Created by wp on 2018/6/25.
 */

open class BasicBean {
    var statusInfo: StatusInfo? = StatusInfo()

    var resultData: String? = null

    companion object {

        fun isNull(basicBean: BasicBean?): Boolean {
            if (basicBean == null) {
                return true
            }
            if (basicBean.resultData == null) {
                return true
            }
            return "null" == basicBean.resultData
        }

        fun getNullMessage(basicBean: BasicBean?): String {
            if (basicBean == null) {
                return ""
            } else {
                val statusInfo = basicBean.statusInfo
                return if (statusInfo == null) {
                    ""
                } else {
                    statusInfo.statusMessage + ""
                }
            }
        }
    }
}
