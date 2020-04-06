package com.wp.app.resource.common;

import android.text.TextUtils;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.wp.app.resource.common.imageloader.GlideImageLoader;

/**
 * Created by wp on 2019/3/26.
 */
public class CommonViewBinding {

    @BindingAdapter(value = {"imageUrl"})
    public static void loadImage(ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url) && !url.startsWith("http")) {
            url = "https:" + url;
        }
        GlideImageLoader.getInstance().load(imageView, url);
    }

    @BindingAdapter(value = {"imageRes"})
    public static void loadImage(ImageView imageView, int resId) {
        GlideImageLoader.getInstance().load(imageView, resId);
    }

    @BindingAdapter(value = {"imageUrl", "circle"})
    public static void loadImage(ImageView imageView, String url, boolean circle) {
        if (!TextUtils.isEmpty(url) && !url.startsWith("http")) {
            url = "https:" + url;
        }
        GlideImageLoader.getInstance().load(imageView, url, new CustomGlideTransform(circle, 0, 0, 0));
    }

    @BindingAdapter(value = {"imageUrl", "radius", "stroke", "strokeColor"}, requireAll = true)
    public static void loadImage(ImageView imageView, String url, int radius, float stroke, int strokeColor) {
        if (!TextUtils.isEmpty(url) && !url.startsWith("http")) {
            url = "https:" + url;
        }
        GlideImageLoader.getInstance().load(imageView, url, new CustomGlideTransform(false, radius, stroke, strokeColor));
    }
}
