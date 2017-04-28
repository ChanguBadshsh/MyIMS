package com.app.component;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.app.virtualbuses.Dashboard;
import com.app.virtualbuses.VirtualBusesLogin;
import com.app.virtualbuses.VirtualBusesProfile;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.smartprime.R;
import com.smart.caching.SmartCaching;
import com.smart.common.Spannable;
import com.smart.customviews.RoundedImageView;
import com.smart.customviews.SmartTextView;
import com.smart.framework.AlertMagnatic;
import com.smart.framework.Constants;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartSuperMaster;
import com.smart.framework.SmartUtils;
import com.smart.webservice.SmartWebManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tasol on 23/6/15.
 */
public abstract class MasterActivity extends SmartSuperMaster
        implements Constants, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , ResultCallback<LocationSettingsResult>, LocationListener {

    //NavigationView
    private RecyclerView rvNavigationView;
    private RecyclerViewNavigationAdapter recyclerViewNavigationAdapter;
    private LinearLayoutManager linearLayoutManager;
    private JSONArray IN_NAVIGATION_DATA = new JSONArray();
    private SmartCaching smartCaching;
    private AQuery aQuery;
    public static boolean streamFlag = false;

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec

    /**
     * Constant used in the location settings dialog.
     */
    protected static final int REQUEST_CHECK_SETTINGS = 0x8;

    final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 501;


    List<String> navList= new ArrayList<>();
    List<String> imageArr=new ArrayList<>();
    String userName="",userImage="";

    @Override
    public View getFooterLayoutView() {
        return null;
    }

    @Override
    public int getFooterLayoutID() {
        return 0;
    }

    @Override
    public View getHeaderLayoutView() {
        return null;
    }

    @Override
    public int getHeaderLayoutID() {
        return 0;
    }

    @Override
    public void preOnCreate() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        imageArr.add("R.drawable.dashboard");

        navList.add("Dashboard");
        navList.add("Profile");
        navList.add("Rides");
        navList.add("Routes");
        navList.add("Offers");
        navList.add("Logout");

        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
            buildLocationSettingsRequest();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public View getLayoutView() {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setAnimations() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setAllowReturnTransitionOverlap(true);
    }

    @Override
    public void initComponents() {
        Log.v(getClass().getName(), "Come in initComponents()");
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        try {
            JSONObject jsonObject= new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                    .getString(SP_LOGGED_IN_USER_DATA, null));
            userName=jsonObject.getString("name");
            userImage=jsonObject.getString("image");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (getDrawerLayoutID() != 0) {
            aQuery = new AQuery(this);
            smartCaching = new SmartCaching(this);
            try {
                ArrayList<ContentValues> result = smartCaching.getDataFromCache("menus",
                        "select menuitem from menus where screens LIKE '%"
//                                + IjoomerScreenHolder.aliasScreens.get(getClass().getSimpleName())
                                + "%' and menuposition='2'");
                if (result != null && result.size() > 0) {
//                    IN_NAVIGATION_DATA = new JSONArray(result.get(0).getAsString(MENUITEM));
                }
                rvNavigationView = (RecyclerView) findViewById(R.id.rvNavigationView);
                linearLayoutManager = new LinearLayoutManager(this);
                rvNavigationView.setLayoutManager(linearLayoutManager);
                rvNavigationView.setHasFixedSize(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void prepareViews() {
        if (getDrawerLayoutID() != 0) {
            recyclerViewNavigationAdapter = new RecyclerViewNavigationAdapter();
            rvNavigationView.setAdapter(recyclerViewNavigationAdapter);
        }
    }

    public void updateNavigationView() {
        if (recyclerViewNavigationAdapter != null) {
            recyclerViewNavigationAdapter.notifyItemChanged(0);
        }
    }

    @Override
    public void setActionListeners() {

    }

    @Override
    public void postOnCreate() {
    }

    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return true;
    }

    @Override
    public int getDrawerLayoutID() {
        return R.layout.drawer;
    }

    /**
     * This method used to get simple text from voice data.
     *
     * @param strData represented voice data
     * @return represented {@link String}
     */
    public String getPlainText(String strData) {
        try {
            if (strData.contains("{voice}")) {
                strData = strData.substring(0, strData.indexOf("{voice}"));
            }
            return strData;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    private class RecyclerViewNavigationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == TYPE_HEADER) {
                View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_header,
                        parent, false);
                RecyclerView.ViewHolder viewHolder = new HeaderViewHolder(parentView);
                return viewHolder;
            } else if (viewType == TYPE_ITEM) {
                View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_list_item,
                        parent, false);
                RecyclerView.ViewHolder viewHolder = new ViewHolder(parentView);
                return viewHolder;
            }
            return null;
//                View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_list_item,
//                        parent, false);
//                RecyclerView.ViewHolder viewHolder = new ViewHolder(parentView);
//                return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

            if (viewHolder instanceof HeaderViewHolder) {
                HeaderViewHolder headerHolder = (HeaderViewHolder) viewHolder;
                try {
                    headerHolder.txtUserName.setText(userName);
                    aQuery.id(headerHolder.imgUserAvatar).image(userImage,
                            true, true, getDeviceWidth(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (viewHolder instanceof ViewHolder) {
                try {
                    ViewHolder holder = (ViewHolder) viewHolder;



                    TypedArray icon=getResources().obtainTypedArray(R.array.menu_icon);

                    holder.imgMenuItem.setImageResource(icon.getResourceId(position,-1));

                    holder.txtMenuItem.setText(navList.get(position));
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if(navList.get(position).equals("Logout")){
                                    LoginManager.getInstance().logOut();
                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, true);
                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_IN_USER_DATA, null);

                                    Intent intent= new Intent(MasterActivity.this, VirtualBusesLogin.class);
                                    startActivity(intent);
                                }
                                if(navList.get(position).equals("Profile")){
//                                    LoginManager.getInstance().logOut();
//                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, true);
//                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_IN_USER_DATA, null);
                                    Intent intent= new Intent(MasterActivity.this, VirtualBusesProfile.class);
                                    startActivity(intent);
                                }
                                if(navList.get(position).equals("Dashboard")){
//                                    LoginManager.getInstance().logOut();
//                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, true);
//                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_IN_USER_DATA, null);
                                    Intent intent= new Intent(MasterActivity.this, Dashboard.class);
                                    startActivity(intent);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            closeDrawer();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public int getItemViewType(int position) {
            if (isPositionHeader(position)) {
                return TYPE_HEADER;
            }
            return TYPE_ITEM;
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }

        @Override
        public int getItemCount() {
            return navList.size();
        }

        public class HeaderViewHolder extends RecyclerView.ViewHolder {
            public RoundedImageView imgUserAvatar;
            public SmartTextView txtUserName;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                imgUserAvatar = (RoundedImageView) itemView.findViewById(R.id.imgUserAvatar);
                txtUserName = (SmartTextView) itemView.findViewById(R.id.txtUserName);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView imgMenuItem;
            public SmartTextView txtMenuItem;

            public ViewHolder(View itemView) {
                super(itemView);

                txtMenuItem = (SmartTextView) itemView.findViewById(R.id.txtMenuItem);
                imgMenuItem= (ImageView) itemView.findViewById(R.id.imgMenuItem);
            }
        }
    }

    /**
     * This method used to add clicable part on spannable string.
     *
     * @param strSpanned represented spannable string
     * @param row        represented spannable data
     * @return represented {@link SpannableStringBuilder}
     */
    public SpannableStringBuilder addClickablePart(Spanned strSpanned, final ContentValues row) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(row.getAsString(USER_NAME))) {
            final String IN_USERID = row.getAsString(USER_ID);
            ssb.setSpan(new Spannable(getResources().getColor(R.color.accent), true) {

                @Override
                public void onClick(View widget) {

                    if (row.getAsString(PROFILE_ACCESS).equalsIgnoreCase("1")) {
//                        gotoProfile(IN_USERID);
                    }
                }
            }, str.indexOf(row.getAsString(USER_NAME)), str.indexOf(row.getAsString(USER_NAME)) + row.getAsString(USER_NAME).length(), 0);
        }
        return ssb;
    }

    public ContentValues jsonToContentValues(JSONObject object) throws JSONException {
        ContentValues map = new ContentValues();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)).toString());
        }
        return map;
    }

    private Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            return jsonToContentValues((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        } else {
            return json;
        }
    }

    private List toList(JSONArray array) throws JSONException {
        List list = new ArrayList();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    public int convertSizeToDeviceDependent(int value) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return ((dm.densityDpi * value) / 160);
    }

    public void onShareClick(final String shareLink) {
        Resources resources = getResources();

        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_TEXT, shareLink);
        emailIntent.setType("message/rfc822");

        PackageManager pm = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.share_via));
        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<>();
        for (int i = 0; i < resInfo.size(); i++) {
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("twitter") || packageName.contains("facebook")
                    || packageName.contains("plus") || packageName.contains("whatsapp")
                    || packageName.contains("mms")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, shareLink);
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }
        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }

    public void doLogout() {
        SmartUtils.getConfirmDialog(MasterActivity.this, getString(R.string.logout), getString(R.string.logout_message),
                getString(R.string.yes), getString(R.string.no), true, new AlertMagnatic() {

                    @Override
                    public void PositiveMethod(DialogInterface dialog, int id) {
                        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, MasterActivity.this);
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_GET_CONFIGURATION);
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put(TASK, LOGOUT);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, jsonObject);
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

                            @Override
                            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                                if (responseCode == 200 || responseCode == 400) {
                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, true);
                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGIN_REQ_OBJECT, null);

