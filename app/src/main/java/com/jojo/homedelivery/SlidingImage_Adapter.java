package com.jojo.homedelivery;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class SlidingImage_Adapter extends PagerAdapter {
    private ArrayList<Integer> IMAGES;
    private ArrayList<String> NAMES;
    private LayoutInflater inflater;
    private Context context;

    public SlidingImage_Adapter(Context context,ArrayList<Integer> IMAGES, ArrayList<String> NAMES) {
        this.context = context;
        this.IMAGES = IMAGES;
        this.NAMES = NAMES;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

        assert imageLayout != null;
        ImageView imageView = imageLayout.findViewById(R.id.image);
//        TextView textView = imageView.findViewById(R.id.productTitle);
        imageView.setImageBitmap(decodeSampledBitmapFromResource(context.getResources(),IMAGES.get(position), 200, 200));
        view.addView(imageLayout, 0);
        //textView.setText(String.valueOf(NAMES.get(position)));
        //NAMES.get(position));
        return imageLayout;
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) { }

    @Override
    public Parcelable saveState() {
        return null;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

}