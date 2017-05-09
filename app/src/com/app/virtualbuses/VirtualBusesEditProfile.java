package com.app.virtualbuses;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.androidquery.AQuery;
import com.app.component.MasterActivity;
import com.smart.customviews.CustomClickListener;
import com.smart.customviews.SmartButton;
import com.smart.customviews.SmartEditText;
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

public class VirtualBusesEditProfile extends MasterActivity {
    SmartEditText tvUserName,tvUserEmail,tvUserMobile,tvUserBirthDate;
    Spinner spnGender;
    List<String> genderList= new ArrayList<>();
    String IN_EMAIL;
    ArrayAdapter<String> adapter;
    String name="",password="",email="",mobile="",image="",dobirth="",gender="",registerDate="";
    String userName,userImage;
    AQuery aQuery;


    public void getIntentData(){
        IN_EMAIL=getIntent().getStringExtra("IN_EMAIL");
    }

    @Override
    public void initComponents() {
        super.initComponents();
        tvUserName=(SmartEditText)findViewById(R.id.tvUserName);
        tvUserEmail=(SmartEditText)findViewById(R.id.tvUserEmail);
        tvUserMobile=(SmartEditText)findViewById(R.id.tvUserMobile);
        tvUserBirthDate=(SmartEditText)findViewById(R.id.tvUserBirthDate);
        spnGender=(Spinner)findViewById(R.id.spnGender);

        genderList.add("Male");
        genderList.add("FeMale");
        adapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,genderList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aQuery= new AQuery(VirtualBusesEditProfile.this);

        getIntentData();
        spnGender.setAdapter(adapter);


        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                    .getString(SP_LOGGED_IN_USER_DATA, null));
            userName=jsonObject.getString("name");
            userImage=jsonObject.getString("image");
            mobile=jsonObject.getString("mobile");
            email=jsonObject.getString("email");
            dobirth=jsonObject.getString("birth_date");
            dobirth=SmartUtils.getDateFromTimeStamp(Long.parseLong(dobirth));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tvUserName.setText(userName);
        tvUserEmail.setText(email);
        tvUserMobile.setText(mobile);
        tvUserBirthDate.setText(dobirth);
//        aQuery.id(imgProfile).image(userImage,
//                true, true, getDeviceWidth(), 0);

    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();
        spnGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender=adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tvUserBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmartUtils.getDateTimeDialog(VirtualBusesEditProfile.this,((SmartEditText)view).getText().toString()
                        ,new CustomClickListener(){

                            @Override
                            public void onClick(String value) {
                                SmartUtils.getDateFromString(value, getString(R.string.date_format_vbs));
                                tvUserBirthDate.setText(value);
                            }
                        }, getString(R.string.date_format_vbs));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_save){
            boolean isValid=true;
            if(TextUtils.isEmpty(tvUserName.getText().toString())){
                isValid=false;
                tvUserName.setError(getString(R.string.empty_validation));

            }else if(TextUtils.isEmpty(tvUserEmail.getText().toString())){
                isValid=false;
                tvUserEmail.setError(getString(R.string.empty_validation));
            }else if(TextUtils.isEmpty(tvUserMobile.getText().toString())){
                isValid=false;
                tvUserMobile.setError(getString(R.string.empty_validation));
            }else if(TextUtils.isEmpty(tvUserBirthDate.getText().toString())){
                isValid=false;
                tvUserBirthDate.setError(getString(R.string.empty_validation));
            }

            name=tvUserName.getText().toString();
            email=tvUserEmail.getText().toString();
            mobile=tvUserMobile.getText().toString();
            dobirth=tvUserBirthDate.getText().toString();
            dobirth=String.valueOf(SmartUtils.getTimeStampInMillisecond(dobirth));


            if(isNetworkConnected()){
                if(isValid){
                    updateUser(name,password,email,image,mobile,dobirth,gender);
                    Log.v("@@@WWe"," Values"+name+password+email+image+mobile+dobirth+registerDate+gender);
                }else {
                }
            }else {
                SmartUtils.ting(VirtualBusesEditProfile.this,"No Internet connection");
            }
        }
        return true;
    }

    @Override
    public int getLayoutID() {
        return R.layout.virtual_buses_edit_profile;
    }
    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        toolbar.setTitle("Edit Profile");
    }
    public void updateUser(final String name, final String password, final String email, final String image, final String mobile, final String birthdate, final String gender){
        SmartUtils.showLoadingDialog(VirtualBusesEditProfile.this);
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, VirtualBusesEditProfile.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_REMOVE_WALLORACTIVITIES);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("task","updateUser");
            JSONObject taskData = new JSONObject();
            try {
                taskData.put("name", name);
                taskData.put("email", email);
                taskData.put("image", image);
                taskData.put("mobile", mobile);
                taskData.put("birthdate", birthdate);
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
                        JSONArray userData=response.getJSONArray("userData");
                        JSONObject jObj=userData.getJSONObject(0);
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_IN_USER_DATA, jObj.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent profileIntent= new Intent(VirtualBusesEditProfile.this,VirtualBusesProfile.class);
                    startActivity(profileIntent);
                }
                SmartUtils.hideLoadingDialog();
            }

            @Override
            public void onResponseError() {

                SmartUtils.hideLoadingDialog();
            }
        });
        SmartWebManager.getInstance(VirtualBusesEditProfile.this).addToRequestQueueMultipart(requestParams, null, "", false);
    }

}
