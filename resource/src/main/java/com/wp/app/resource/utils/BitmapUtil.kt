package com.wp.app.resource.utils

//import com.google.zxing.BarcodeFormat
//import com.google.zxing.EncodeHintType
//import com.google.zxing.WriterException
//import com.google.zxing.common.BitMatrix
//import com.google.zxing.qrcode.QRCodeWriter
//import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.util.Base64
import android.util.LruCache
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.io.*
import java.lang.ref.SoftReference


/**
 * Created by kaiwang on 2017/12/1.
 */

object BitmapUtil {

    fun scaleBitmap(bitmap: Bitmap, newWidth: Double, newHeight: Double): Bitmap {
        // 获取这个图片的宽和高
        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()
        // 创建操作图片用的matrix对象
        val matrix = Matrix()
        // 计算宽高缩放率
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(
            bitmap, 0, 0, width.toInt(),
            height.toInt(), matrix, true
        )
    }

    /**
     * 保存base64图片到手机图库
     *
     * @param context
     * @param str
     */
    fun saveImageFromBase64(
        context: Context,
        path: String,
        str: String,
        showToast: Boolean
    ): String? {
        try {
            val bitmapArray = Base64.decode(str, Base64.DEFAULT)
            //Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            val bitmap = byteToBitmap(bitmapArray)
            if (bitmap != null) {
                val savePath =
                    saveImageFromBitmap(path, bitmap, Bitmap.CompressFormat.PNG, null)

                bitmap.recycle()

                CommonUtil.sendImageChangeBroadcast(savePath!!)

                if (showToast) {
                    // ToastUtil.showLongToast(context, R.string.toast_save_image);
                }

                return savePath
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }


    /**
     * 保存Drawable下的图片到本地
     *
     * @param context
     * @param drawableId
     */
    fun saveImageFromDrawable(path: String, context: Context, drawableId: Int): String? {
        val resources = context.resources
        val drawable = resources.getDrawable(drawableId) as BitmapDrawable
        val bitmap = drawable.bitmap

        return saveImageFromBitmap(path, bitmap, Bitmap.CompressFormat.JPEG, null)
    }

    /**
     * 保存bitmap到指定目录
     *
     * @param path
     * @param bitmap
     * @param format
     * @return
     */
    fun saveImageFromBitmap(
        path: String,
        bitmap: Bitmap?,
        format: Bitmap.CompressFormat,
        customName: String?
    ): String? {
        if (bitmap != null) {
            var suffix = ".jpg"
            if (format == Bitmap.CompressFormat.PNG) {
                suffix = ".png"
            }
            val imagePath =
                path + (if (TextUtils.isEmpty(customName)) System.nanoTime() else customName) + suffix
            try {
                val outputStream = FileOutputStream(imagePath)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
                //CommonUtil.sendImageChangeBroadcast(imagePath);
                return imagePath
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return null
    }

    /**
     * 保存bitmap到指定目录
     *
     * @param path
     * @param bitmap
     * @param format
     * @return
     */
    fun saveImageFromBitmap(
        path: String,
        bitmap: Bitmap?,
        format: Bitmap.CompressFormat,
        customName: String?,
        quality: Int
    ): String? {
        if (bitmap != null) {
            var suffix = ".jpg"
            if (format == Bitmap.CompressFormat.PNG) {
                suffix = ".png"
            }
            val imagePath =
                path + (if (TextUtils.isEmpty(customName)) System.nanoTime() else customName) + suffix
            try {
                val outputStream = FileOutputStream(imagePath)
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                outputStream.close()
                CommonUtil.sendImageChangeBroadcast(imagePath)
                return imagePath
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return null
    }

    /**
     * 回收ImageView占用的图像内存;
     *
     * @param view
     */
    fun recycleImageView(view: View?) {
        if (view == null) return
        if (view is ImageView) {
            val drawable = view.drawable
            if (drawable is BitmapDrawable) {
                var bmp: Bitmap? = drawable.bitmap
                if (bmp != null && !bmp.isRecycled) {
                    view.setImageBitmap(null)
                    bmp.recycle()
                    bmp = null
                }
            }
        }
    }

    fun getCutBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        var bitmap = bitmap
        // 计算缩放比例
        val scaleWidth = width.toFloat() / bitmap.width
        val scaleHeight = height.toFloat() / bitmap.height
        // 取得想要缩放的matrix参数
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        // 得到新的图片
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return bitmap
    }

    /**
     * Get bitmap from specified image path
     *
     * @param imgPath
     * @param sampleSize
     * @return
     */
    fun getBitmap(imgPath: String, sampleSize: Int?): Bitmap {
        // Get bitmap through image path
        val newOpts = BitmapFactory.Options()
        newOpts.inJustDecodeBounds = false
        newOpts.inPurgeable = true
        newOpts.inInputShareable = true
        newOpts.inSampleSize = sampleSize!!
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565
        return BitmapFactory.decodeFile(imgPath, newOpts)
    }

    /**
     * Store bitmap into specified image path
     *
     * @param bitmap
     * @param outPath
     * @throws FileNotFoundException
     */
    @Throws(FileNotFoundException::class)
    fun storeImage(bitmap: Bitmap, outPath: String) {
        val os = FileOutputStream(outPath)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
    }

    /**
     * Compress image by pixel, this will modify image width/height.
     * Used to get thumbnail
     *
     * @param imgPath image path
     * @param pixelW  target pixel of width
     * @param pixelH  target pixel of height
     * @return
     */
    fun ratio(imgPath: String, pixelW: Float, pixelH: Float): Bitmap {
        val newOpts = BitmapFactory.Options()
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565
        // Get bitmap info, but notice that bitmap is null now

        newOpts.inJustDecodeBounds = false
        val w = newOpts.outWidth
        val h = newOpts.outHeight
        // 想要缩放的目标尺寸
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        var be = 1//be=1表示不缩放
        if (pixelH == pixelW) {
            be = 2
        } else if (w > h && w > pixelW) {//如果宽度大的话根据宽度固定大小缩放
            be = (newOpts.outWidth / pixelW).toInt()
        } else if (w < h && h > pixelH) {//如果高度高的话根据宽度固定大小缩放
            be = (newOpts.outHeight / pixelH).toInt()
        }
        if (be <= 0) be = 1
        newOpts.inSampleSize = be//设置缩放比例
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
        val bitmap = BitmapFactory.decodeFile(imgPath, newOpts)
        // 压缩好比例大小后再进行质量压缩
        //        return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap
    }

    /**
     * Compress image by size, this will modify image width/height.
     * Used to get thumbnail
     *
     * @param image
     * @param pixelW target pixel of width
     * @param pixelH target pixel of height
     * @return
     */
    fun ratio(image: Bitmap, pixelW: Float, pixelH: Float): Bitmap? {
        val os = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, os)
        if (os.toByteArray().size / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset()//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, os)//这里压缩50%，把压缩后的数据存放到baos中
        }
        var `is` = ByteArrayInputStream(os.toByteArray())
        val newOpts = BitmapFactory.Options()
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565
        var bitmap = BitmapFactory.decodeStream(`is`, null, newOpts)
        newOpts.inJustDecodeBounds = false
        val w = newOpts.outWidth
        val h = newOpts.outHeight
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        var be = 1//be=1表示不缩放
        if (w > h && w > pixelW) {//如果宽度大的话根据宽度固定大小缩放
            be = (newOpts.outWidth / pixelW).toInt()
        } else if (w < h && h > pixelH) {//如果高度高的话根据宽度固定大小缩放
            be = (newOpts.outHeight / pixelH).toInt()
        }
        if (be <= 0) be = 1
        newOpts.inSampleSize = be//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        `is` = ByteArrayInputStream(os.toByteArray())
        bitmap = BitmapFactory.decodeStream(`is`, null, newOpts)
        //压缩好比例大小后再进行质量压缩
        //	    return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap
    }

    /**
     * Compress by quality,  and generate image to the path specified
     *
     * @param image
     * @param outPath
     * @param maxSize target will be compressed to be smaller than this size.(kb)
     * @throws IOException
     */
    @Throws(IOException::class)
    fun compressAndGenImage(image: Bitmap, maxSize: Int): String {
        val os = ByteArrayOutputStream()
        // scale
        var options = 100
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os)
        // Compress by loop
        while (os.toByteArray().size / 1024 > maxSize) {
            // Clean up os
            os.reset()
            // interval 10
            options -= 10
            image.compress(Bitmap.CompressFormat.JPEG, options, os)
        }

        // Generate compressed image file
        val file = File(FileUtils.getAppImageCachePath(), "${System.nanoTime()}.png")
        val fos = FileOutputStream(file)
        fos.write(os.toByteArray())
        fos.flush()
        fos.close()

        return file.absolutePath
    }

    /**
     * Compress by quality,  and generate image to the path specified
     *
     * @param imgPath
     * @param outPath
     * @param maxSize     target will be compressed to be smaller than this size.(kb)
     * @param needsDelete Whether delete original file after compress
     * @throws IOException
     */
    @Throws(IOException::class)
    fun compressAndGenImage(
        imgPath: String,
        maxSize: Int
    ): String {
        val bmp = BitmapFactory.decodeFile(imgPath)
        val output = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, output)
        if (output.toByteArray().size / 1024 < maxSize) {
            return imgPath
        }
        return compressAndGenImage(getBitmap(imgPath, 1), maxSize)
    }

    /**
     * Ratio and generate thumb to the path specified
     *
     * @param image
     * @param outPath
     * @param pixelW  target pixel of width
     * @param pixelH  target pixel of height
     * @throws FileNotFoundException
     */
    @Throws(FileNotFoundException::class)
    fun ratioAndGenThumb(image: Bitmap, outPath: String, pixelW: Float, pixelH: Float) {
        val bitmap = ratio(image, pixelW, pixelH)
        storeImage(bitmap!!, outPath)
    }

    /**
     * Ratio and generate thumb to the path specified
     *
     * @param imgPath
     * @param outPath
     * @param pixelW      target pixel of width
     * @param pixelH      target pixel of height
     * @param needsDelete Whether delete original file after compress
     * @throws FileNotFoundException
     */
    @Throws(FileNotFoundException::class)
    fun ratioAndGenThumb(
        imgPath: String,
        outPath: String,
        pixelW: Float,
        pixelH: Float,
        needsDelete: Boolean
    ) {
        val bitmap = ratio(imgPath, pixelW, pixelH)
        storeImage(bitmap, outPath)

        // Delete original file
        if (needsDelete) {
            val file = File(imgPath)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    /**
     * bitmap -> byte
     *
     * @param bmp
     * @param needRecycle
     * @return
     */
    fun bmpToByteArray(bmp: Bitmap, needRecycle: Boolean): ByteArray {
        val output = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, output)
        if (needRecycle) {
            bmp.recycle()
        }

        val result = output.toByteArray()
        try {
            output.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    /**
     * byte to bitmap
     *
     * @param imgByte
     * @return
     */
    fun byteToBitmap(imgByte: ByteArray?): Bitmap? {
        var imgByte = imgByte
        val options = BitmapFactory.Options()
        options.inSampleSize = 8
        val input = ByteArrayInputStream(imgByte)
        val softRef = SoftReference(
            BitmapFactory.decodeStream(
                input, null, options
            )
        )
        val bitmap = softRef.get() as Bitmap
        if (imgByte != null) {
            imgByte = null
        }
        try {
            input?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bitmap
    }

    fun compressBitmap(bm: Bitmap, scale: Float): Bitmap {
        var result = bm
        LogUtils.i(
            "douyao", "压缩前图片的大小" + result.byteCount / 1024f
                    + "KB\n宽度为" + result.width + "高度为" + result.height
        )
        val matrix = Matrix()
        matrix.setScale(scale, scale)
        result = Bitmap.createBitmap(
            result, 0, 0, result.width,
            result.height, matrix, true
        )
        LogUtils.i(
            "douyao", "压缩后图片的大小" + result.byteCount / 1024f
                    + "KB\n宽度为" + result.width + "高度为" + result.height
        )
        return result
    }

    fun scaleBitmap(bitmap: Bitmap, w: Float, h: Float): Bitmap? {
        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()
        var x = 0f
        var y = 0f
        var scaleWidth = width
        var scaleHeight = height
        val newbmp: Bitmap
        //Log.e("gacmy","width:"+width+" height:"+height);
        if (w > h) {//比例宽度大于高度的情况
            val scale = w / h
            val tempH = width / scale
            if (height > tempH) {//
                x = 0f
                y = (height - tempH) / 2
                scaleWidth = width
                scaleHeight = tempH
            } else {
                scaleWidth = height * scale
                x = (width - scaleWidth) / 2
                y = 0f
            }
        } else if (w < h) {//比例宽度小于高度的情况
            val scale = h / w
            val tempW = height / scale
            if (width > tempW) {
                y = 0f
                x = (width - tempW) / 2
                scaleWidth = tempW
                scaleHeight = height
            } else {
                scaleHeight = width * scale
                y = (height - scaleHeight) / 2
                x = 0f
                scaleWidth = width
            }
        } else {//比例宽高相等的情况
            if (width > height) {
                x = (width - height) / 2
                y = 0f
                scaleHeight = height
                scaleWidth = height
            } else {
                y = (height - width) / 2
                x = 0f
                scaleHeight = width
                scaleWidth = width
            }
        }
        try {
            newbmp = Bitmap.createBitmap(
                bitmap,
                x.toInt(),
                y.toInt(),
                scaleWidth.toInt(),
                scaleHeight.toInt(),
                null,
                false
            )// createBitmap()方法中定义的参数x+width要小于或等于bitmap.getWidth()，y+height要小于或等于bitmap.getHeight()
            //bitmap.recycle();
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        return newbmp
    }

    /**
     * View截图
     */
    fun getViewBitmap(view: View?): Bitmap? {
        if (null == view) {
            return null
        }
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()

// Bitmap bitmap = view.getDrawingCache();

        return Bitmap.createBitmap(
            view.drawingCache,
            0,
            0,
            view.measuredWidth,
            view.measuredHeight
        )
    }

    /**
     * view to bitmap
     *
     * @param view
     * @return
     */
    fun convertViewToBitmap(view: View): Bitmap {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.buildDrawingCache()
        return view.drawingCache

    }

    /**
     * View截图
     */
    fun createViewBitmap(v: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            v.width, v.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        v.draw(canvas)
        return bitmap
    }

    /**
     * recycleview截图
     *
     * @param view
     * @return
     */
    fun shotRecyclerView(view: RecyclerView): Bitmap? {
        val adapter = view.adapter
        var bigBitmap: Bitmap? = null
        if (adapter != null) {
            val size = adapter.itemCount
            var height = 0
            val paint = Paint()
            var iHeight = 0
            val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

            // Use 1/8th of the available memory for this memory cache.
            val cacheSize = maxMemory / 8
            val bitmaCache = LruCache<String, Bitmap>(cacheSize)
            for (i in 0 until size) {
                val holder = adapter.createViewHolder(view, adapter.getItemViewType(i))
                adapter.onBindViewHolder(holder, i)
                holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                holder.itemView.layout(
                    0, 0, holder.itemView.measuredWidth,
                    holder.itemView.measuredHeight
                )
                holder.itemView.isDrawingCacheEnabled = true
                holder.itemView.buildDrawingCache()
                val drawingCache = holder.itemView.drawingCache
                if (drawingCache != null) {
                    bitmaCache.put(i.toString(), drawingCache)
                }
                height += holder.itemView.measuredHeight
            }

            // bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_4444);
            bigBitmap = Bitmap.createBitmap(
                view.measuredWidth,
                view.measuredWidth,
                Bitmap.Config.ARGB_4444
            )

            val bigCanvas = Canvas(bigBitmap!!)
            val lBackground = view.background
            if (lBackground is ColorDrawable) {
                val lColor = lBackground.color
                bigCanvas.drawColor(lColor)
            }

            for (i in 0 until size) {
                val bitmap = bitmaCache.get(i.toString())
                bigCanvas.drawBitmap(bitmap, 0f, iHeight.toFloat(), paint)
                iHeight += bitmap.height
                bitmap.recycle()
            }
        }
        return bigBitmap
    }
}
