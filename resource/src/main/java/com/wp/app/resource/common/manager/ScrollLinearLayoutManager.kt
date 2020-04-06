package com.wp.app.resource.common.manager

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * Created by wp on 2020/3/16.
 */
class ScrollLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    private var canScrollVertical: Boolean = true

    override fun canScrollVertically(): Boolean {
        return super.canScrollVertically() && canScrollVertical
    }

    fun setCanScrollVertical(b: Boolean) {
        canScrollVertical = b
    }
}