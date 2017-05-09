package com.app.virtualbuses;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tasol on 27/4/17.
 */

public class VirtualBusesListing extends MasterActivity {
    RecyclerView rvListing;
    LinearLayoutManager limo;
    ArrayList<RouteModel> row= new ArrayList<>();
    RecyclerViewAdapter listAdapter;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    @Override
    public void initComponents() {
        super.initComponents();
        getAllRoutes();
        rvListing=(RecyclerView) findViewById(R.id.rvListing);

        limo= new LinearLayoutManager(VirtualBusesListing.this);
        rvListing.setLayoutManager(limo);
        rvListing.setHasFixedSize(true);
        alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);

    }

    @Override
    public int getLayoutID() {
        return R.layout.virtual_buses_listing;
    }
    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        toolbar.setTitle("Profile");
    }

    public void getAllRoutes(){
        SmartUtils.showLoadingDialog(VirtualBusesListing.this);
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, VirtualBusesListing.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_REMOVE_WALLORACTIVITIES);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("task","getAllRoutes");
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
                        JSONArray JArray=response.getJSONArray("userData");
                        for (int i = 0; i < JArray.length(); i++) {
                            JSONObject object=JArray.getJSONObject(i);
                            RouteModel routeModel=new RouteModel(object.getString("route_id"),object.getString("bus_number"),object.getString("start_station"),object.getString("end_station"),object.getString("line_no"),object.getString("arrival_time"));
                            row.add(routeModel);
                            listAdapter= new RecyclerViewAdapter();
                            rvListing.setAdapter(listAdapter);

                        }
                        JSONObject iObj= new JSONObject();
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
        SmartWebManager.getInstance(VirtualBusesListing.this).addToRequestQueueMultipart(requestParams, null, "", false);
    }


    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_ITEM = 1;
        private final int VIEW_PROGRESS = 0;

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
            holder.lineNumber.setText(row.get(position).getLine_number());
            holder.startStation.setText(row.get(position).getStart_station());
            holder.endStation.setText(row.get(position).getEnd_station());
            holder.arrivalTime.setText(row.get(position).getArrival_time());

            holder.btnRemind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.etSettime.getText().toString();
                    setAlarm(holder.etSettime.getText().toString());
                }
            });
            holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelAlarm();
                }
            });
        }

        @Override
        public int getItemCount() {
            return row.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView busNumber,lineNumber,startStation,endStation,arrivalTime;
            Button btnRemind,btnCancel;
            EditText etSettime;

            public ViewHolder(View itemView) {
                super(itemView);

                busNumber=(TextView)itemView.findViewById(R.id.busNumber);
                lineNumber=(TextView)itemView.findViewById(R.id.lineNumber);
                startStation=(TextView)itemView.findViewById(R.id.startStation);
                endStation=(TextView)itemView.findViewById(R.id.endStation);
                arrivalTime=(TextView)itemView.findViewById(R.id.arrivalTime);
                btnRemind=(Button)itemView.findViewById(R.id.btnRemind);
                etSettime=(EditText)itemView.findViewById(R.id.etSettime);
                btnCancel=(Button)itemView.findViewById(R.id.btnCancel);
            }
        }
    }

    public void setAlarm(String time){
        int timeInt=Integer.parseInt(time);
        Intent intent=new Intent(VirtualBusesListing.this, AlarmReceiver.class);
        pendingIntent=PendingIntent.getBroadcast(this.getApplicationContext(),234324243,intent,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+(timeInt*1000),pendingIntent);
        SmartUtils.ting(VirtualBusesListing.this,"Reminder Set "+timeInt+" seconds");
    }
    public void cancelAlarm(){
        if(alarmManager!=null){
            Intent intent=new Intent(VirtualBusesListing.this, AlarmReceiver.class);
            pendingIntent=PendingIntent.getBroadcast(this.getApplicationContext(),234324243,intent,0);
            alarmManager.cancel(pendingIntent);
        }
    }


}