//                                    Intent loginIntent = new Intent(SobiproMasterActivity.this, IjoomerLoginActivity.class);
//                                    SmartUtils.clearActivityStack(SobiproMasterActivity.this, loginIntent);
                                }
                            }

                            @Override
                            public void onResponseError() {

                            }
                        });
                        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", false);
                    }

                    @Override
                    public void NegativeMethod(DialogInterface dialog, int id) {

                    }
                });
    }

    public JSONObject getLoggedUserDetail() {
        try {
            String result = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_IN_USER_DATA, "");
            return new JSONObject(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method used to get readable file size from {@link Long} size.
     *
     * @param size represented long size
     * @return represented {@link String}
     */
    public String readableFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * This method used to add fragment to given layout id.
     *
     * @param layoutId represented layout id
     * @param fragment represented fragment
     */
    public void addFragment(int layoutId, Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(layoutId, fragment);
        ft.commit();
    }

    /**
     * Method to verify google play services on the device
     */
    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:

                Log.i("GoogleLocationApi", "All location settings are satisfied.");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
                } else {

                    startLocationUpdates();
                }
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                Log.i("GoogleLocationApi", "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(MasterActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i("GoogleLocationApi", "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                Log.i("GoogleLocationApi", "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:

                        Log.i("GoogleLocationApi", "User agreed to make required location settings changes.");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {

                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
                        } else {

                            startLocationUpdates();
                        }
                        break;
                    case Activity.RESULT_CANCELED:

                        Log.i("GoogleLocationApi", "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // Do something with granted permission
            startLocationUpdates();
        }
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Uses a {@link LocationSettingsRequest.Builder} to build
     * a {@link LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.v("@@@START LATITUDE", "" + mLastLocation.getLatitude());
            Log.v("@@@START LONGITUDE", "" + mLastLocation.getLongitude());

            SmartUtils.setLatitude("" + mLastLocation.getLatitude());
            SmartUtils.setLongitude("" + mLastLocation.getLongitude());

            if (mGoogleApiClient.isConnected()) {

                stopLocationUpdates();
            }
        } else {

            checkLocationSettings();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mLastLocation != null) {
            Log.v("@@@START LATITUDE", "" + mLastLocation.getLatitude());
            Log.v("@@@START LONGITUDE", "" + mLastLocation.getLongitude());

            SmartUtils.setLatitude("" + mLastLocation.getLatitude());
            SmartUtils.setLongitude("" + mLastLocation.getLongitude());

            if (mGoogleApiClient.isConnected()) {

                stopLocationUpdates();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d("gcm", "location failed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public boolean isNetworkConnected() {
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
