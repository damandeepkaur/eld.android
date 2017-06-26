package com.bsmwireless.data.storage;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

public class FontCache {
    public static final String SANS_SERIF = "sans_serif";
    public static final String BEBAS_NEUE = "fonts/BebasNeue.otf";
    public static final String FONTY = "fonts/Fonty.ttf";

    private Context mContext;
    private Hashtable<String, Typeface> mFontCache = new Hashtable<String, Typeface>();

    public FontCache(Context context) {
        mContext = context;
    }

    public Typeface get(String name) {
        Typeface typeface = mFontCache.get(name);

        if (SANS_SERIF.equals(name)) {
            //load default font, it is cached automatically by system
            typeface = Typeface.create(android.graphics.Typeface.SANS_SERIF, Typeface.NORMAL);
        }

        if(typeface == null) {
            try {
                //load custom font
                typeface = Typeface.createFromAsset(mContext.getAssets(), name);
            } catch (Exception e) {
                return null;
            }
            mFontCache.put(name, typeface);
        }

        return typeface;
    }
}
