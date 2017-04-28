package com.app.virtualbuses;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.smart.customviews.SmartButton;
import com.smart.customviews.SmartEditText;
import com.smart.framework.Constants;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartUtils;
import com.smart.webservice.SmartWebManager;
import com.smartprime.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.HashMap;

import static com.smart.framework.Constants.SP_ISLOGOUT;
import static com.smart.framework.Constants.SP_LOGGED_IN_USER_DATA;


/**
 * Created by tasol on 11/4/17.
 */

public class VirtualBusesRegister extends Activity {
    SmartEditText etEmail,etPassword,etFirstName,etLastName,etMobile;
    SmartButton btnRegister,btnCancel;
    boolean isValid=false;
    String name,password,email,mobile,image;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.virtual_buses_register);
        etEmail=(SmartEditText)findViewById(R.id.etMobile);
        etPassword=(SmartEditText)findViewById(R.id.etPassword);
        etFirstName=(SmartEditText)findViewById(R.id.etFirstName);
        etLastName=(SmartEditText)findViewById(R.id.etLastName);
        etMobile=(SmartEditText)findViewById(R.id.etMobile);
        btnRegister=(SmartButton) findViewById(R.id.btnRegister);
        btnCancel=(SmartButton) findViewById(R.id.btnCancel);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkConnected()){
//                    registerNewUser();
                }else {
                    SmartUtils.ting(VirtualBusesRegister.this,"No Internet connection");
                }

                if(!TextUtils.isEmpty(etFirstName.getText().toString())){
                    isValid=true;
                    name=name+etFirstName.getText().toString()+" ";
                }else {
                    isValid=false;
                    etFirstName.setError(getString(R.string.empty_validation));
                }
                if(!TextUtils.isEmpty(etLastName.getText().toString())){
                    isValid=true;
                    name=name+etLastName.getText().toString();
                }else {
                    isValid=false;
                    etLastName.setError(getString(R.string.empty_validation));
                }
                if(!TextUtils.isEmpty(etPassword.getText().toString())){
                    isValid=true;
                    password=password+etPassword.getText().toString();
                }else {
                    isValid=false;
                    etPassword.setError(getString(R.string.empty_validation));
                }
                if(!TextUtils.isEmpty(etEmail.getText().toString())){
                    isValid=true;
                    email=email+etEmail.getText().toString();
                }else {
                    isValid=false;
                    etEmail.setError(getString(R.string.empty_validation));
                }
                if(!TextUtils.isEmpty(etMobile.getText().toString())){
                    isValid=true;
                    mobile=mobile+etMobile.getText().toString();
                }else {
                    isValid=false;
                    etMobile.setError(getString(R.string.empty_validation));
                }
            }
        });
    }

    public void registerNewUser(final String name, final String password, final String email, final String image){
        SmartUtils.showLoadingDialog(VirtualBusesRegister.this);
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, VirtualBusesRegister.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_REMOVE_WALLORACTIVITIES);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("task","registerNewUser");
            JSONObject taskData = new JSONObject();
            try {
                taskData.put("name", name);
                taskData.put("passwsord", password);
                taskData.put("email", email);
                taskData.put("image", image);
            } catch (Throwable e) {
            }
            jsonObject.put("taskData", taskData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, jsonObject);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                if (responseCode == 200) {

                    try {
                        ContentValues userData=new ContentValues();
                        userData.put("name",name);
                        userData.put("password",password);
                        userData.put("email",email);
                        userData.put("image",image);

                        JSONObject iObj= new JSONObject();
                        iObj.put("name",name);
                        iObj.put("password",password);
                        iObj.put("email",email);
                        iObj.put("image",image);
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_IN_USER_DATA, iObj.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, false);
                    Intent loginIntent= new Intent(VirtualBusesRegister.this,Dashboard.class);
                    loginIntent.putExtra("IN_EMAIL",email);
                    startActivity(loginIntent);
                }
                SmartUtils.hideLoadingDialog();
            }

            @Override
            public void onResponseError() {

                SmartUtils.hideLoadingDialog();
            }
        });
        SmartWebManager.getInstance(VirtualBusesRegister.this).addToRequestQueueMultipart(requestParams, null, "", false);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }

}
