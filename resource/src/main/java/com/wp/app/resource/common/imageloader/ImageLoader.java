package com.wp.app.resource.common.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Transformation;


/**
 * 框架全局图片加载接口
 *
 * @author 王兴春
 * @email 1028729086@qq.com
 * @time 2017/1/10 10:16
 */
public interface ImageLoader {
	
	/**
	 * 图片加载方法
	 * <p>
	 * （默认图片在实现类中实现，此方法主要是全局调用，默认图片统一，避免每次都要传入默认图片）
	 *
	 * @param imageView
	 * @param imageRes  类型为Object原因：因为你的图片链接可以是string、uri、file等多中类型
	 */
	void load(@NonNull ImageView imageView, Object imageRes);
	
	/**
	 * 图片加载方法
	 * <p>
	 * （默认图片可以自己每次单独设置，主要满足软件一些地方可能默认图片不一样的情况）
	 *
	 * @param imageView
	 * @param imageRes
	 * @param defaultImage
	 */
	void load(@NonNull ImageView imageView, Object imageRes, int defaultImage);
	
	/**
	 * 模糊
	 *
	 * @param imageView
	 * @param imageUrl
	 * @param radius
	 */
	void loadBlur(@NonNull ImageView imageView, String imageUrl, int radius, int sampling);
	
	/**
	 * 圆形图片
	 */
	void loadCircle(@NonNull ImageView imageView, String imageUrl);
	
	/**
	 * 圆角图片
	 */
	void loadRound(@NonNull ImageView imageView, String imageUrl, int radius);
	
	/**
	 * 加载不同形状图片
	 *
	 * @param imageView
	 * @param imageUrl
	 * @param transformation 传入你要加载的图片形状实现类
	 */
	void load(@NonNull ImageView imageView, String imageUrl, Transformation<Bitmap> transformation);
}
