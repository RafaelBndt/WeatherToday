package com.showcase.weathertoday.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

public class GlideCustomizado {
    private static RequestManager sharedGlide;
    public static final String CENTER_INSIDE = "centerInside";
    public static final String CENTER_CROP = "centerCrop";
    public static final String FIT_CENTER = "fitCenter";
    public static final String CIRCLE_CROP = "circleCrop";


    public static synchronized RequestManager getSharedGlideInstance(Context context) {
        if (sharedGlide == null) {
            sharedGlide = GlideApp.with(context.getApplicationContext());
            try {
                Glide.get(context).clearDiskCache();
                Glide.get(context).clearMemory();
                GlideApp.get(context).clearDiskCache();
                GlideApp.get(context).clearMemory();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return sharedGlide;
    }

    public static void loadDrawableImage(Context context, String url, ImageView imageView, int placeholder) {

        if (url == null || url.isEmpty()) {
            return;
        }

        RequestOptions options = new RequestOptions()
                .placeholder(placeholder)
                .encodeQuality(100)
                .error(android.R.color.transparent)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop();

        getSharedGlideInstance(context)
                .load(url)
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }
}
