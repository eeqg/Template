package com.wp.app.resource.common

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore

import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment

import com.wp.app.resource.common.imageloader.MatisseGlideEngine
import com.wp.app.resource.utils.LogUtils
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy

import java.io.File
import java.io.IOException

/**
 * Created by wp on 2019/4/10.
 */
object PicturePicker {

    fun pickSingle(activity: Activity, requestCode: Int) {
        pickMulti(activity, requestCode, 1)
    }

    fun pickCrop(activity: Activity, requestCode: Int, xRatio: Float, yRatio: Float) {

    }

    fun pickMulti(activity: Activity, requestCode: Int, count: Int) {
        Matisse.from(activity)
            .choose(MimeType.allOf())
            .countable(true)
            .capture(true) //是否提供拍照功能
            .captureStrategy(
                CaptureStrategy(
                    true,
                    "${activity.applicationContext.packageName}.fileprovider"
                )
            )
            .maxSelectable(count)
            // .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
            // .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.85f)
            .imageEngine(MatisseGlideEngine())
            .forResult(requestCode)
    }

    fun pickMulti(fragment: Fragment, requestCode: Int, count: Int) {
        Matisse.from(fragment)
            .choose(MimeType.allOf())
            .countable(true)
            .capture(true) //是否提供拍照功能
            .captureStrategy(
                CaptureStrategy(
                    true,
                    "${fragment.context?.applicationContext?.packageName}.fileprovider"
                )
            )
            .maxSelectable(count)
            // .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
            // .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.85f)
            .imageEngine(MatisseGlideEngine())
            .forResult(requestCode)
    }

    fun pickCamera(activity: Activity, requestCode: Int): Uri {
        val uri: Uri
        val outputImage =
            File(
                activity.externalCacheDir,
                System.currentTimeMillis().toString() + ".jpg"
            )
        LogUtils.d("-----$outputImage")
        try {
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(
                activity,
                "${activity.applicationContext.packageName}.fileprovider",
                outputImage
            )
        } else {
            uri = Uri.fromFile(outputImage)
        }
        //启动相机程序
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        activity.startActivityForResult(intent, requestCode)

        return uri
    }
}
