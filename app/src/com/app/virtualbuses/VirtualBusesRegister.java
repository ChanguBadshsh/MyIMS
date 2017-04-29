package com.app.virtualbuses;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

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
import com.smart.customviews.CustomClickListener;
import com.smart.customviews.SmartButton;
import com.smart.customviews.SmartEditText;
import com.smart.customviews.SmartSpinner;
import com.smart.framework.Constants;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartUtils;
import com.smart.webservice.SmartWebManager;
import com.smartprime.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.smart.framework.Constants.SP_ISLOGOUT;
import static com.smart.framework.Constants.SP_LOGGED_IN_USER_DATA;


/**
 * Created by tasol on 11/4/17.
 */

public class VirtualBusesRegister extends Activity {
    SmartEditText etEmail,etPassword,etName,etMobile,etDateOfBirth,etConfirmPassword;
    SmartButton btnRegister,btnCancel;
    Spinner spnGender;
    String name="",password="",email="",mobile="",image="",dobirth="",gender="",registerDate="";
    List<String> existingEmails=new ArrayList<>();
    List<String> genderList= new ArrayList<>();
    ArrayAdapter<String> adapter;
    Dialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.virtual_buses_register);
        getExistingEmails();
        etEmail=(SmartEditText)findViewById(R.id.etEmail);
        etPassword=(SmartEditText)findViewById(R.id.etPassword);
        etConfirmPassword=(SmartEditText)findViewById(R.id.etConfirmPassword);
        etName=(SmartEditText)findViewById(R.id.etName);
        etMobile=(SmartEditText)findViewById(R.id.etMobile);
        etDateOfBirth=(SmartEditText)findViewById(R.id.etDateOfBirth);
        spnGender=(Spinner) findViewById(R.id.spnGender);

        btnRegister=(SmartButton) findViewById(R.id.btnRegister);
        btnCancel=(SmartButton) findViewById(R.id.btnCancel);

        genderList.add("Male");
        genderList.add("FeMale");

        adapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,genderList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnGender.setAdapter(adapter);

        etDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmartUtils.getDateTimeDialog(VirtualBusesRegister.this,((SmartEditText)view).getText().toString()
                        ,new CustomClickListener(){

                            @Override
                            public void onClick(String value) {
                                SmartUtils.getDateFromString(value, getString(R.string.date_format_vbs));
                                etDateOfBirth.setText(value);
                            }
                        }, getString(R.string.date_format_vbs));
            }
        });


        spnGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender=adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValid=true;
                if(TextUtils.isEmpty(etName.getText().toString())){
                    isValid=false;
                    etName.setError(getString(R.string.empty_validation));

                }else if(TextUtils.isEmpty(etPassword.getText().toString())){
                    isValid=false;

                    etPassword.setError(getString(R.string.empty_validation));
                }else if(TextUtils.isEmpty(etConfirmPassword.getText().toString())){
                    isValid=false;
                    etConfirmPassword.setError(getString(R.string.empty_validation));
                }else if(TextUtils.isEmpty(etEmail.getText().toString())){
                    isValid=false;
                    etEmail.setError(getString(R.string.empty_validation));
                }else if(TextUtils.isEmpty(etMobile.getText().toString())){
                    isValid=false;
                    etMobile.setError(getString(R.string.empty_validation));
                }else if(TextUtils.isEmpty(etDateOfBirth.getText().toString())){
                    isValid=false;
                    etDateOfBirth.setError(getString(R.string.empty_validation));
                }else if(!TextUtils.isEmpty(etEmail.getText().toString())){
                    for (int i = 0; i <existingEmails.size() ; i++) {
                        if(etEmail.getText().toString().trim().equals(existingEmails.get(i))){
                            etEmail.setError(getString(R.string.email_exist));
                            isValid=false;
                        }
                    }
                }else if(!TextUtils.isEmpty(etConfirmPassword.getText().toString())){
                    if(!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())){
                        isValid=false;
                        etConfirmPassword.setError(getString(R.string.confirm_password));
                    }
                }

                String stringDtTime=SmartUtils.getCurrentDate();
                registerDate=String.valueOf(SmartUtils.getTimeStampInMillisecond(stringDtTime));

                name=etName.getText().toString();
                password=etPassword.getText().toString();
                email=etEmail.getText().toString();
                mobile=etMobile.getText().toString();
                dobirth=etDateOfBirth.getText().toString();
                dobirth=String.valueOf(SmartUtils.getTimeStampInMillisecond(dobirth));


                if(isNetworkConnected()){
                    if(isValid){
                        registerNewUser(name,password,email,image,mobile,dobirth,registerDate,gender);
                        Log.v("@@@WWe"," Values"+name+password+email+image+mobile+dobirth+registerDate+gender);
                    }else {
                    }
                }else {
                    SmartUtils.ting(VirtualBusesRegister.this,"No Internet connection");
                }


            }
        });
    }

    public void getExistingEmails(){
        SmartUtils.showLoadingDialog(VirtualBusesRegister.this);
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, VirtualBusesRegister.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_REMOVE_WALLORACTIVITIES);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("task","getEmails");
            JSONObject taskData = new JSONObject();
            try {
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
                        JSONArray userData=response.getJSONArray("userData");
                        for (int i = 0; i < userData.length(); i++) {
                            existingEmails.add(userData.getJSONObject(i).getString("email"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    public void registerNewUser(final String name, final String password, final String email, final String image, final String mobile, final String birthdate, final String rigisterdate, final String gender){
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
                taskData.put("mobile", mobile);
                taskData.put("birthdate", birthdate);
                taskData.put("rigisterdate", rigisterdate);
                taskData.put("gender", gender);
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
                        JSONObject iObj= new JSONObject();
                        iObj.put("name", name);
                        iObj.put("passwsord", password);
                        iObj.put("email", email);
                        iObj.put("image", image);
                        iObj.put("mobile", mobile);
                        iObj.put("birthdate", birthdate);
                        iObj.put("rigisterdate", rigisterdate);
                        iObj.put("gender", gender);
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


    private void genderDialog(final SmartEditText etDateOfBirth){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        etDateOfBirth.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

}
