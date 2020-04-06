package com.wp.app.resource.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.wp.app.resource.R
import com.wp.app.resource.basic.net.StatusInfo
import com.wp.app.resource.utils.NetworkUtils

import cn.shyman.library.refresh.RefreshLayout
import cn.shyman.library.refresh.RefreshStatus


class BasicRefreshStatus(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs),
    RefreshStatus<StatusInfo> {
    private var ivStatus: ImageView? = null
    private var tvStatus: TextView? = null
    private var tvCheck: TextView? = null
    private var tvReload: TextView? = null

    private var animation: RotateAnimation? = null

    private var oldOnRefreshListener: RefreshLayout.OnRefreshListener? = null

    override fun initOnRefreshListener(onTryRefreshListener: RefreshLayout.OnRefreshListener) {
        this.oldOnRefreshListener = onTryRefreshListener
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        this.ivStatus = findViewById<View>(R.id.ivStatus) as ImageView
        this.ivStatus!!.setOnClickListener {
            if (oldOnRefreshListener != null) {
                oldOnRefreshListener!!.notifyRefresh()
            }
        }
        this.tvStatus = findViewById<View>(R.id.tvStatus) as TextView
        this.tvCheck = findViewById<View>(R.id.tvCheck) as TextView
        this.tvReload = findViewById<View>(R.id.tvReload) as TextView
        this.tvReload!!.setOnClickListener {
            if (oldOnRefreshListener != null) {
                oldOnRefreshListener!!.notifyRefresh()
            }
        }

        // Animation scaleAnimation = new ScaleAnimation(
        // 		1.0F, 0.55F, 1.0F, 0.55F,
        // 		Animation.RELATIVE_TO_SELF, 0.5F,
        // 		Animation.RELATIVE_TO_SELF, 0.5F);
        // scaleAnimation.setDuration(800);
        // scaleAnimation.setRepeatMode(Animation.REVERSE);
        // scaleAnimation.setRepeatCount(Animation.INFINITE);
        //
        // Animation rotateAnimation = new RotateAnimation(
        // 		0, 360,
        // 		Animation.RELATIVE_TO_SELF, 0.5F,
        // 		Animation.RELATIVE_TO_SELF, 0.5F);
        // rotateAnimation.setDuration(600);
        // scaleAnimation.setRepeatMode(Animation.REVERSE);
        // rotateAnimation.setRepeatCount(Animation.INFINITE);
        //
        // this.animation = new AnimationSet(true);
        // this.animation.setInterpolator(new LinearInterpolator());
        // this.animation.addAnimation(scaleAnimation);
        // this.animation.addAnimation(rotateAnimation);

        this.animation = RotateAnimation(
            0f, 3600f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        this.animation!!.duration = 10000
        this.animation!!.interpolator = LinearInterpolator()
        this.animation!!.repeatCount = Animation.INFINITE
    }

    override fun onRefreshScale(scale: Float) {}

    override fun onRefreshReady() {
        this.ivStatus!!.clearAnimation()
        this.ivStatus!!.setImageDrawable(null)
        this.ivStatus!!.startAnimation(this.animation)
        this.tvStatus!!.setText(R.string.loading_dot)
        this.tvCheck!!.visibility = View.GONE
        this.tvReload!!.visibility = View.GONE
    }

    override fun onRefresh() {
        this.ivStatus!!.setImageResource(R.mipmap.img_refresh_status)
        this.ivStatus!!.startAnimation(this.animation)
        this.tvStatus!!.setText(R.string.loading_dot)
        this.tvCheck!!.visibility = View.GONE
        this.tvReload!!.visibility = View.GONE
    }

    override fun onRefreshComplete(statusInfo: StatusInfo?): Boolean {
        this.ivStatus!!.clearAnimation()
        if (statusInfo == null) {
            this.ivStatus!!.setImageResource(R.mipmap.img_network_error)
            this.ivStatus!!.isEnabled = false
            this.tvStatus!!.setText(R.string.network_request_error)
            if (!NetworkUtils.isConnected(context)) {
                this.tvCheck!!.visibility = View.VISIBLE
            }
            this.tvReload!!.visibility = View.VISIBLE
            return true
        } else if (statusInfo.isSuccessful) {
            this.ivStatus!!.setImageDrawable(null)
            this.ivStatus!!.isEnabled = true
            this.tvStatus!!.text = statusInfo.statusMessage
            this.tvCheck!!.visibility = View.GONE
            this.tvReload!!.visibility = View.GONE
        } else {
            this.ivStatus!!.setImageResource(R.mipmap.img_data_error)
            this.ivStatus!!.isEnabled = true
            this.tvStatus!!.text = statusInfo.statusMessage
            this.tvCheck!!.visibility = View.GONE
            this.tvReload!!.visibility = View.GONE
        }
        return !statusInfo.isSuccessful
    }
}
