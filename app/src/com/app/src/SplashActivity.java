package com.app.src;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.app.ims.AppUser;
import com.app.ims.DatabaseHandler;
import com.app.ims.IMSDash;
import com.app.ims.IMSLogin;
import com.app.ims.biller.IMSBillerDash;
import com.app.ims.developer.IMSDeveloperDash;
import com.app.model.TableClass;
import com.smart.caching.SmartCaching;
import com.smart.framework.Constants;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartUtils;
import com.smart.webservice.SmartWebManager;
import com.smartprime.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This Class Contains All Method Related To SplashActivity.
 *
 * @author tasol
 */
public class SplashActivity extends CoreMaster {

    private SmartCaching smartCaching;

    private boolean entryToApp = false;
    private String logOutStatus ="0";
    public String userEmail;
    public String firstTimeAppEntry="1";
    DatabaseHandler databaseHandler;
    List<AppUser> userList=new ArrayList<>();
    String userData;
    public String appUserName="";
    List<String>tableList=new ArrayList<>();

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
        tableList.add("syncmaster");tableList.add("appuser");tableList.add("custpurchase");tableList.add("stocks");tableList.add("custdetails");


        databaseHandler=new DatabaseHandler(SplashActivity.this);
        tableEntryUser();
        logOutStatus =SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_ISLOGIN,"");
        userData=SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(MYAPPUSERDATA,"");
        if(SmartUtils.isNetworkAvailable()){
            autoSyncFromWeb();
        }else {
            entryToApp=true;
        }
        if(entryToApp){
            proceedAfterPermission();
        }
        try{
            JSONObject jsonObject=new JSONObject(userData.toString());
            appUserName=jsonObject.getString("userName");
            Log.v("@@@WWE"," User Data "+jsonObject.getString("userName"));
        }catch (Exception je){
            je.printStackTrace();
        }

    }


    @Override
    public void setActionListeners() {
        super.setActionListeners();
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        actionBar.hide();
    }


    private void proceedAfterPermission() {

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(logOutStatus==null||logOutStatus.length()==0||logOutStatus.equals("0")){
//            go to login activity
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(FIRTSTAPPENTRY,"1");
                    Intent intent= new Intent(SplashActivity.this, IMSLogin.class);
                    startActivity(intent);
                }else{
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(FIRTSTAPPENTRY,"0");
                    if(appUserName.equals("developer")){
                        Intent intent= new Intent(SplashActivity.this, IMSDeveloperDash.class);
                        startActivity(intent);
                    }else if(appUserName.equals("biller")){
                        Intent intent= new Intent(SplashActivity.this, IMSBillerDash.class);
                        startActivity(intent);
                    } else {

                        Intent intent= new Intent(SplashActivity.this, IMSDash.class);
                        startActivity(intent);
                    }
                    // go to profile

                }
                firstTimeAppEntry=SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(FIRTSTAPPENTRY,"1");
                if(firstTimeAppEntry.equals("1")){
                    createAllTables();
                }
            }
        },5000);

    }


    private void autoSyncFromWeb() {

        entryToApp=true;
    }
    public void tableEntryUser(){
        databaseHandler.appUserData();
        if(databaseHandler.countUsercData()>0){
            userList=databaseHandler.getappUsercData();
        }else {
            databaseHandler.populateappUserData(new AppUser("1","admin","admin","12:12:12"));
            databaseHandler.populateappUserData(new AppUser("2","biller","biller","12:12:12"));
            databaseHandler.populateappUserData(new AppUser("3","developer","developer","12:12:12"));
        }
    }

    private void createAllTables() {
        for (int i = 0; i < tableList.size(); i++) {
            getTableStructure(tableList.get(i));
            Log.v("@@@WWE","Table "+tableList.get(i)+" Created Sucessfully");
        }
    }

    public void getTableStructure(final String tableName) {
        SmartUtils.showLoadingDialog(SplashActivity.this);
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, SplashActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_REMOVE_WALLORACTIVITIES);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();


        try {
            jsonObject.put("task", "getTableStructure");
            JSONObject taskData = new JSONObject();
            try {
                taskData.put("tablename", tableName);
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
                        JSONArray jArry = response.getJSONArray("tableData");
                        HashMap<String, List<TableClass>> hashMap = new HashMap<String, List<TableClass>>();
                        hashMap = databaseHandler.getTableFormatImproved(tableName, jArry);
                        databaseHandler.createTableFromListImproved(hashMap);
//
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
        SmartWebManager.getInstance(SplashActivity.this).addToRequestQueueMultipart(requestParams, null, "", false);
    }
}