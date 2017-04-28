package com.app.src;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.virtualbuses.Dashboard;
import com.app.virtualbuses.VirtualBusesLogin;
import com.smart.framework.SmartApplication;
import com.smartprime.R;
import com.smart.caching.SmartCaching;
import com.smart.framework.AlertMagnatic;
import com.smart.framework.SmartUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This Class Contains All Method Related To SplashActivity.
 *
 * @author tasol
 */
public class SplashActivity extends CoreMaster {

    private SmartCaching smartCaching;

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;
    private boolean logOutStatus =false;
    public String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutID() {
        return R.layout.splash;
    }

    @Override
    public void initComponents() {
        SmartUtils.exportDatabse(this, "Application");
        smartCaching = new SmartCaching(this);
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        logOutStatus =SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getBoolean(SP_ISLOGOUT,true);
        try {
            JSONObject jsonObject= new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                    .getString(SP_LOGGED_IN_USER_DATA,""));
            userEmail=jsonObject.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        proceedAfterPermission();
    }

    @Override
    public void prepareViews() {
    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        actionBar.hide();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(SplashActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    private void proceedAfterPermission() {

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                Intent intent= new Intent(SplashActivity.this, VirtualBusesLogin.class);
//                startActivity(intent);
                if(logOutStatus){
//            go to login activity
                    Intent intent= new Intent(SplashActivity.this, VirtualBusesLogin.class);
                    startActivity(intent);
                }else{
                    Intent intent= new Intent(SplashActivity.this, Dashboard.class);
                    intent.putExtra("IN_EMAIL",userEmail);
                    startActivity(intent);
                    // go to profile
                }
            }
        },5000);

    }
}