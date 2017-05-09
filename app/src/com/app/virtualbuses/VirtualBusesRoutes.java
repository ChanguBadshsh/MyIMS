package com.app.virtualbuses;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.app.component.MasterActivity;
import com.app.virtualbuses.model.RouteModel;
import com.app.virtualbuses.reveiver.AlarmReceiver;
import com.smart.framework.Constants;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartUtils;
import com.smart.webservice.SmartWebManager;
import com.smartprime.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by tasol on 27/4/17.
 */

public class VirtualBusesRoutes extends MasterActivity {
    RecyclerView rvListing;
    LinearLayoutManager limo;
    ArrayList<RouteModel> row = new ArrayList<>();
    RecyclerViewAdapter listAdapter;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    private static VirtualBusesRoutes inst;

    @Override
    public void initComponents() {
        super.initComponents();
        getAllRoutes();
        rvListing = (RecyclerView) findViewById(R.id.rvListing);

        limo = new LinearLayoutManager(VirtualBusesRoutes.this);
        rvListing.setLayoutManager(limo);
        rvListing.setHasFixedSize(true);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

    }

    @Override
    public int getLayoutID() {
        return R.layout.virtual_buses_listing;
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        toolbar.setTitle("Routes");
    }

    public void getAllRoutes() {
        SmartUtils.showLoadingDialog(VirtualBusesRoutes.this);
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, VirtualBusesRoutes.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_REMOVE_WALLORACTIVITIES);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("task", "getAllRoutes");
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
                        JSONArray JArray = response.getJSONArray("userData");
                        for (int i = 0; i < JArray.length(); i++) {
                            JSONObject object = JArray.getJSONObject(i);
                            RouteModel routeModel = new RouteModel(object.getString("route_id"), object.getString("bus_number"), object.getString("start_station"), object.getString("end_station"), object.getString("line_no"), object.getString("arrival_time"));
                            row.add(routeModel);
                            listAdapter = new RecyclerViewAdapter();
                            rvListing.setAdapter(listAdapter);

                        }
                        JSONObject iObj = new JSONObject();
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
        SmartWebManager.getInstance(VirtualBusesRoutes.this).addToRequestQueueMultipart(requestParams, null, "", false);
    }


    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;

            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.virtual_buses_temp_item,
                    parent, false);
            viewHolder = new ViewHolder(parentView);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            final ViewHolder holder = (ViewHolder) viewHolder;
            holder.busNumber.setText(row.get(position).getBus_number());
            holder.startStation.setText(row.get(position).getStart_station());
            holder.endStation.setText(row.get(position).getEnd_station());
            holder.arrivalTime.setText(row.get(position).getArrival_time());


        }

        @Override
        public int getItemCount() {
            return row.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView busNumber, startStation, endStation, arrivalTime;
            Switch switchRemind;


            public ViewHolder(View itemView) {
                super(itemView);

                busNumber = (TextView) itemView.findViewById(R.id.busNumber);
                startStation = (TextView) itemView.findViewById(R.id.startStation);
                endStation = (TextView) itemView.findViewById(R.id.endStation);
                arrivalTime = (TextView) itemView.findViewById(R.id.arrivalTime);
                switchRemind=(Switch)itemView.findViewById(R.id.switchRemind);
                switchRemind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(compoundButton.isChecked()){
                            startNotification();
                        }
                    }
                });
            }
        }
    }


    public void startNotification(){
        Log.d("MyActivity", "Alarm On");
            int hour=17;
            int minutes=27;
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY,hour);
            c.set(Calendar.MINUTE,minutes);
            Intent myIntent = new Intent(VirtualBusesRoutes.this, AlarmReceiver.class);
            myIntent.putExtra("IN_TIME", "start");
            pendingIntent = PendingIntent.getBroadcast(VirtualBusesRoutes.this, 0, myIntent, 0);
            alarmManager.set(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);
    }


//    public void onToggleClicked(View view) {
//        if (((ToggleButton) view).isChecked()) {
//            Log.d("MyActivity", "Alarm On");
//            int hour=16;
//            int minutes=05;
//            Calendar c = Calendar.getInstance();
//            c.set(Calendar.HOUR_OF_DAY,hour);
//            c.set(Calendar.MINUTE,minutes);
//            Intent myIntent = new Intent(VirtualBusesRoutes.this, AlarmReceiver.class);
//            myIntent.putExtra("IN_TIME", "start");
//            pendingIntent = PendingIntent.getBroadcast(VirtualBusesRoutes.this, 0, myIntent, 0);
//            alarmManager.set(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);
//        } else {
//            Intent myIntent = new Intent(VirtualBusesRoutes.this, AlarmReceiver.class);
//            myIntent.putExtra("IN_TIME", "stop");
//            pendingIntent = PendingIntent.getBroadcast(VirtualBusesRoutes.this, 0, myIntent, 0);
//            alarmManager.cancel(pendingIntent);
//            setAlarmText("");
//            Log.d("MyActivity", "Alarm Off");
//        }
//    }


}
