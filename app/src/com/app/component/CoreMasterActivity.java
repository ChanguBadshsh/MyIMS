package com.app.component;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.smart.framework.SmartSuperMaster;

/**
 * Created by tasol on 5/10/15.
 */
public abstract class CoreMasterActivity extends SmartSuperMaster {

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
    public void setAnimations() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setAllowReturnTransitionOverlap(true);
    }

    @Override
    public void preOnCreate() {
    }

    @Override
    public void postOnCreate() {
    }

    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
    }

    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return true;
    }

    /**
     * This method used to hide soft keyboard.
     */
    public void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method used to get simple text from voice data.
     *
     * @param strData represented voice data
     * @return represented {@link String}
     */
    public String getPlainText(String strData) {
        if (strData.contains("{voice}")) {
            strData = strData.substring(0, strData.indexOf("{voice}"));
        }
        return strData;
    }
}
