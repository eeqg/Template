package com.wp.app.resource.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.text.TextUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.util.*


/**
 * Created by wp on 2020/3/9.
 */
object ZxingUtil {
    /**
     * 生成二维码图片
     */
    fun createQRCodeBitmap(content: String, widthPix: Int, heightPix: Int): Bitmap? {
        return createQRCodeBitmap(
            content,
            widthPix,
            heightPix,
            "UTF-8",
            "H",
            "1",
            Color.BLACK,
            Color.WHITE
        )
    }

    fun createQRCodeBitmap(
        content: String,
        widthPix: Int,
        heightPix: Int,
        colorBlack: Int,
        colorWhite: Int
    ): Bitmap? {
        return createQRCodeBitmap(
            content,
            widthPix,
            heightPix,
            "UTF-8",
            "H",
            "1",
            colorBlack,
            colorWhite
        )
    }

    fun createQRCodeBitmapWithLog(
        content: String, widthPix: Int, heightPix: Int,
        logoBitmap: Bitmap, logoPercent: Float = 0.2f
    ): Bitmap? {
        return createQRCodeBitmapWithLog(
            content,
            widthPix,
            heightPix,
            Color.BLACK,
            Color.WHITE,
            logoBitmap,
            logoPercent
        )
    }

    fun createQRCodeBitmapWithLog(
        content: String,
        widthPix: Int,
        heightPix: Int,
        colorBlack: Int,
        colorWhite: Int,
        logoBitmap: Bitmap,
        logoPercent: Float = 0.2f
    ): Bitmap? {
        return createQRCodeBitmap(
            content,
            widthPix,
            heightPix,
            "UTF-8",
            "H",
            "1",
            colorBlack,
            colorWhite,
            logoBitmap,
            logoPercent
        )
    }

    /**
     * 生成简单二维码
     *
     * @param content                字符串内容
     * @param width                  二维码宽度
     * @param height                 二维码高度
     * @param characterSet          编码方式（一般使用UTF-8）
     * @param errorCorrectionLevel 容错率 L：7% M：15% Q：25% H：35%
     * @param margin                 空白边距（二维码与边框的空白区域）
     * @param colorBlack            黑色色块
     * @param colorWhite            白色色块
     * @param logoBitmap             logo图片
     * @param logoPercent            logo所占百分比
     * @return BitMap
     */
    fun createQRCodeBitmap(
        content: String?, width: Int, height: Int,
        characterSet: String?, errorCorrectionLevel: String?,
        margin: String?, colorBlack: Int, colorWhite: Int,
        logoBitmap: Bitmap? = null, logoPercent: Float = 0.2f
    ): Bitmap? {
        // 字符串内容判空
        if (TextUtils.isEmpty(content)) {
            return null
        }
        return if (width < 0 || height < 0) {
            null
        } else try {
            /** 1.设置二维码相关配置  */
            val hints: Hashtable<EncodeHintType, String?> = Hashtable()
            // 字符转码格式设置
            if (!TextUtils.isEmpty(characterSet)) {
                hints.put(EncodeHintType.CHARACTER_SET, characterSet)
            }
            // 容错率设置
            if (!TextUtils.isEmpty(errorCorrectionLevel)) {
                hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel)
            }
            // 空白边距设置
            if (!TextUtils.isEmpty(margin)) {
                hints.put(EncodeHintType.MARGIN, margin)
            }
            /** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象  */
            val bitMatrix =
                QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)
            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值  */
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) { //bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
                    if (bitMatrix[x, y]) {
                        pixels[y * width + x] = colorBlack //黑色色块像素设置
                    } else {
                        pixels[y * width + x] = colorWhite // 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象  */
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)

            if (logoBitmap != null) addLogo(bitmap, logoBitmap, logoPercent) else bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 向二维码中间添加logo图片(图片合成)
     *
     * @param srcBitmap 原图片（生成的简单二维码图片）
     * @param logoBitmap logo图片
     * @param logoPercent 百分比 (用于调整logo图片在原图片中的显示大小, 取值范围[0,1] )
     * @return
     */
    private fun addLogo(
        srcBitmap: Bitmap?,
        logoBitmap: Bitmap?,
        logoPercent: Float
    ): Bitmap? {
        var logoPercent = logoPercent
        if (srcBitmap == null) {
            return null
        }
        if (logoBitmap == null) {
            return srcBitmap
        }
        //传值不合法时使用0.2F
        if (logoPercent < 0f || logoPercent > 1f) {
            logoPercent = 0.2f
        }
        /** 1. 获取原图片和Logo图片各自的宽、高值  */
        val srcWidth = srcBitmap.width
        val srcHeight = srcBitmap.height
        val logoWidth = logoBitmap.width
        val logoHeight = logoBitmap.height
        /** 2. 计算画布缩放的宽高比  */
        val scaleWidth = srcWidth * logoPercent / logoWidth
        val scaleHeight = srcHeight * logoPercent / logoHeight
        /** 3. 使用Canvas绘制,合成图片  */
        val bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(srcBitmap, 0f, 0f, null)
        canvas.scale(scaleWidth, scaleHeight, srcWidth / 2f, srcHeight / 2f)
        canvas.drawBitmap(
            logoBitmap,
            srcWidth / 2f - logoWidth / 2f,
            srcHeight / 2f - logoHeight / 2f,
            null
        )
        return bitmap
    }
}