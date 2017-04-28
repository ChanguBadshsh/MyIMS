package com.app.src;

import android.view.View;
import android.view.Window;

import com.smart.framework.SmartSuperMaster;

/**
 * Created by tasol on 23/6/15.
 */
public abstract class CoreMaster extends SmartSuperMaster {

    @Override
    public View getFooterLayoutView() {
        return null;
    }

    @Override
    public int getFooterLayoutID() {
        return 0;
    }

    @Override
    public View getHeaderLayoutView() {
        return null;
    }

    @Override
    public int getHeaderLayoutID() {
        return 0;
    }

    @Override
    public void preOnCreate() {

    }

    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public void setAnimations() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setAllowReturnTransitionOverlap(true);
    }

    @Override
    public void initComponents() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void prepareViews() {

    }

    @Override
    public void setActionListeners() {

    }

    @Override
    public void postOnCreate() {

    }

    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return true;
    }

    @Override
    public int getDrawerLayoutID() {
        return 0;
    }
}
