package com.app.virtualbuses;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.app.component.MasterActivity;
import com.smart.customviews.RoundedImageView;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartUtils;
import com.smartprime.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tasol on 27/4/17.
 */

public class VirtualBusesProfile extends MasterActivity {
    RoundedImageView imgProfile;
    TextView tvUserName,tvUserEmail,tvUserMobile,tvUserBirthDate;
    String userName,userImage,mobile,email;
    AQuery aQuery;

    @Override
    public void initComponents() {
        super.initComponents();
        imgProfile=(RoundedImageView) findViewById(R.id.imgProfile);
        tvUserName=(TextView)findViewById(R.id.tvUserName);
        tvUserEmail=(TextView)findViewById(R.id.tvUserEmail);
        tvUserMobile=(TextView)findViewById(R.id.tvUserMobile);
        tvUserBirthDate=(TextView)findViewById(R.id.tvUserBirthDate);
        aQuery= new AQuery(VirtualBusesProfile.this);



        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                    .getString(SP_LOGGED_IN_USER_DATA, null));
            userName=jsonObject.getString("name");
            userImage=jsonObject.getString("image");
            mobile=jsonObject.getString("mobile");
            email=jsonObject.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tvUserName.setText(userName);
        tvUserEmail.setText(email);
        tvUserMobile.setText(mobile);
        aQuery.id(imgProfile).image(userImage,
                true, true, getDeviceWidth(), 0);
    }

    @Override
    public int getLayoutID() {
        return R.layout.virtual_buses_profile;
    }
    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        toolbar.setTitle("Profile");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_edit){
            SmartUtils.ting(VirtualBusesProfile.this,"Edit profile");
        }
        return true;
    }
}
