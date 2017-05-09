package com.app.virtualbuses;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.smart.framework.Constants.SP_ISLOGOUT;
import static com.smart.framework.Constants.SP_LOGGED_IN_USER_DATA;


/**
 * Created by tasol on 11/4/17.
 */

public class VirtualBusesLogin extends Activity {
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private CallbackManager callbackManager;
    LoginButton loginButton;
    Button btnGoogle,btnRegister,btnLogin;
    ContentValues profileDetails= new ContentValues();
    Dialog dialog;
    EditText etUserPassword,etUserName;


    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            displayMessage(profile);
            getUserProfile(accessToken);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.virtual_buses_login);
        FacebookSdk.sdkInitialize(VirtualBusesLogin.this);

        loginButton = (LoginButton) findViewById(R.id.login_button);

        dialog = new Dialog(VirtualBusesLogin.this);
        btnGoogle=(Button)findViewById(R.id.btnGoogle);
        btnRegister=(Button)findViewById(R.id.btnRegister);

        etUserName=(EditText)findViewById(R.id.etUserName);
        etUserPassword=(EditText)findViewById(R.id.etUserPassword);

        btnLogin=(Button)findViewById(R.id.btnLogin);

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayMessage(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, callback);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValid=true;
                if(TextUtils.isEmpty(etUserName.getText().toString())){
                    isValid=false;
                    etUserName.setError(getString(R.string.empty_validation));

                }else if(TextUtils.isEmpty(etUserPassword.getText().toString())) {
                    isValid = false;
                    etUserPassword.setError(getString(R.string.empty_validation));
                }
                if(isValid){
                    authenticateUser(etUserName.getText().toString(),etUserPassword.getText().toString());
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(VirtualBusesLogin.this,VirtualBusesRegister.class);
                startActivity(intent);
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void displayMessage(Profile profile){
        if(profile != null){
            Log.v("@@@WWE"," "+profile);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        displayMessage(profile);
    }

    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {

                            String name="",email="",password="",image="";
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("isLogin","true");

                            String stringDtTime=SmartUtils.getCurrentDate();
                            String registerDate=String.valueOf(SmartUtils.getTimeStampInMillisecond(stringDtTime));
                            Log.v("@@@WWe"," date "+registerDate);


//                            if(object.has("email")){
                                if(object.has("picture")){
                                    profileDetails.put("image_data",object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                    image=object.getJSONObject("picture").getJSONObject("data").getString("url");
                                }
                                if(object.has("name")){
                                    profileDetails.put("user_name",object.getString("name"));
                                    name=object.getString("name").toString();
                                }

                                if(object.has("id")){
                                    profileDetails.put("password",object.getString("id"));
                                    password=object.getString("id").toString();
                                }
                              if(object.has("email")){
                                  profileDetails.put("user_email",object.getString("email"));
                                  email=object.getString("email").toString();
                              }
                                registerUserWithFaceBook(name,password,email,image,registerDate);


                            //sendMailAndVerify(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //  doSignup();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields","id,name,email,picture.width(200)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void registerUserWithFaceBook(final String name, final String password, final String email, final String image,final String registerDate){
        SmartUtils.showLoadingDialog(VirtualBusesLogin.this);
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, VirtualBusesLogin.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_REMOVE_WALLORACTIVITIES);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("task", "registerNewUser");
            JSONObject taskData = new JSONObject();
            try {
                taskData.put("name",name);
                taskData.put("passwsord",password);
                taskData.put("email",email);
                taskData.put("image",image);
                taskData.put("mobile","");
                taskData.put("birthdate","");
                taskData.put("rigisterdate",registerDate);
                taskData.put("gender","");
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
                    Intent loginIntent= new Intent(VirtualBusesLogin.this,Dashboard.class);
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
        SmartWebManager.getInstance(VirtualBusesLogin.this).addToRequestQueueMultipart(requestParams, null, "", false);
    }


    public void getUserEmail(final String name, final String password, final String image,final String registerDate) {

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.virtual_buses_get_emaildialog);

        final SmartEditText etEmailAddress=(SmartEditText)dialog.findViewById(R.id.etEmailAddress);
        SmartButton btnSubmit=(SmartButton)dialog.findViewById(R.id.btnSubmit);

        final String[] retVal = {""};


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retVal[0] =etEmailAddress.getText().toString();
                profileDetails.put("email",retVal[0]);
                registerUserWithFaceBook(name,password,retVal[0],image,registerDate);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
    }

    private void enterEmailDialog(final String name, final String password, final String image, final String registerDate) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.virtual_buses_get_emaildialog);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);


        final SmartEditText etEmailAddress=(SmartEditText)dialog.findViewById(R.id.etEmailAddress);
        SmartButton btnSubmit=(SmartButton)dialog.findViewById(R.id.btnSubmit);

        final String[] retVal = {""};


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retVal[0] =etEmailAddress.getText().toString();
                profileDetails.put("email",retVal[0]);
                registerUserWithFaceBook(name,password,retVal[0],image,registerDate);
            }
        });
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

    public void authenticateUser(final String email, final String password){
        SmartUtils.showLoadingDialog(VirtualBusesLogin.this);
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, VirtualBusesLogin.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_REMOVE_WALLORACTIVITIES);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("task", "authorizrUser");
            JSONObject taskData = new JSONObject();
            try {
                taskData.put("password",password);
                taskData.put("email",email);
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

                    getUserDetails(email);
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, false);
                    final Intent loginIntent= new Intent(VirtualBusesLogin.this,Dashboard.class);
                    loginIntent.putExtra("IN_EMAIL",email);
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(loginIntent);
                        }
                    },4000);

                }else {
                    SmartUtils.ting(VirtualBusesLogin.this,"Invalid Credentials");
                }
                SmartUtils.hideLoadingDialog();
            }

            @Override
            public void onResponseError() {

                SmartUtils.hideLoadingDialog();
            }
        });
        SmartWebManager.getInstance(VirtualBusesLogin.this).addToRequestQueueMultipart(requestParams, null, "", false);
    }

    public void getUserDetails(final String email){
        SmartUtils.showLoadingDialog(VirtualBusesLogin.this);
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, VirtualBusesLogin.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_REMOVE_WALLORACTIVITIES);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("task", "getUserDetail");
            JSONObject taskData = new JSONObject();
            try {
                taskData.put("email",email);
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
                        JSONObject iObj= userData.getJSONObject(0);
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_IN_USER_DATA, iObj.toString());
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
        SmartWebManager.getInstance(VirtualBusesLogin.this).addToRequestQueueMultipart(requestParams, null, "", false);
    }

}
