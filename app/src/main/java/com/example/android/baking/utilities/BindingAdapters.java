package com.example.android.baking.utilities;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.example.android.baking.R;
import com.squareup.picasso.Picasso;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

@SuppressWarnings("WeakerAccess")
public class BindingAdapters {

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Drawable imageDefault = ContextCompat.getDrawable(view.getContext(), R.drawable.cupcake);
        loadImage(view, imageUrl, imageDefault);
    }

    @BindingAdapter({"imageUrl", "imageDefault"})
    public static void loadImage(ImageView view, String imageUrl, Drawable imageDefault) {
        if (imageDefault == null) {
            imageDefault = ContextCompat.getDrawable(view.getContext(), R.drawable.cupcake);
        }

        if (!TextUtils.isEmpty(imageUrl)) {

            Picasso.with(view.getContext()).setLoggingEnabled(true);

            Picasso.with(view.getContext())
                    .load(imageUrl)
                    .placeholder(imageDefault)
                    .error(R.drawable.cupcake_fail)
                    .into(view);
        } else {
            view.setImageDrawable(imageDefault);
        }
    }

}