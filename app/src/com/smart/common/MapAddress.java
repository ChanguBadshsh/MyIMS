package com.smart.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidmapsextensions.GoogleMap;
import com.app.src.CoreMaster;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.smartprime.R;
import com.smart.customviews.SmartEditText;
import com.smart.customviews.SmartTextView;
import com.smart.framework.SmartUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapAddress extends CoreMaster {

    private RecyclerView rvMapAddress;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAddressesAdapter recyclerViewAddressesAdapter;

    private GoogleMap googleMap;

    private SmartEditText editSearch;
    private LinearLayout btnSearch;
    private ProgressBar pbrMapAddress;

    private TouchableWrapper relMap;

    private ArrayList<HashMap<String, String>> addressList = new ArrayList<>();

    /**
     * Overrides method
     */
    @Override
    public int getLayoutID() {
        return R.layout.map_address;
    }

    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    @Override
    public void initComponents() {
        super.initComponents();
        relMap = (TouchableWrapper) findViewById(R.id.relMap);
        googleMap = getMapView();

        linearLayoutManager = new LinearLayoutManager(this);
        rvMapAddress = (RecyclerView) findViewById(R.id.rvMapAddress);
        rvMapAddress.setHasFixedSize(true);
        rvMapAddress.setLayoutManager(linearLayoutManager);
        rvMapAddress.addItemDecoration(new DividerItemDecoration(MapAddress.this, R.drawable.ijoomer_list_devider));

        pbrMapAddress = (ProgressBar) findViewById(R.id.pbrMapAddress);
        editSearch = (SmartEditText) findViewById(R.id.editSearch);
        btnSearch = (LinearLayout) findViewById(R.id.btnSearch);

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
    }

    @Override
    public void prepareViews() {

        try {
            setAddressData(Double.parseDouble(SmartUtils.getLatitude()), Double.parseDouble(SmartUtils.getLongitude()));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(SmartUtils.getLatitude()),
                    Double.parseDouble(SmartUtils.getLongitude())), 15.0f));
        } catch (Exception e) {
        }
    }

    @Override
    public void setActionListeners() {

        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    SmartUtils.hideSoftKeyboard(MapAddress.this);
                    if (editSearch.getText().toString().trim().length() > 0) {
                        try {
                            Address address = SmartUtils.getLatLongFromAddress(MapAddress.this, editSearch.getText().toString().trim());
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(address.getLatitude(), address.getLongitude())).tilt(50).zoom(15).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            setAddressData(address.getLatitude(), address.getLongitude());
                            editSearch.setText(null);
                        } catch (Exception e) {
                            editSearch.setText(null);
                        }
                    } else {
                        SmartUtils.hideSoftKeyboard(MapAddress.this);
                    }
                    return true;
                }
                return false;
            }
        });

        relMap.setOnTouchUpListener(new TouchableWrapper.onTouchUpListener() {
            @Override
            public void onTouchUp() {
                LatLng center = googleMap.getCameraPosition().target;
                setAddressData(center.latitude, center.longitude);

            }
        });

        btnSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SmartUtils.hideSoftKeyboard(MapAddress.this);
                if (editSearch.getText().toString().trim().length() > 0) {
                    try {
                        Address address = SmartUtils.getLatLongFromAddress(MapAddress.this, editSearch.getText().toString().trim());
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(address.getLatitude(), address.getLongitude())).tilt(50).zoom(15).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        setAddressData(address.getLatitude(), address.getLongitude());
                        editSearch.setText(null);
                    } catch (Exception e) {
                        editSearch.setText(null);
                    }
                }
            }
        });
    }

    /**
     *  Class method
     */

    /**
     * This method used to getting address from lat-lng.
     *
     * @param lat represented latitude
     * @param lng represented longitude
     */
    private void setAddressData(double lat, double lng) {
        if (lat != 0 && lng != 0) {

            getGooglePlaces(lat, lng);
        }
    }

    private void getGooglePlaces(final double lat, final double lng) {
        addressList.clear();
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pbrMapAddress.setVisibility(View.VISIBLE);
            }

            @Override
            protected JSONObject doInBackground(Void... voids) {
                try {
                    Geocoder geocoder = new Geocoder(MapAddress.this);
                    try {
                        List<Address> list = geocoder.getFromLocation(lat, lng, 10);
                        if (list != null && list.size() > 0) {
                            for (Address address : list) {
                                HashMap<String, String> data = new HashMap<String, String>();
                                if (address.getAddressLine(0).toString().trim().length() > 0) {
                                    data.put("address", address.getAddressLine(0));
                                    data.put("latitude", String.valueOf(address.getLatitude()));
                                    data.put("longitude", String.valueOf(address.getLongitude()));
                                    addressList.add(data);
                                } else if (address.getAddressLine(1).toString().trim().length() > 0) {
                                    data.put("address", address.getAddressLine(1));
                                    data.put("latitude", String.valueOf(address.getLatitude()));
                                    data.put("longitude", String.valueOf(address.getLongitude()));
                                    addressList.add(data);
                                } else if (address.getLocality().toString().trim().length() > 0) {
                                    data.put("address", address.getLocality());
                                    data.put("latitude", String.valueOf(address.getLatitude()));
                                    data.put("longitude", String.valueOf(address.getLongitude()));
                                    addressList.add(data);
                                } else if (address.getAdminArea().toString().trim().length() > 0) {
                                    data.put("address", address.getAdminArea());
                                    data.put("latitude", String.valueOf(address.getLatitude()));
                                    data.put("longitude", String.valueOf(address.getLongitude()));
                                    addressList.add(data);
                                } else if (address.getCountryName().toString().trim().length() > 0) {
                                    data.put("address", address.getAdminArea());
                                    data.put("latitude", String.valueOf(address.getLatitude()));
                                    data.put("longitude", String.valueOf(address.getLongitude()));
                                    addressList.add(data);
                                }
                            }
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);

                recyclerViewAddressesAdapter = new RecyclerViewAddressesAdapter();
                rvMapAddress.setAdapter(recyclerViewAddressesAdapter);
                pbrMapAddress.setVisibility(View.GONE);
            }
        }.execute();
    }

    private void reportDialog(final HashMap<String, String> location) {

        View locationView = LayoutInflater.from(MapAddress.this).inflate(R.layout.select_location_dialog, null);

        final SmartEditText edtLocation = (SmartEditText) locationView.findViewById(R.id.edtLocation);
        edtLocation.setText(location.get("address"));

        AlertDialog.Builder builder = new AlertDialog.Builder(MapAddress.this, R.style.AppCompatAlertDialogStyle)
                .setTitle(getString(R.string.select_locaton))
                .setPositiveButton(getString(R.string.select), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(edtLocation.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        dialog.cancel();

                        location.put("address", edtLocation.getText().toString());

                        Intent intent = new Intent();
                        intent.putExtra("MAP_ADDRESSS_DATA", location);
                        setResult(Activity.RESULT_OK, intent);

                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(edtLocation.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        dialog.cancel();
                    }
                });
        builder.setView(locationView);
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
    }

    /**
     * List adapter
     */
    private class RecyclerViewAddressesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_address_item,
                    parent, false);
            RecyclerView.ViewHolder viewHolder = new ViewHolder(parentView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            ViewHolder holder = (ViewHolder) viewHolder;

            final HashMap<String, String> row = addressList.get(position);

            holder.txtMapAddressData.setText(row.get("address"));
            holder.itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    reportDialog(row);
                }
            });
        }

        @Override
        public int getItemCount() {
            return addressList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public SmartTextView txtMapAddressData;

            public ViewHolder(View itemView) {
                super(itemView);

                txtMapAddressData = (SmartTextView) itemView.findViewById(R.id.txtMapAddressData);
            }
        }
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getString(R.string.select_locaton));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });
    }
}
