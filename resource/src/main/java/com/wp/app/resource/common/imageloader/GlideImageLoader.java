package com.wp.app.resource.common.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wp.app.resource.R;
import com.wp.picture.widget.CircleImageView;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class GlideImageLoader implements ImageLoader {
    private static GlideImageLoader INSTANCE;

    private GlideImageLoader() {
    }

    public static GlideImageLoader getInstance() {
        if (INSTANCE == null) {
            synchronized (GlideImageLoader.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GlideImageLoader();
                }
            }
        }
        return INSTANCE;
    }

    public Glide getGlide(Context context) {
        return Glide.get(context);
    }

    @Override
    public void load(@NonNull ImageView imageView, Object imageRes) {
        load(imageView, imageRes, R.mipmap.ic_placeholder);
    }

    @Override
    public void load(@NonNull ImageView imageView, Object imageRes, @DrawableRes int defaultImage) {
        if (defaultImage == 0) {
            defaultImage = R.mipmap.ic_placeholder;
        }
        RequestOptions options = new RequestOptions()
                // .centerCrop()
                .placeholder(defaultImage)
                .error(defaultImage);
        loadReal(imageView, imageRes, options);
    }

    public void load(int resId, ImageView imageView, String imageUrl) {
        if (resId == 0) {
            resId = R.mipmap.ic_placeholder;
        }
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(resId)
                .error(resId);
        loadReal(imageView, imageUrl, options);
    }


    public void loadBlur(ImageView imageView, String imageUrl) {
        loadBlur(imageView, imageUrl, 25, 1);
    }

    @Override
    public void loadBlur(@NonNull ImageView imageView, String imageUrl, int radius, int sampling) {
        RequestOptions requestOptions = bitmapTransform(new BlurTransformation(radius, sampling))
                .placeholder(R.mipmap.ic_placeholder)
                .error(R.mipmap.ic_placeholder);
        loadReal(imageView, imageUrl, requestOptions);
    }

    @Override
    public void loadCircle(@NonNull ImageView imageView, String imageUrl) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.mipmap.ic_placeholder)
                .error(R.mipmap.ic_placeholder)
                .transform(new CircleCrop());
        loadReal(imageView, imageUrl, requestOptions);
    }

    @Override
    public void loadRound(@NonNull ImageView imageView, String imageUrl, int radius) {
        RequestOptions options = bitmapTransform(new RoundedCorners(radius))
                .placeholder(R.mipmap.ic_placeholder)
                .error(R.mipmap.ic_placeholder);
        loadReal(imageView, imageUrl, options);
    }

    public void loadTopRounded(ImageView imageView, String imageUrl, int radius) {
        loadRounded(imageView, imageUrl, radius, 0, RoundedCornersTransformation.CornerType.TOP);
    }

    public void loadBottomRounded(ImageView imageView, String imageUrl, int radius) {
        loadRounded(imageView, imageUrl, radius, 0, RoundedCornersTransformation.CornerType.BOTTOM);
    }

    public void loadRounded(ImageView imageView, String imageUrl, int radius, int margin,
                            RoundedCornersTransformation.CornerType cornerType) {
        RequestOptions options = bitmapTransform(new RoundedCornersTransformation(radius, margin, cornerType))
                .placeholder(R.mipmap.ic_placeholder)
                .error(R.mipmap.ic_placeholder);
        loadReal(imageView, imageUrl, options);
    }

    @Override
    public void load(@NonNull ImageView imageView, String imageUrl, Transformation<Bitmap> transformation) {
        RequestOptions options = bitmapTransform(transformation)
                .placeholder(R.mipmap.ic_placeholder)
                .error(R.mipmap.ic_placeholder);
        loadReal(imageView, imageUrl, options);
    }

    private void loadReal(@NonNull ImageView imageView, Object imageUrl, RequestOptions options) {
        if (imageView instanceof CircleImageView){
            Glide.with(imageView.getContext())
                    .asBitmap()
                    .load(imageUrl)
                    .apply(options)
                    .into(imageView);
        }else {
            Glide.with(imageView.getContext())
                    .load(imageUrl)
                    .apply(options)
                    .into(imageView);
        }
    }

    public void loadWrapHeight(@NonNull final ImageView imageView, final String imageUrl) {
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        float ratio = 1F * bitmap.getHeight() / bitmap.getWidth();
                        // LogUtils.d("-----ratio = " + ratio);
                        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                        layoutParams.height = (int) (layoutParams.width * ratio);
                        imageView.setLayoutParams(layoutParams);
                        imageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    public void loadBitmap(Context context, String url, RequestOptions options, final LoadCallback callback) {
        Glide.with(context).asBitmap().load(url).apply(options)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (callback != null) {
                            callback.onLoadSuccess(resource);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        if (callback != null) {
                            callback.onLoadFailure();
                        }
                    }
                });
    }

    public void loadBitmap(Context context, String url, final LoadCallback callback) {
        Glide.with(context).asBitmap().load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (callback != null) {
                            callback.onLoadSuccess(resource);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        if (callback != null) {
                            callback.onLoadFailure();
                        }
                    }
                });
    }

    public void loadBlurBitmap(@NonNull Context context, String imageUrl, int radius, int sampling, final LoadCallback callback) {
        RequestOptions requestOptions = bitmapTransform(new BlurTransformation(radius, sampling));
        loadBitmap(context, imageUrl, requestOptions, callback);
    }

    public void loadBlurBitmap(@NonNull Context context, int imageRes, int radius, int sampling, final LoadCallback callback) {
        RequestOptions requestOptions = bitmapTransform(new BlurTransformation(radius, sampling));
        Glide.with(context).asBitmap().load(imageRes).apply(requestOptions)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (callback != null) {
                            callback.onLoadSuccess(resource);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        if (callback != null) {
                            callback.onLoadFailure();
                        }
                    }
                });
    }

    public void loadImage(Context context, String uri, int width, int height, final LoadCallback callback) {
        Glide.with(context).asBitmap().load(uri).into(new SimpleTarget<Bitmap>(width, height) {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                if (callback != null) {
                    callback.onLoadSuccess(resource);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                if (callback != null) {
                    callback.onLoadFailure();
                }
            }
        });
    }

    public interface LoadCallback {
        void onLoadSuccess(Bitmap bitmap);

        void onLoadFailure();
    }
}
