package com.kegy.mobilemedia.utils.common;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.TextView;

public class Icon {
    private static Typeface normal;

    public static void applyTypeface(TextView v) {
        if (v.getTypeface() == null) {
            v.setTypeface(getNormal(v.getContext()));
            return;
        }
        switch (v.getTypeface().getStyle()) {
            case Typeface.BOLD:
                v.setTypeface(getNormal(v.getContext()));
                v.getPaint().setFakeBoldText(true);
                break;
            default:
                v.setTypeface(getNormal(v.getContext()));
                break;
            case Typeface.ITALIC:
                v.setTypeface(getNormal(v.getContext()));
                v.getPaint().setTextSkewX(-0.25f);
                break;
            case Typeface.BOLD_ITALIC:
                v.setTypeface(getNormal(v.getContext()));
                v.getPaint().setFakeBoldText(true);
                v.getPaint().setTextSkewX(-0.25f);
                break;
        }

    }

    public static void applyTypeface(Context context, Paint v) {
        if (v.getTypeface() == null) {
            v.setTypeface(getNormal(context));
            return;
        }
        switch (v.getTypeface().getStyle()) {
            case Typeface.BOLD:
                v.setTypeface(getNormal(context));
                v.setFakeBoldText(true);
                break;
            default:
                v.setTypeface(getNormal(context));
                break;
            case Typeface.ITALIC:
                v.setTypeface(getNormal(context));
                v.setTextSkewX(-0.25f);
                break;
            case Typeface.BOLD_ITALIC:
                v.setTypeface(getNormal(context));
                v.setFakeBoldText(true);
                v.setTextSkewX(-0.25f);
                break;
        }
    }

    public synchronized static Typeface getNormal(Context context) {
        if (normal == null)
            normal = loadFont(context.getAssets(), "icons.ttf");
        return normal;
    }

    private static Typeface loadFont(AssetManager am, String path) {
        try {
            Typeface tf = Typeface.createFromAsset(am, path);
            return tf;
        } catch (Exception e) {
            e.printStackTrace();
            return Typeface.DEFAULT;
        }
    }
}
