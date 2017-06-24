package com.app.ims;

import android.content.ContentValues;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.app.component.MasterActivity;
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
 * Created by tasol on 26/4/17.
 */

public class IMSDash extends MasterActivity {
    SmartCaching smartCaching;
    Button btnGet,btnUpdate;
    DatabaseHandler db;
    List<String> columnNameList;
    String userData;

    @Override
    public void initComponents() {
        super.initComponents();
        smartCaching=new SmartCaching(IMSDash.this);
        btnGet=(Button)findViewById(R.id.btnGet);
        btnUpdate=(Button)findViewById(R.id.btnUpdate);
        db=new DatabaseHandler(IMSDash.this);
        columnNameList=new ArrayList<>();
        columnNameList=db.getTableStructure("custdetails");
//        getUserDetails("stocks");
        userData=SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(MYAPPUSERDATA,"");
        if(userData!=null&&userData.length()>0){
            try{
                JSONObject jsonObject=new JSONObject(userData.toString());
                Log.v("@@@WWE"," User Data "+jsonObject.getString("userName"));
            }catch (Exception je){
                je.printStackTrace();
            }
        }
    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                  getUserDetails();
//                String query="update userDetails set custage='100',custaddress='ahemdabad' where custid='1'";
//                smartCaching.updateTable(query);
//                Log.v("@@@WWe"," Table updated ");
            }
        });
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 db.getUsersFinal();
                 getTableStructure("custdetails");

//                ArrayList<ContentValues>table=smartCaching.getDataFromCache("userDetails","select * from userDetails");
//                for (int i = 0; i < table.size(); i++) {
//                    Log.v("@@@WWe"," Table Data");
//                    ContentValues row=table.get(i);
//                    Log.v("@@@WWe","  Name  "+row.getAsString("custname")+" Age  "+row.getAsString("custage"));
//                }
//                ArrayList<ContentValues>table=smartCaching.getDataFromCache(TABLE_IJACTIVITIES,"select * from "+TABLE_IJACTIVITIES);
//                for (int i = 0; i < table.size(); i++) {
//                    Log.v("@@@WWe"," Table Data");
//                    ContentValues row=table.get(i);
//                    Log.v("@@@WWe"," Column Name  "+row.getAsString("COLUMN_NAME")+"Column Type  "+row.getAsString("COLUMN_TYPE"));
//                }
            }
        });
    }

    @Override
    public int getLayoutID() {
        return R.layout.virtual_buses_dashboard;
    }
    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        toolbar.setTitle("IMSDash");
    }


    public void getUserDetails(){
        SmartUtils.showLoadingDialog(IMSDash.this);
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, IMSDash.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_REMOVE_WALLORACTIVITIES);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("task","getUSers");
            JSONObject taskData = new JSONObject();
            try {
                taskData.put("", "");
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
                        HashMap<String,List<String>> rowList=new HashMap<String, List<String>>();
                        JSONArray  userData=response.getJSONArray("userData");
                        rowList=db.populateTable("custdetails",userData);
//                        db.populateTable(rowList);
                        Log.v("@@@WWE"," RowList"+rowList);
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
        SmartWebManager.getInstance(IMSDash.this).addToRequestQueueMultipart(requestParams, null, "", false);
    }

    public void getTableStructure(final String tableName){
        SmartUtils.showLoadingDialog(IMSDash.this);
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, IMSDash.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_REMOVE_WALLORACTIVITIES);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();



        try {
            jsonObject.put("task","getTableStructure");
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
                        JSONArray jArry=response.getJSONArray("tableData");
                        HashMap<String,List<TableClass>> hashMap=new HashMap<String, List<TableClass>>();
                        hashMap = db.getTableFormat(tableName,jArry);
                        db.createTableFromList(hashMap);
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
        SmartWebManager.getInstance(IMSDash.this).addToRequestQueueMultipart(requestParams, null, "", false);
    }

}
