package com.wp.app.resource.basic

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.wp.app.resource.R
import com.wp.app.resource.common.ext.getScreenHeight

/**
 * Created by wp on 2019/5/8.
 */
abstract class BasicDialogFragment<B : ViewDataBinding> : DialogFragment(), BasicViewImp {

    protected lateinit var dataBinding: B
    protected lateinit var mContext: Context
    protected lateinit var mActivity: Activity
    private var fullScreen: Boolean = false
    private var bottomAnimation: Boolean = false
    private var transparent: Boolean = false
    private var canceledTouchOutside = true

    private var onDismissListener: OnDismissListener? = null
    //	private LoadingDialog loadingDialog;

    fun setFullScreen() {
        this.fullScreen = true
    }

    fun setCanceledTouchOutside(canceledTouchOutside: Boolean) {
        this.canceledTouchOutside = canceledTouchOutside
    }

    protected fun setBottomAnimation() {
        this.bottomAnimation = true
    }

    protected fun setTransparent() {
        this.transparent = true
    }

    protected fun showLoading() {
        //		if (!loadingDialog.isShowing()) {
        //			loadingDialog.show();
        //		}
    }

    protected fun hideLoading() {
        //		if (loadingDialog.isShowing()) {
        //			loadingDialog.dismiss();
        //		}
    }

    protected fun promptMessage(resId: Int) {
        promptMessage(getString(resId))
    }

    protected fun promptMessage(msg: String) {
        BasicApp.toast(msg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFullScreen)
        mContext = context!!
        mActivity = activity!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, contentView, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        loadingDialog = LoadingDialog(context)
        onInit()
        initView()
        subscribe()

        dataBinding.root.setOnClickListener { dismiss() }
    }

    protected open fun subscribe() {}

    override fun onStart() {
        super.onStart()
        val window = dialog.window
        if (window != null) {
            //transparent
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //width & height
            val dialogWidth = (resources.displayMetrics.widthPixels * 0.8).toInt()
            val dialogHeight = (resources.displayMetrics.heightPixels * 0.8).toInt()
            window.setLayout(
                if (fullScreen) resources.displayMetrics.widthPixels else dialogWidth,
                if (fullScreen) mContext.getScreenHeight()
                else WindowManager.LayoutParams.WRAP_CONTENT
            )

            if (fullScreen) {
//                val layoutParams = window.attributes
//                layoutParams.gravity = Gravity.BOTTOM
//                window.attributes = layoutParams
                window.setGravity(Gravity.BOTTOM)
            }

            //animation
            if (bottomAnimation) {
                window.setWindowAnimations(R.style.AnimationBottom)
                // window.getAttributes().windowAnimations = R.style.AnimationBottom;
            }
            if (transparent) {
                window.setDimAmount(0.0f)
            }

            //点击边际可消失
            dialog.setCanceledOnTouchOutside(canceledTouchOutside)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoading()
    }


    override fun dismiss() {
        if (onDismissListener != null) {
            onDismissListener!!.onDismiss()
        }
        super.dismiss()
    }

    fun setOnDismissListener(onDismissListener: OnDismissListener) {
        this.onDismissListener = onDismissListener
    }

    interface OnDismissListener {
        fun onDismiss()
    }

    private fun getNavigationBarSize(): Int {
        val appUsableSize = getAppUsableScreenSize();
        val realScreenSize = getRealScreenSize();

//        LogUtils.d("-----${appUsableSize.x}--${appUsableSize.y}")
//        LogUtils.d("-----${realScreenSize.x}--${realScreenSize.y}")
        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return Point(realScreenSize.x - appUsableSize.x, appUsableSize.y).y
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return Point(appUsableSize.x, realScreenSize.y - appUsableSize.y).y
        }

        // navigation bar is not present
        return Point().y
    }

    private fun getAppUsableScreenSize(): Point {
        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size);
        return size;
    }

    private fun getRealScreenSize(): Point {
        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
//            try {
//                size.x = Display.class.getMethod("getRawWidth").invoke(display);
//                size.y = (Integer) Display .class.getMethod("getRawHeight").invoke(display);
//            } catch (IllegalAccessException e) {
//            } catch (InvocationTargetException e) {
//            } catch (NoSuchMethodException e) {
//            }
        }

        return size;
    }

//    override fun show(manager: FragmentManager, tag: String) {
//        val ft = manager.beginTransaction()
//        ft.add(this, tag)
//        ft.commitAllowingStateLoss()
//    }
}
