package com.wp.app.template.comm

import android.content.Context

/**
 * Created by wp on 2019/5/28.
 */
object AppCache {
    var appContext: Context? = null
    var baseUrl = Const.BASE_URL
    var baseH5Url = Const.BASE_URL

    var pushAlias: String? = null
    var pushTag: String? = null
}
