package com.wp.app.resource.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView


import cn.shyman.library.refresh.RefreshHeader
import com.wp.app.resource.R
import com.wp.app.resource.basic.net.StatusInfo

class BasicRefreshHeader(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs),
    RefreshHeader<StatusInfo> {
    private var ivStatus: ImageView? = null
    private var tvStatus: TextView? = null

    private var animator: ObjectAnimator? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        this.ivStatus = findViewById(R.id.ivStatus)
        this.tvStatus = findViewById(R.id.tvStatus)

        animator = ObjectAnimator.ofFloat(this.ivStatus, "rotation", 0f, 3600f)
        animator!!.interpolator = LinearInterpolator()
        animator!!.repeatCount = Animation.INFINITE
        animator!!.duration = 10000
    }

    override fun getRefreshOffsetPosition(): Int {
        return (measuredHeight * 0.8).toInt()
    }

    override fun onRefreshScale(scale: Float) {}

    override fun onPullToRefresh() {
        this.animator!!.end()
        this.ivStatus!!.rotationY = 0f
        this.tvStatus!!.setText(R.string.pull_to_refresh)
    }

    override fun onReleaseToRefresh() {
        this.animator!!.end()
        this.ivStatus!!.rotationY = 0f
        this.tvStatus!!.setText(R.string.release_to_refresh)
    }

    override fun onRefresh() {
        this.animator!!.start()
        this.tvStatus!!.setText(R.string.loading_dot)
    }

    override fun onRefreshComplete(statusInfo: StatusInfo?) {
        this.animator!!.end()
        this.ivStatus!!.rotationY = 0f
        this.tvStatus!!.setText(R.string.refresh_complete)
    }
}
