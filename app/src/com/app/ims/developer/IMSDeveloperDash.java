package com.app.ims.developer;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.component.MasterActivity;
import com.app.ims.DatabaseHandler;
import com.app.ims.IMSDash;
import com.app.model.TableClass;
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
 * Created by tasol on 13/6/17.
 */

public class IMSDeveloperDash extends MasterActivity {
    DatabaseHandler databaseHandler;
    TextView tvNote;
    LinearLayout lnrCaution;
    @Override
    public void setActionListeners() {
        super.setActionListeners();
    }

    @Override
    public void initComponents() {
        super.initComponents();
        databaseHandler=new DatabaseHandler(IMSDeveloperDash.this);
        tvNote=(TextView)findViewById(R.id.tvNote);
        lnrCaution=(LinearLayout)findViewById(R.id.lnrCaution);

        if(SmartUtils.isNetworkAvailable()){
            tvNote.setText("Network is available");
            lnrCaution.setVisibility(View.GONE);
        }else {
            tvNote.setText("Network is Not available");
            lnrCaution.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getLayoutID() {
        return R.layout.ims_developer_dashboard;
    }
    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        toolbar.setTitle("Developer Console");
    }
}
