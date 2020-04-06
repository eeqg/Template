package com.wp.app.resource.basic.net

import com.google.gson.annotations.SerializedName

open class ArrayBean : BasicBean() {
    @SerializedName(value = "totalCount", alternate = ["count"])
    var totalCount: Int = 0
}
