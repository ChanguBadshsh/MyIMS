package com.smart.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.smartprime.R;

public class RatioViewPager extends ViewPager {
 
    private float mRatio = 1f;
 
    public RatioViewPager(Context context) {
        super(context);
    }
 
    public RatioViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }
 
    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray styled = getContext().obtainStyledAttributes(attrs, R.styleable.RatioViewPager);
            mRatio = styled.getFloat(R.styleable.RatioViewPager_ratio, 1f);
            styled.recycle();
        }
    }
 
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
         int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * mRatio);
        setMeasuredDimension(width, height);
        measureChildren(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));

    }
}