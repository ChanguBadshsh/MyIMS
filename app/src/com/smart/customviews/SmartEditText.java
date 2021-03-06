package com.smart.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.smart.framework.SmartApplication;

/**
 * This Class Contains All Method Related To IjoomerEditText.
 *
 * @author tasol
 */
public class SmartEditText extends AppCompatEditText {

    public SmartEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public SmartEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SmartEditText(Context context) {
        super(context);
        init(context);
    }

    private void init(Context mContext) {
        try {
            if (getTypeface().getStyle() == Typeface.BOLD) {
                if (SmartApplication.REF_SMART_APPLICATION.BOLDFONT != null) {

                    setTypeface(SmartApplication.REF_SMART_APPLICATION.BOLDFONT);
                } else {

                    Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), SmartApplication.REF_SMART_APPLICATION.BOLDFONT_NAME);
                    SmartApplication.REF_SMART_APPLICATION.BOLDFONT = typeface;
                    setTypeface(SmartApplication.REF_SMART_APPLICATION.BOLDFONT);
                }
            } else {
                if (SmartApplication.REF_SMART_APPLICATION.FONT != null) {

                    setTypeface(SmartApplication.REF_SMART_APPLICATION.FONT);
                } else {

                    Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), SmartApplication.REF_SMART_APPLICATION.FONT_NAME);
                    SmartApplication.REF_SMART_APPLICATION.FONT = typeface;
                    setTypeface(SmartApplication.REF_SMART_APPLICATION.FONT);
                }
            }
        } catch (Throwable e) {
        }
    }
}
