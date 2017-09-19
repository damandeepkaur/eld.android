package com.bsmwireless.widgets.common;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.bsmwireless.common.App;
import com.bsmwireless.data.storage.FontCache;

import javax.inject.Inject;

import app.bsmuniversal.com.R;

public class FontTextView extends AppCompatTextView {

    public enum FontType {
        NONE(0),
        SANS_SERIF(1),
        BEBAS_NEUE(2),
        FONTY(3);

        int mId;

        FontType(int id) {
            mId = id;
        }

        static FontType fromId(int id) {
            for (FontType type : values()) {
                if (type.mId == id) {
                    return type;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    @Inject
    FontCache mFontCache;

    public FontTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        App.getComponent().inject(this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FontTextView, defStyleAttr, 0);
        FontType type = FontType.fromId(typedArray.getInt(R.styleable.FontTextView_textFont, 0));

        setFont(type);

        typedArray.recycle();
    }

    public void setFont(FontType type) {
        Typeface typeface = null;
        switch (type) {
            case SANS_SERIF:
                typeface = mFontCache.get(FontCache.SANS_SERIF);
                break;

            case BEBAS_NEUE:
                typeface = mFontCache.get(FontCache.BEBAS_NEUE);
                break;

            case FONTY:
                typeface = mFontCache.get(FontCache.FONTY);
                break;
        }

        if (typeface != null) {
            setTypeface(typeface);
        }
    }
}
