package com.app.virtualbuses;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
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
    ImageView imgProfile;
    TextView tvUserName,tvUserEmail,tvUserMobile,tvUserBirthDate,tvDateOfRegister,tvGender;
    String userName,userImage,mobile,email,dob,dateOfregister,gender;
    AQuery aQuery;
    private String imageProfile="";

    @Override
    public void initComponents() {
        super.initComponents();
        imgProfile=(ImageView) findViewById(R.id.imgProfile);
        tvUserName=(TextView)findViewById(R.id.tvUserName);
        tvUserEmail=(TextView)findViewById(R.id.tvUserEmail);
        tvUserMobile=(TextView)findViewById(R.id.tvUserMobile);
        tvUserBirthDate=(TextView)findViewById(R.id.tvUserBirthDate);
        tvGender=(TextView)findViewById(R.id.tvGender);
        tvDateOfRegister=(TextView)findViewById(R.id.tvDateOfRegister);
        aQuery= new AQuery(VirtualBusesProfile.this);

        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                    .getString(SP_LOGGED_IN_USER_DATA, null));
            userName=jsonObject.getString("name");
            userImage=jsonObject.getString("image");
            mobile=jsonObject.getString("mobile");
            email=jsonObject.getString("email");
            dob=jsonObject.getString("birth_date");
            dob=SmartUtils.getDateFromTimeStamp(Long.parseLong(dob));
            gender=jsonObject.getString("gender");
            dateOfregister=jsonObject.getString("dateofregister");


            if(userImage==null||userImage.length()<=0){
                if(gender!=null&&gender.length()>0){
                    if(gender.equals("Male")){
                        imgProfile.setImageDrawable(getResources().getDrawable(R.drawable.dymmy_user_male));
                    }else {
                        imgProfile.setImageDrawable(getResources().getDrawable(R.drawable.dymmy_user_female));
                    }
                }
            }else {
                aQuery.id(imgProfile).image(userImage,
                        true, true, getDeviceWidth(), 0);
            }
            dateOfregister=SmartUtils.getDateFromTimeStamp(Long.parseLong(dateOfregister));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tvUserName.setText(userName);
        tvUserEmail.setText(email);
        tvUserMobile.setText(mobile);
        tvGender.setText(gender);
        tvDateOfRegister.setText(dateOfregister);
        tvUserBirthDate.setText(dob);

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
            Intent intent=new Intent(VirtualBusesProfile.this,VirtualBusesEditProfile.class);
            intent.putExtra("IN_EMAIL",email);
            startActivity(intent);
        }
        return true;
    }


}
