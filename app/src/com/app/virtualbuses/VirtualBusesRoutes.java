package com.app.virtualbuses;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.widget.Switch;
import android.widget.TextView;

import com.app.component.MasterActivity;
import com.app.virtualbuses.model.BusRouteModel;
import com.app.virtualbuses.model.Route;
import com.app.virtualbuses.model.RouteModel;
import com.app.virtualbuses.model.Stations;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    List<BusRouteModel> busRouteModelList= new ArrayList<>();
    List<String>busList= new ArrayList<>();
    List<Stations>stationList= new ArrayList<>();
    private static VirtualBusesRoutes inst;
    String inputFormat = "HH:mm";
    SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat);
    List<Route>routeList= new ArrayList<>();
    List<JSONObject>jObjList= new ArrayList<>();

    @Override
    public void initComponents() {
        super.initComponents();

        RouteModel routeModel= new RouteModel("1","101","A","B","2","12:32");
        RouteModel routeModel1= new RouteModel("2","102","B","E","2","10:19");
        RouteModel routeModel2= new RouteModel("3","103","A","E","2","13:10");
        row.add(routeModel);
        row.add(routeModel1);
        row.add(routeModel2);

//        getAllRoutes();
//        getRoutesFromSD("A","B");
        rvListing = (RecyclerView) findViewById(R.id.rvListing);

        limo = new LinearLayoutManager(VirtualBusesRoutes.this);
        rvListing.setLayoutManager(limo);
        rvListing.setHasFixedSize(true);


        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        String ids =SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_BUS_IDS,"");
        String[] busIds=ids.split(",");
        ids=ids.replace('[',' ');
        ids=ids.replace(']',' ');
        String[] finalID=ids.split(",");
        for (int i = 0; i < finalID.length; i++) {
            Log.v("@@@WWe"," "+i+" "+finalID[i]);
            getAllRoutes(finalID[i]);
        }
        Log.v("@@@WWE"," "+routeList);
        listAdapter = new RecyclerViewAdapter();
        rvListing.setAdapter(listAdapter);

    }

    @Override
    public int getLayoutID() {
        return R.layout.virtual_buses_listing;
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        toolbar.setTitle("Routes");
    }

    public void getAllRoutes(String busIds) {
        SmartUtils.showLoadingDialog(VirtualBusesRoutes.this);
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, VirtualBusesRoutes.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_REMOVE_WALLORACTIVITIES);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("task","getBusRoutes");
            JSONObject taskData = new JSONObject();
            try {
                taskData.put("bus_ids",busIds);
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
                        JSONArray userData = response.getJSONArray("userData");
                        for (int i = 0; i < userData.length(); i++) {
                            JSONObject jsonObject1=userData.getJSONObject(i);
                            jObjList.add(jsonObject1);
                        }
                        String start="",end="",start_time="",end_time="",busID="";
                        for (int i = 0; i < jObjList.size(); i++) {
                            if(i==0){
                                start=jObjList.get(i).getString("station");
                                start_time=jObjList.get(i).getString("arrival_time");
                                busID=jObjList.get(i).getString("bus_id");
                            }
                            if(i==jObjList.size()){
                                end=jObjList.get(i).getString("station");
                                end_time=jObjList.get(i).getString("departure_time");
                            }
                        }
                        Route route= new Route(busID,start,end,start_time,end_time);
                        routeList.add(route);

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


    public BusRouteModel getBusDetails(String busID) {
        final BusRouteModel[] model = new BusRouteModel[1];
        SmartUtils.showLoadingDialog(VirtualBusesRoutes.this);
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, VirtualBusesRoutes.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_REMOVE_WALLORACTIVITIES);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("task", "getBusRouteDetails");
            JSONObject taskData = new JSONObject();
            try {
                taskData.put("bus_id",busID);
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


//                            RouteModel routeModel = new RouteModel(object.getString("route_id"), object.getString("bus_number"), object.getString("start_station"), object.getString("end_station"), object.getString("line_no"), object.getString("arrival_time"));
//                            row.add(routeModel);
//                            listAdapter = new RecyclerViewAdapter();
//                            rvListing.setAdapter(listAdapter);
                        }
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
        return model[0];
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
            holder.busNumber.setText(routeList.get(position).getBus_id());
            holder.startStation.setText(routeList.get(position).getStart_station());
            holder.endStation.setText(routeList.get(position).getEnd_station());
            holder.arrivalTime.setText(routeList.get(position).getArrival_time());
            holder.switchRemind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(compoundButton.isChecked()){
                        startNotification(row.get(position));
                    }else if(!compoundButton.isChecked()){
                        cancelNotification();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return routeList.size();
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

            }
        }
    }


    public void startNotification(RouteModel busRouteModel){
        Log.d("MyActivity", "Alarm On");


            String arrivaltime=busRouteModel.getArrival_time();
            String[] time=arrivaltime.split(":");
            int hour=Integer.parseInt(time[0]);
            int minutes=Integer.parseInt(time[1]);;
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY,hour);
            c.set(Calendar.MINUTE,minutes);


            Calendar now = Calendar.getInstance();
            int nowHour = now.get(Calendar.HOUR);
            int nowMinute = now.get(Calendar.MINUTE);

             Date currentTime=parseDate(nowHour+":"+nowMinute);
             Date compareTime=parseDate(arrivaltime);

             if(currentTime.before(compareTime)){
                 Log.v("@@@WWE"," Current Time "+currentTime.toString());
                 Log.v("@@@WWE"," Compare Time "+compareTime.toString());
                 Log.v("@@@WWE"," Time Before");
             }else if(currentTime.after(compareTime)){
                 Log.v("@@@WWE"," Current Time "+currentTime.toString());
                 Log.v("@@@WWE"," Compare Time "+compareTime.toString());
                 Log.v("@@@WWE"," Time After");
             }else {
                 Log.v("@@@WWE"," Current Time "+currentTime.toString());
                 Log.v("@@@WWE"," Compare Time "+compareTime.toString());
                 Log.v("@@@WWE"," Time Same");
             }





//            Intent myIntent = new Intent(VirtualBusesRoutes.this, AlarmReceiver.class);
//            myIntent.putExtra("IN_TIME", "start");
//            pendingIntent = PendingIntent.getBroadcast(VirtualBusesRoutes.this, 0, myIntent, 0);
//            alarmManager.set(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);


    }
    public void cancelNotification(){
        Log.d("MyActivity", "Alarm Off");
        int hour=12;
        int minutes=03;
        Calendar c = Calendar.getInstance();
//        c.set(Calendar.HOUR_OF_DAY);
//        c.set(Calendar.MINUTE,minutes);
//        Intent myIntent = new Intent(VirtualBusesRoutes.this, AlarmReceiver.class);
//        myIntent.putExtra("IN_TIME", "stop");
//        pendingIntent = PendingIntent.getBroadcast(VirtualBusesRoutes.this, 0, myIntent, 0);
//        alarmManager.set(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);
    }


    private Date parseDate(String date) {

        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }




}
