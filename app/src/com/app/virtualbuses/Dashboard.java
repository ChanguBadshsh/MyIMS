package com.app.virtualbuses;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.app.component.MasterActivity;
import com.smart.framework.Constants;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartUtils;
import com.smart.webservice.SmartWebManager;
import com.smartprime.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by tasol on 26/4/17.
 */

public class Dashboard extends MasterActivity {
    String email;
    String IN_EMAIL,userEmail;

    @Override
    public void initComponents() {
        super.initComponents();
        getIntentData();

        Log.v("@@@WWE"," Email:"+IN_EMAIL);
        JSONObject jsonObject=null;
        String email="";
        try {
            if(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                    .getString(SP_LOGGED_IN_USER_DATA,"")!=null&&SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                    .getString(SP_LOGGED_IN_USER_DATA,"").length()>0){
                jsonObject= new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                        .getString(SP_LOGGED_IN_USER_DATA,""));
            }
            if(jsonObject!=null){
                email=jsonObject.getString("email");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jsonObject!=null){
            userEmail=email;
        }else {
            userEmail=IN_EMAIL;
        }
        if(isNetworkConnected()){
            getUserDetails(userEmail);
        }else{

        }
    }

    @Override
    public int getLayoutID() {
        return R.layout.virtual_buses_dashboard;
    }
    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        toolbar.setTitle("Dashboard");
    }

    public void getIntentData(){
        IN_EMAIL=getIntent().getStringExtra("IN_EMAIL");
    }

    public void getUserDetails(final String email){
        SmartUtils.showLoadingDialog(Dashboard.this);
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, Dashboard.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_REMOVE_WALLORACTIVITIES);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("task","getUserDetail");
            JSONObject taskData = new JSONObject();
            try {
                taskData.put("email", email);
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
                        Log.v("@@@WWe"," Date");

                        String dateStr=jObj.getString("dateofregister");
                        long dateLong=Long.parseLong(dateStr);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(dateLong);

                        int mYear = calendar.get(Calendar.YEAR);
                        int mMonth = calendar.get(Calendar.MONTH);
                        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                        Log.v("@@@WWe"," Date");
                        Log.v("@@@WWe"," Year "+mYear+" Month : "+mMonth+" Day "+mDay);
                        Log.v("@@@WWe"," ADjusted Date Year "+mYear+" Month : "+(mMonth+1)+" Day "+mDay);

                    } catch (JSONException e) {
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
        SmartWebManager.getInstance(Dashboard.this).addToRequestQueueMultipart(requestParams, null, "", false);
    }
}
