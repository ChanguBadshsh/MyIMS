package com.smart.framework;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.smartprime.R;
import com.smart.caching.SmartCaching;
import com.smart.common.AnnouncementAndDiscussionListener;
import com.smart.common.RealPathUtil;
import com.smart.customviews.CustomClickListener;
import com.smart.customviews.CustomTimePickerDialog;
import com.smart.customviews.MultipleSelectListener;
import com.smart.customviews.SelectImageDialogListner;
import com.smart.customviews.SmartDatePickerView;
import com.smart.customviews.SmartSpannable;
import com.smart.customviews.SmartTextView;
import com.smart.utilities.Iso2Phone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tasol on 23/5/15.
 */

public class SmartUtils implements Constants {

    private static final String TAG = "SmartUtil";
    private static boolean isNetworkAvailable;
    private static ProgressDialog progressDialog;
    private static Dialog loadingDialog;
    private static Geocoder geocoder;
    private static String imgPath;
     static Context mContext;
    private static AQuery aQuery;
    private static TimePickerDialog timePickerDialog;
    final private int GET_ADDRESS_FROM_MAP = 2;
    static SmartCaching smartCaching ;
//    static RecyclerViewCategoryAdapter recyclerViewCategoryAdapter;
    private static LinearLayoutManager linearLayoutManager;

    private static HashMap<String,String> selectedItems = new HashMap<>();

    static StringBuilder newValue = new StringBuilder();
    static StringBuilder newIds = new StringBuilder();

    private static ArrayList<ContentValues> listDataSideMenu = new ArrayList<ContentValues>();


//    public static void getVMMultiSelectionFilterDialogPartTwo(Context context, final MultipleSelectListener target){
//        mContext=context;
//        smartCaching=new SmartCaching(mContext);
////        listDataSideMenu = getCategories();
//        final Dialog dialog=new Dialog(context);
//        dialog.setContentView(R.layout.virtuemart_drawer_in_dialog);
//
//        RecyclerView sideMenuList;
//
//        SmartTextView cancel_filter_btn;
//        SmartTextView ok_filter_btn;
//        cancel_filter_btn = (SmartTextView) dialog.findViewById(R.id.cancel_filter_btn);
//        ok_filter_btn = (SmartTextView) dialog.findViewById(R.id.ok_filter_btn);
//
//        sideMenuList = (RecyclerView) dialog.findViewById(R.id.sideMenuList);
//        sideMenuList.setHasFixedSize(true);
//        linearLayoutManager = new LinearLayoutManager(mContext);
//        sideMenuList.setLayoutManager(linearLayoutManager);
//        dialog.show();
//        listDataSideMenu = getCategories();
//        recyclerViewCategoryAdapter = new RecyclerViewCategoryAdapter();
//        sideMenuList.setAdapter(recyclerViewCategoryAdapter);
//        ok_filter_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int size = selectedItems.size();
//
//                newValue = new StringBuilder();
//                newIds = new StringBuilder();
//                Iterator it = selectedItems.entrySet().iterator();
//                while (it.hasNext()) {
//                    Map.Entry pair = (Map.Entry)it.next();
//                    System.out.println(pair.getKey() + " = " + pair.getValue());
//
//
//                    newValue.append(pair.getValue()+",");
//                    newIds.append(pair.getKey()+ ",");
//                    it.remove(); // avoids a ConcurrentModificationException
//                }
//
//
////                for (int i = 0; i < size; i++) {
////                        newValue.append(newValue.length() > 0 ? "," + values.get(i) : values.get(i));
////                        newIds.append(newIds.length() > 0 ? "," + ids.get(i) : ids.get(i));
////                }
//
//                if(newValue.toString().length()>0){
//                    target.onClick(newValue.toString().substring(0,newValue.toString().length()-1),newIds.toString().substring(0,newIds.toString().length()-1));
//                }else{
//                    target.onClick("","'");
//                }
//
//                dialog.dismiss();
//
//            }
//        });
//        cancel_filter_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//    }

    private static ArrayList<ContentValues> getCategories() {
        ArrayList<ContentValues> categories = new SmartCaching(mContext).
                getDataFromCache(VIRTUEMARTCATEGORIES, "SELECT * FROM " + VIRTUEMARTCATEGORIES);
        return categories;
    }

    private static boolean hasChildCategories(ContentValues category) {
        try {
            ArrayList<ContentValues> categories = getChildCategories(category);
            if (categories != null && categories.size() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static ArrayList<ContentValues> getChildCategories(ContentValues category) {
        try {
            JSONArray childCategories = new JSONArray(category.getAsString(CHILDREN));
            final String[] unNormalizedFields = {IMAGES, CHILDREN};
            HashMap<String, ArrayList<ContentValues>> result = smartCaching.parseResponse(childCategories,
                    VIRTUEMARTPRODUCTS, unNormalizedFields);
            return result.get(VIRTUEMARTPRODUCTS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
//    private static class RecyclerViewCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.virtuemart_sidemenu_list_group,
//                    parent, false);
//            RecyclerView.ViewHolder viewHolder = new RecyclerViewCategoryAdapter.ViewHolder(parentView);
//            return viewHolder;
//        }
//
//        @Override
//        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
//            final RecyclerViewCategoryAdapter.ViewHolder holder = (RecyclerViewCategoryAdapter.ViewHolder) viewHolder;
//
//
//            final ContentValues row = listDataSideMenu.get(position);
//            Log.d("ROW = ",row.toString());
//            holder.itemView.setId(row.getAsInteger(ID));
//            holder.txtMenuItemCaption.setText(row.getAsString(NAME).trim());
//            holder.txtMenuItemCaption.setTag(row);
//
//
//            if(selectedItems.size()>0 && selectedItems.containsKey(row.getAsString("id"))){
//                holder.childCheckBox.setChecked(true);
//            }else{
//                holder.childCheckBox.setChecked(false);
//            }
//
//            holder.childCheckBox.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(holder.childCheckBox.isChecked()){
//                        selectedItems.put(row.getAsString("id"),row.getAsString("name"));
//                    }else{
//                        selectedItems.remove(row.getAsString("id"));
//                    }
//                    notifyDataSetChanged();
//
//                }
//            });
////            holder.childCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
////                @Override
////                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                    if(isChecked){
////                        values.add(row.getAsString("name"));
////                        ids.add(row.getAsString("id"));
////                        Log.v("@@@WS","Values Array "+values.toString());
////                        Log.v("@@@WS","ID Array "+ids.toString());
////                    }
////                }
////            });
//
//
//
//
//            if (hasChildCategories(row)) {
//                holder.childCheckBox.setVisibility(View.GONE);
//                holder.txtMenuItemCaption.setId(R.id.collapsed);
//                holder.txtMenuItemCaption.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_down_grey600_24dp, 0);
//            } else {
//                holder.childCheckBox.setVisibility(View.VISIBLE);
//                holder.txtMenuItemCaption.setId(R.id.no_children);
//            }
//
//            holder.txtMenuItemCaption.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View view) {
//                    if (view.getId() == R.id.collapsed) {
//                        view.setId(R.id.expanded);
//                        ((SmartTextView) view).setTypeface(null, Typeface.BOLD);
//                        ((SmartTextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_up_grey600_24dp, 0);
//
//                        holder.childView.removeAllViews();
//
//                        ArrayList<ContentValues> categories = getChildCategories(row);
//
//                        if (categories != null && categories.size() > 0) {
//                            for (int i = 0; i < categories.size(); i++) {
//                                LayoutInflater inflater = LayoutInflater.from(mContext);
//                                final View childView = inflater.inflate(R.layout.virtuemart_sidemenu_list_child, null, false);
//                                final LinearLayout childViewSecondLevel = (LinearLayout) childView.findViewById(R.id.childView);
//                                final SmartTextView txtMenuItemCaption = (SmartTextView) childView.findViewById(R.id.txtMenuItemCaption);
//                                final CheckBox childCheckBoxTwo=(CheckBox) childView.findViewById(R.id.childCheckBoxTwo);
//                                final ContentValues value = categories.get(i);
//
//                                if(selectedItems.size()>0 && selectedItems.containsKey(value.getAsString("id"))){
//                                    childCheckBoxTwo.setChecked(true);
//                                }else{
//                                    childCheckBoxTwo.setChecked(false);
//                                }
//                                childCheckBoxTwo.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        if(childCheckBoxTwo.isChecked()){
//                                            selectedItems.put(value.getAsString("id"),value.getAsString("name"));
//                                        }else{
//                                            selectedItems.remove(value.getAsString("id"));
//                                        }
//
//                                        notifyDataSetChanged();
//                                    }
//                                });
//
////                                holder.childCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
////                                    @Override
////                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                                        if(isChecked){
////                                            values.add(value.getAsString("name"));
////                                            ids.add(value.getAsString("id"));
////                                            Log.v("@@@WS","Values Array "+values.toString());
////                                            Log.v("@@@WS","ID Array "+ids.toString());
////                                        }
////                                    }
////                                });
//
//                                txtMenuItemCaption.setText(value.getAsString(NAME).trim());
//                                txtMenuItemCaption.setTag(value);
//                                if (hasChildCategories(value)) {
//                                    childCheckBoxTwo.setVisibility(View.GONE);
//
//                                    txtMenuItemCaption.setId(R.id.collapsed);
//                                    txtMenuItemCaption.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_down_grey600_24dp, 0);
//                                } else {
//                                    childCheckBoxTwo.setVisibility(View.VISIBLE);
//
//                                    txtMenuItemCaption.setId(R.id.no_children);
//                                }
//                                txtMenuItemCaption.setOnClickListener(new View.OnClickListener() {
//
//                                    @Override
//                                    public void onClick(View view) {
//                                        if (view.getId() == R.id.collapsed) {
//                                            Log.v("@@DATATATA", "will come here");
//                                            view.setId(R.id.expanded);
//                                            ((SmartTextView) view).setTypeface(null, Typeface.BOLD);
//                                            ((SmartTextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_up_grey600_24dp, 0);
//
//                                            childViewSecondLevel.removeAllViews();
//                                            ArrayList<ContentValues> categories = getChildCategories((ContentValues) view.getTag());
//                                            if (categories != null && categories.size() > 0) {
//                                                for (int i = 0; i < categories.size(); i++) {
//                                                    LayoutInflater inflater = LayoutInflater.from(mContext);
//                                                    final View childView = inflater.inflate(R.layout.virtuemart_sidemenu_list_child, null, false);
//                                                    final SmartTextView txtMenuItemCaption = (SmartTextView) childView.findViewById(R.id.txtMenuItemCaption);
//                                                    final CheckBox childCheckBoxTwo=(CheckBox) childView.findViewById(R.id.childCheckBoxTwo);
//
//                                                    final ContentValues value = categories.get(i);
////                                                    holder.childCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
////                                                        @Override
////                                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                                                            if(isChecked){
////                                                                values.add(value.getAsString("name"));
////                                                                ids.add(value.getAsString("id"));
////                                                                Log.v("@@@WS","Values Array "+values.toString());
////                                                                Log.v("@@@WS","ID Array "+ids.toString());
////                                                            }
////                                                        }
////                                                    });
//
//                                                    if(selectedItems.size()>0 && selectedItems.containsKey(value.getAsString("id"))){
//                                                        childCheckBoxTwo.setChecked(true);
//                                                    }else{
//                                                        childCheckBoxTwo.setChecked(false);
//                                                    }
//                                                    childCheckBoxTwo.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View v) {
//                                                            if(childCheckBoxTwo.isChecked()){
//                                                                selectedItems.put(value.getAsString("id"),value.getAsString("name"));
//                                                            }else{
//                                                                selectedItems.remove(value.getAsString("id"));
//                                                            }
//
//                                                            notifyDataSetChanged();
//                                                        }
//                                                    });
//
//                                                    txtMenuItemCaption.setText(value.getAsString(NAME).trim());
//                                                    childCheckBoxTwo.setVisibility(View.VISIBLE);
//                                                    txtMenuItemCaption.setTag(value);
//                                                    txtMenuItemCaption.setOnClickListener(new View.OnClickListener() {
//
//                                                        @Override
//                                                        public void onClick(View v) {
//
//                                                            //this will open product list page
//                                                           // openProductListing((ContentValues) v.getTag());
//                                                        }
//                                                    });
//                                                    childViewSecondLevel.addView(childView);
//                                                }
//                                            }
//                                        } else if (view.getId() == R.id.expanded) {
//                                            view.setId(R.id.collapsed);
//                                            ((SmartTextView) view).setTypeface(null, Typeface.NORMAL);
//                                            ((SmartTextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_down_grey600_24dp, 0);
//                                            childViewSecondLevel.removeAllViews();
//                                        } else {
//
//                                            Log.v("@@DATATATA", "will coming wrong here here");
//                                            //this will open product list page
//                                           // openProductListing((ContentValues) view.getTag());
//                                        }
//                                    }
//                                });
//                                holder.childView.addView(childView);
//                            }
//                        }
//                    } else if (view.getId() == R.id.expanded) {
//                        view.setId(R.id.collapsed);
//                        ((SmartTextView) view).setTypeface(null, Typeface.NORMAL);
//                        ((SmartTextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_down_grey600_24dp, 0);
//                        holder.childView.removeAllViews();
//                    } else {
//                        //openProductListing((ContentValues) view.getTag());
//                    }
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return listDataSideMenu.size();
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            public SmartTextView txtMenuItemCaption;
//            public LinearLayout childView;
//            public CheckBox childCheckBox;
//
//
//
//            public ViewHolder(View itemView) {
//                super(itemView);
//                childCheckBox=(CheckBox) itemView.findViewById(R.id.childCheckBox);
//                txtMenuItemCaption = (SmartTextView) itemView.findViewById(R.id.txtMenuItemCaption);
//                childView = (LinearLayout) itemView.findViewById(R.id.childView);
//
//            }
//        }
//    }

    public static void getVMMultiSelectionFilterDialog(Context context, final String name, String jsonString,
                                                       final String selectedValues, final String selectedIDs, final MultipleSelectListener target) {

        final ArrayList<String> items = new ArrayList<String>();
        final ArrayList<String> values = new ArrayList<String>();
        final ArrayList<String> ids = new ArrayList<String>();
        final ArrayList<String> active = new ArrayList<String>();
        try {
            JSONArray options = new JSONArray(jsonString);
            for (int i = 0; i < options.length(); i++) {
                JSONObject option = options.getJSONObject(i);
                items.add(option.getString("text"));
                values.add(option.getString("value"));
                ids.add(option.getString("text"));

                if(option.has("child") && option.getJSONArray("child").length()>0){
                    active.add("0");
                }else{
                    active.add("1");
                }

                if (option.has("child")) {
                    JSONArray first_children = option.getJSONArray("child");
                    if (first_children != null && first_children.length() > 0) {
                        for (int j = 0; j < first_children.length(); j++) {
                            JSONObject first_children_option = first_children.getJSONObject(j);
                            items.add("-- " + first_children_option.getString("text"));
                            values.add(first_children_option.getString("value"));
                            ids.add(first_children_option.getString("text"));
                            if(first_children_option.has("child") && first_children_option.getJSONArray("child").length()>0){
                                active.add("0");
                            }else{
                                active.add("1");
                            }
                            if (first_children_option.has("child")) {
                                JSONArray second_children = first_children_option.getJSONArray("child");
                                if (second_children != null && second_children.length() > 0) {
                                    for (int k = 0; k < second_children.length(); k++) {
                                        JSONObject second_children_option = second_children.getJSONObject(k);
                                        items.add("--- " + second_children_option.getString("text"));
                                        values.add(second_children_option.getString("value"));
                                        ids.add(second_children_option.getString("text"));
                                        active.add(second_children_option.getJSONObject("extra_attrib").getString("active"));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            final boolean[] selections = new boolean[values.size()];
            final StringBuilder newValue = new StringBuilder();
            final StringBuilder newIds = new StringBuilder();

            AlertDialog alert = null;

            if (selectedValues.length() > 0) {
                String[] oldValue = selectedValues.split(",");
                for (int i = 0; i < values.size(); i++) {
                    int len = oldValue.length;
                    for (int j = 0; j < len; j++) {
                        if (values.get(i).toString().trim().equalsIgnoreCase(oldValue[j].trim())) {
                            selections[i] = true;
                            break;
                        }
                    }
                }
            }

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final DialogInterface.OnMultiChoiceClickListener listener = new DialogInterface.OnMultiChoiceClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if(active.get(which).equals("1")){
                        selections[which] = isChecked;
                    }else{
                        selections[which] = false;
                        ((AlertDialog) dialog).getListView().setItemChecked(which, false);
                    }




                }
            };


            builder.setTitle(name);
            builder.setMultiChoiceItems(items.toArray(new CharSequence[items.size()]), selections,listener);

            builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int size = selections.length;
                    for (int i = 0; i < size; i++) {
                        if (selections[i]) {
                            newValue.append(newValue.length() > 0 ? "," + values.get(i) : values.get(i));
                            newIds.append(newIds.length() > 0 ? "," + ids.get(i) : ids.get(i));
                        }
                    }
                    target.onClick(newIds.toString(),newValue.toString());

                }
            });
            builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    target.onClick(selectedIDs,selectedValues);
                }
            });
            alert = builder.create();
            alert.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getLatitude() {
        return latitude;
    }

    public static void setLatitude(String latitude) {
        SmartUtils.latitude = latitude;
    }

    private static String latitude = "0";

    public static String getLongitude() {
        return longitude;
    }

    public static void setLongitude(String longitude) {
        SmartUtils.longitude = longitude;
    }

    private static String longitude = "0";

    public static boolean isReloadRequired() {
        return isReloadRequired;
    }

    public static void setIsReloadRequired(boolean isReloadRequired) {
        SmartUtils.isReloadRequired = isReloadRequired;
    }

    private static boolean isReloadRequired = false;

    public static boolean isReloadCartRequired() {
        return isReloadCartRequired;
    }

    public static void setIsReloadCartRequired(boolean isReloadCartRequired) {
        SmartUtils.isReloadCartRequired = isReloadCartRequired;
    }

    private static boolean isReloadCartRequired = false;

    public static boolean isNetworkAvailable() {
        return isNetworkAvailable;
    }

    public static void setNetworkStateAvailability(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                        isNetworkAvailable = true;
                        return;
                    }
                }
            }
        }

        isNetworkAvailable = false;
    }


    // Validation

    /**
     * This method used to email validator.
     *
     * @param mailAddress represented email
     * @return represented {@link Boolean}
     */
    public static boolean emailValidator(final String mailAddress) {
        Pattern pattern;
        Matcher matcher;

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(mailAddress);
        return matcher.matches();
    }

    /**
     * This method used to birth date validator.
     *
     * @param birthDate represented birth date
     * @return represented {@link Boolean}
     */
    public static boolean birthdateValidator(String birthDate, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            Date date = dateFormat.parse(birthDate);
            Calendar bdate = Calendar.getInstance();
            bdate.setTime(date);
            Calendar today = Calendar.getInstance();

            if (bdate.compareTo(today) == 1) {
                return false;
            } else {
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }


    //Dates

    /**
     * This method used to get difference from minute.
     *
     * @param miliseconds represented {@link Long} milliseconds
     * @return represented {@link Long}
     */
    public static long getDfferenceInMinute(long miliseconds) {
        long diff = (Calendar.getInstance().getTimeInMillis() - miliseconds);
        diff = diff / 60000L;
        return Math.abs(diff);
    }

    /**
     * This method used to calculate times ago from milliseconds.
     *
     * @param miliseconds represented {@link Long} milliseconds
     * @return represented {@link String}
     */
    public static String calculateTimesAgo(long miliseconds, String format) {
        Date start = new Date(miliseconds);
        Date end = new Date();

        long diffInSeconds = (end.getTime() - start.getTime()) / 1000;

        long diff[] = new long[]{0, 0, 0, 0};
        /* sec */
        diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        /* min */
        diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        /* hours */
        diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        /* days */
        diff[0] = (diffInSeconds = (diffInSeconds / 24));

        System.out.println(String.format("%d day%s, %d hour%s, %d minute%s, %d second%s ago", diff[0], diff[0] > 1 ? "s" : "", diff[1],
                diff[1] > 1 ? "s" : "", diff[2], diff[2] > 1 ? "s" : "", diff[3], diff[3] > 1 ? "s" : ""));

        if (diff[0] > 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(start);

            if (c.getMaximum(Calendar.DATE) <= diff[0]) {
                return (String) DateFormat.format(format, start);
            } else {
                return diff[0] > 1 ? String.format("%d days ago", diff[0]) : String.format("%d day ago", diff[0]);
            }
        } else if (diff[1] > 0) {
            return diff[1] > 1 ? String.format("%d hours ago", diff[1]) : String.format("%d hour ago", diff[1]);
        } else if (diff[2] > 0) {
            return diff[2] > 1 ? String.format("%d minutes ago", diff[2]) : String.format("%d minute ago", diff[2]);
        } else if (diff[3] > 0) {
            return diff[3] > 1 ? String.format("%d seconds ago", diff[3]) : String.format("%d second ago", diff[3]);
        } else {
            return (String) DateFormat.format(format, start);
        }
    }

    /**
     * This method used to get milliseconds from time zone.
     *
     * @param timestamp represented {@link Long} time stamp
     * @return represented {@link Long}
     */
//    public static long getMillisecondsTimeZone(long timestamp) {
//        Calendar calendar = Calendar.getInstance();
//        TimeZone t = TimeZone.getTimeZone(JomConfigurations.getServerTimeZone());
//
//        calendar.setTimeInMillis(timestamp * 1000);
//        calendar.add(Calendar.MILLISECOND, t.getOffset(calendar.getTimeInMillis()));
//        System.out.println("Date : " + calendar.getTime());
//        return calendar.getTimeInMillis();
//    }

    /**
     * This method used to get date from string.
     *
     * @param strDate represented date
     * @return represented {@link Date}
     */
    public static Calendar getDateFromString(String strDate, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Calendar calender = Calendar.getInstance();
        Date date;
        try {
            date = dateFormat.parse(strDate);
            calender.setTime(date);
            return calender;
        } catch (Throwable e) {
            return Calendar.getInstance();
        }
    }


    public static String getStringFromCalendar(Calendar c, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(c.getTime());
    }

    /**
     * This method used to get time from string.
     *
     * @param strTime represented time
     * @return represented {@link Date}
     */
    public static Calendar getTimeFromString(String strTime, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date;
        Calendar calnder = Calendar.getInstance();
        try {
            date = dateFormat.parse(strTime);
            calnder.setTime(date);
            return calnder;
        } catch (Throwable e) {
            return Calendar.getInstance();
        }
    }

    /**
     * This method used to get date dialog.
     *
     * @param strDate  represented date
     * @param restrict represented isRestrict
     */
    public static void getDateDialog(Context context, final String strDate,
                                     boolean restrict, final CustomClickListener target, final String format) {
        Calendar date = getDateFromString(strDate, format);
        Calendar today = Calendar.getInstance();
        if (restrict && date.get(Calendar.YEAR) == today.get(Calendar.YEAR) && date.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && date.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
            date.add(Calendar.YEAR, -18);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SmartDatePickerView dateDlg = new SmartDatePickerView(context, android.R.style.Theme_Material_Light_Dialog_NoActionBar,
                    new DatePickerDialog.OnDateSetListener() {

                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Time chosenDate = new Time();
                            chosenDate.set(dayOfMonth, monthOfYear, year);
                            long dt = chosenDate.toMillis(true);
                            CharSequence strDate = DateFormat.format(format, dt);
                            target.onClick(strDate.toString());
                        }
                    }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), restrict);

            dateDlg.show();
        } else {
            SmartDatePickerView dateDlg = new SmartDatePickerView(context, new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Time chosenDate = new Time();
                    chosenDate.set(dayOfMonth, monthOfYear, year);
                    long dt = chosenDate.toMillis(true);
                    CharSequence strDate = DateFormat.format(format, dt);
                    target.onClick(strDate.toString());
                }
            }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), restrict);

            dateDlg.show();
        }
    }

    //this below method is used for cusotm time picker

    public static void getCustomTimePickerDialog(Context context, final String strTime, final CustomTimeDialogListener target, final String format) {

        Calendar date = getTimeFromString(strTime, format);
        CustomTimePickerDialog timeDialog = new CustomTimePickerDialog(context, new CustomTimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar date = Calendar.getInstance();
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                String dateString = (("" + hourOfDay).length() == 2 ? ("" + hourOfDay) : ("0" + hourOfDay)) + ":" + (("" + minute).length() == 2 ? ("" + minute) : ("0" + minute));
                target.onClick(dateString, date, hourOfDay, minute);
            }
        }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true);

        timeDialog.show();

    }

    /**
     * This method used to get date-time dialog.
     *
     * @param strDate represented date-time
     * @param target  represented {@link CustomClickListener}
     */
    public static void getDateTimeDialog(final Context context, final String strDate,
                                         final CustomClickListener target, final String format) {
        final Calendar date = getDateFromString(strDate, format);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DatePickerDialog dateDialog = new DatePickerDialog(context, android.R.style.Theme_Material_Light_Dialog_NoActionBar,
                    new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            final int y = year;
                            final int m = monthOfYear;
                            final int d = dayOfMonth;

                            SmartUtils.getCustomTimePickerDialog(context, strDate, new CustomTimeDialogListener() {
                                @Override
                                public void onClick(String value, Calendar date, int hourDay, int minutes) {
                                    Time chosenDate = new Time();
                                    chosenDate.set(0, minutes, hourDay, d, m, y);
                                    long dt = chosenDate.toMillis(true);
                                    CharSequence strDate = DateFormat.format(format, dt);
                                    target.onClick(strDate.toString());
                                }
                            }, format);

//                            new TimePickerDialog(context, android.R.style.Theme_Material_Light_Dialog_NoActionBar,
//                                    new TimePickerDialog.OnTimeSetListener() {
//
//                                        @Override
//                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                                            Toast.makeText(context,"Called from within time pic",Toast.LENGTH_LONG).show();
//                                            Time chosenDate = new Time();
//                                            chosenDate.set(0, minute, hourOfDay, d, m, y);
//                                            long dt = chosenDate.toMillis(true);
//                                            CharSequence strDate = DateFormat.format(format, dt);
//                                            target.onClick(strDate.toString());
//                                        }
//                                    }
//                                    ,date.get(Calendar.HOUR), date.get(Calendar.MINUTE), false).show();

                        }
                    }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
            dateDialog.show();
        } else {
            DatePickerDialog dateDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    final int y = year;
                    final int m = monthOfYear;
                    final int d = dayOfMonth;

                    if(timePickerDialog== null){
                        timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Time chosenDate = new Time();
                                chosenDate.set(0, minute, hourOfDay, d, m, y);
                                long dt = chosenDate.toMillis(true);
                                CharSequence strDate = DateFormat.format(format, dt);
                                timePickerDialog = null;
                                target.onClick(strDate.toString());
                            }
                        }, date.get(Calendar.HOUR), date.get(Calendar.MINUTE), false);

                        timePickerDialog.show();
                    }


                }
            }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
            dateDialog.show();
        }
    }

    /**
     * This method used to get time dialog.
     *
     * @param strTime represented time
     * @param target  represented {@link CustomClickListener}
     */
    public static void getTimeDialog(Context context, final String strTime, final CustomClickListener target, final String format) {

        Calendar date = getTimeFromString(strTime, format);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TimePickerDialog timeDialog = new TimePickerDialog(context, android.R.style.Theme_Material_Light_Dialog_NoActionBar,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            Calendar date = Calendar.getInstance();
                            date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            date.set(Calendar.MINUTE, minute);
                            String dateString = new SimpleDateFormat(format).format(date);
                            target.onClick(dateString);
                        }
                    }, date.get(Calendar.HOUR), date.get(Calendar.MINUTE), true);

            timeDialog.show();
        } else {
            TimePickerDialog timeDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Calendar date = Calendar.getInstance();
                    date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    date.set(Calendar.MINUTE, minute);
                    String dateString = new SimpleDateFormat(format).format(date);
                    target.onClick(dateString);
                }
            }, date.get(Calendar.HOUR), date.get(Calendar.MINUTE), true);

            timeDialog.show();
        }
    }

    /**
     * This method used to set default avatar
     *
     * @return defaultAvatar represented default avatar
     */
    public static void setDefaultAvatar(String defaultAvatar) {
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_DEFAULTAVATAR, defaultAvatar);
    }

    /**
     * This method used to get theme.
     *
     * @return represented {@link String}
     */
    public static String getDefaultAvatar() {
        return SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_DEFAULTAVATAR, "");
    }


    // Dialogs

    /**
     * This method used to show select image selection dialog.
     *
     * @param target represented {@link SelectImageDialogListner}
     */
    public static void selectImageDialog(Context context, final SelectImageDialogListner target) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        final SmartTextView txtCapture = (SmartTextView) dialog.findViewById(R.id.txtCapture);
        final SmartTextView txtPhoneGallery = (SmartTextView) dialog.findViewById(R.id.txtPhoneGallery);
        txtCapture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View paramView) {
                target.onCapture();
                dialog.dismiss();

            }
        });
        txtPhoneGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View paramView) {
                target.onPhoneGallery();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showLoadingDialog(final Context context) {
        hideLoadingDialog();

        loadingDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.show();
    }

    public static void hideLoadingDialog() {
        try {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
                loadingDialog = null;
            }
        }catch (Exception ed){
            ed.printStackTrace();
        }
    }

    /**
     * This method will show the progress dialog with given message in the given
     * activity's context.<br>
     * The progress dialog can be set cancellable by passing appropriate flag in
     * parameter. User can dismiss the current progress dialog by pressing back
     * SmartButton if the flag is set to <b>true</b>; This method can also be
     * called from non UI threads.
     *
     * @param context = Context context will be current activity's context.
     *                <b>Note</b> : A new progress dialog will be generated on
     *                screen each time this method is called.
     */
    public static void showProgressDialog(final Context context, String msg, final boolean isCancellable) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        }
        progressDialog = ProgressDialog.show(context, "", "");

        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(isCancellable);
        progressDialog.setCanceledOnTouchOutside(false);
        ((ProgressBar) progressDialog.findViewById(R.id.progressBar)).getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_ATOP);
        ((SmartTextView) progressDialog.findViewById(R.id.txtMessage)).setText(msg == null || msg.trim().length() <= 0 ? context.getString(R.string.dialog_loading_please_wait) : msg);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * This method will hide existing progress dialog.<br>
     * It will not throw any Exception if there is no progress dialog on the
     * screen and can also be called from non UI threads.
     */
    static public void hideProgressDialog() {
        try {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = null;
        } catch (Throwable e) {
            progressDialog = null;
        }
    }

    public static ProgressDialog getProgressDialog() {
        return progressDialog;
    }


    /**
     * This method will generate and show the Ok dialog with given message and
     * single message SmartButton.<br>
     *
     * @param title  = String title will be the title of OK dialog.
     * @param msg    = String msg will be the message in OK dialog.
     * @param target = String target is AlertNewtral callback for OK SmartButton
     *               click action.
     */
    static public void getConfirmDialog(Context context, String title, String msg, String positiveBtnCaption,
                                        String negativeBtnCaption, boolean isCancelable, final AlertMagnatic target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title).setMessage(msg).setCancelable(false)
                .setPositiveButton(positiveBtnCaption, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        target.PositiveMethod(dialog, id);
                    }
                })
                .setNegativeButton(negativeBtnCaption, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        target.NegativeMethod(dialog, id);
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setCancelable(isCancelable);
        alert.show();
    }

    /**
     * This method will generate and show the Ok dialog with given message and
     * single message SmartButton.<br>
     *
     * @param title         = String title will be the title of OK dialog.
     * @param msg           = String msg will be the message in OK dialog.
     * @param buttonCaption = String SmartButtonCaption will be the name of OK
     *                      SmartButton.
     * @param target        = String target is AlertNewtral callback for OK SmartButton
     *                      click action.
     */
    static public void getOKDialog(Context context, String title, String msg, String buttonCaption,
                                   boolean isCancelable, final AlertNeutral target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(msg)
                .setCancelable(false)
                .setNeutralButton(buttonCaption, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        target.NeutralMathod(dialog, id);
                    }
                });

        AlertDialog alert = builder.create();
        alert.setCancelable(isCancelable);
        alert.show();
    }

//    static public void reportDialog(Context context, boolean isCancelable, final ReportListener target) {
//        View reportView = LayoutInflater.from(context).inflate(R.layout.report_dialog, null);
//        final SmartSpinner spnReportType = (SmartSpinner) reportView.findViewById(R.id.spnReportType);
//        final SmartEditText edtReportMessage = (SmartEditText) reportView.findViewById(R.id.edtReportMessage);
//
//        if (JomConfigurations.getReportType() != null && JomConfigurations.getReportType().size() > 0) {
//            spnReportType.setAdapter(new MyCustomAdapter(context, JomConfigurations.getReportType()));
//        }
//
//        spnReportType.setAdapter(new MyCustomAdapter(context, JomConfigurations.getReportType()));
//
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
//                .setTitle(context.getString(R.string.dialog_title_report))
//                .setCancelable(false)
//                .setPositiveButton(context.getString(R.string.send), new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, final int id) {
//                        target.onClick(spnReportType.getSelectedItem().toString(), edtReportMessage.getText().toString().trim());
//                        dialog.cancel();
//                    }
//                })
//                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, final int id) {
//                        dialog.cancel();
//                    }
//                });
//        builder.setView(reportView);
//        final AlertDialog alert = builder.create();
//        alert.setCancelable(isCancelable);
//        alert.show();
//    }

    /**
     * This method used to get announcement or discussion create dialog.
     *
     * @param dialogTitle represented dialog title
     * @param description represented description
     * @param target      represented {@link PhotoDescriptionListener}
     * @return represented {@link Dialog}
     */
//    static public AlertDialog addEditPhotoDescription(final Context context, final String dialogTitle,
//                                                      final String description, final PhotoDescriptionListener target) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
//                .setTitle(dialogTitle)
//                .setCancelable(false)
//                .setPositiveButton(R.string.save, null)
//                .setNegativeButton(R.string.cancel, null);
//
//        View photoDescriptionDialog = LayoutInflater.from(context).inflate(R.layout.jom_photo_description_dialog, null);
//
//        final TextInputLayout inputPhotoDescription = (TextInputLayout) photoDescriptionDialog.findViewById(R.id.inputPhotoDescription);
//        final SmartEditText edtPhotoDescription = (SmartEditText) photoDescriptionDialog.findViewById(R.id.edtPhotoDescription);
//        edtPhotoDescription.setText(description);
//        builder.setView(photoDescriptionDialog);
//        final AlertDialog alert = builder.create();
//        alert.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                Button btnPositive = alert.getButton(AlertDialog.BUTTON_POSITIVE);
//                btnPositive.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View view) {
//                        hideSoftKeyboard(context);
//
//                        alert.dismiss();
//
//                        target.onClick(edtPhotoDescription.getText().toString());
//                    }
//                });
//
//                Button btnNagative = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
//                btnNagative.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View view) {
//                        hideSoftKeyboard(context);
//
//                        alert.dismiss();
//                    }
//                });
//            }
//        });
//        alert.show();
//
//        return alert;
//    }


    /**
     * This method used to get announcement or discussion create dialog.
     *
     * @param dialogTitle  represented dialog title
     * @param title        represented announcement or discussion title
     * @param message      represented announcement or discussion message
     * @param isUploadFile represented announcement or discussion upload file permission
     * @param target       represented {@link AnnouncementAndDiscussionListener}
     * @return represented {@link Dialog}
     */
//    static public AlertDialog getAnnouncementOrDiscussionCreateDialog(final Context context, final String dialogTitle, final String title,
//                                                                      final String message, final String isUploadFile,
//                                                                      final AnnouncementAndDiscussionListener target) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
//                .setTitle(dialogTitle)
//                .setCancelable(false)
//                .setPositiveButton(R.string.save, null)
//                .setNegativeButton(R.string.cancel, null);
//        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        View announcementOrDiscussionView = LayoutInflater.from(context).inflate(R.layout.jom_group_discussion_announcement_dialog, null);
//
//        final SmartEditText edtDiscussionAnnouncementTitle = (SmartEditText) announcementOrDiscussionView.findViewById(R.id.edtDiscussionAnnouncementTitle);
//        final SmartEditText edtDiscussionAnnouncementMessage = (SmartEditText) announcementOrDiscussionView.findViewById(R.id.edtDiscussionAnnouncementMessage);
//        final SmartCheckBox chbDiscussionAnnouncementAllowMemberUploadFile = (SmartCheckBox) announcementOrDiscussionView.findViewById(R.id.chbDiscussionAnnouncementAllowMemberUploadFile);
//        if(isUploadFile.trim().length() > 0&&isUploadFile.trim().toString().equalsIgnoreCase("1")){
//            chbDiscussionAnnouncementAllowMemberUploadFile.setVisibility(View.VISIBLE);
//        }else{
//            chbDiscussionAnnouncementAllowMemberUploadFile.setVisibility(View.GONE);
//        }
//        if (title.trim().length() > 0 && message.trim().length() > 0 && isUploadFile.trim().length() > 0) {
//            edtDiscussionAnnouncementTitle.setText(title);
//            edtDiscussionAnnouncementMessage.setText(message);
//            chbDiscussionAnnouncementAllowMemberUploadFile.setChecked(isUploadFile.equals("1") ? true : false);
//        }
//        builder.setView(announcementOrDiscussionView);
//
//        final AlertDialog alert = builder.create();
//        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        alert.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                Button btnPositive = alert.getButton(AlertDialog.BUTTON_POSITIVE);
//                btnPositive.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View view) {
//                        hideSoftKeyboard(context);
//                        boolean validationFlag = true;
//                        if (edtDiscussionAnnouncementTitle.getText().toString().trim().length() <= 0) {
//                            validationFlag = false;
//                            edtDiscussionAnnouncementTitle.setError(context.getString(R.string.validation_value_required));
//
//                        }
//                        if (edtDiscussionAnnouncementMessage.getText().toString().trim().length() <= 0) {
//                            validationFlag = false;
//                            edtDiscussionAnnouncementMessage.setError(context.getString(R.string.validation_value_required));
//                        }
//
//                        if (validationFlag) {
//                            hideSoftKeyboard(context);
//
//                            alert.dismiss();
//
//                            target.onClick(edtDiscussionAnnouncementTitle.getText().toString(), edtDiscussionAnnouncementMessage.getText().toString(),
//                                    chbDiscussionAnnouncementAllowMemberUploadFile.isChecked() ? "1" : "0");
//                        }
//                    }
//                });
//
//                Button btnNagative = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
//                btnNagative.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View view) {
//                        hideSoftKeyboard(context);
//
//                        alert.dismiss();
//                    }
//                });
//            }
//        });
//        alert.show();
//
//        return alert;
//    }

    /**
     * This method will show short length Toast message with given string.
     *
     * @param msg = String msg to be shown in Toast message.
     */
    static public void ting(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * This method will show long length Toast message with given string.
     *
     * @param msg = String msg to be shown in Toast message.
     */
    static public void tong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    // Audio, Image and Video

    /**
     * This method used to decode file from string path.
     *
     * @param path represented path
     * @return represented {@link Bitmap}
     */
    static public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * This method used to decode file from uri path.
     *
     * @param path represented path
     * @return represented {@link Bitmap}
     */
    static public Bitmap decodeFile(Context context, Uri path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(getAbsolutePath(context, path), o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(getAbsolutePath(context, path), o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    static public String getYoutubeId(String videoUrl) {
        String video_id = "";
        if (videoUrl != null && videoUrl.trim().length() > 0) {
            String s = "^.*(?:youtu.be\\/|v\\/|e\\/|u\\/\\w+\\/|embed\\/|v=)([^#\\&\\?]*).*";
            CharSequence input = videoUrl;
            Pattern pattern = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                System.out.println("DATA" + matcher.group(1));
                String groupIndex1 = matcher.group(1);
                if (groupIndex1 != null && groupIndex1.length() == 11)
                    video_id = groupIndex1;
            }
        }
        System.out.println("VIDEOID" + video_id);
        if (video_id.trim().length() > 0) {
            return video_id;
        } else {
            return "";
        }
    }

    //General Methods

    /**
     * This method will write any text string to the log file generated by the
     * SmartFramework.
     *
     * @param text = String text is the text which is to be written to the log
     *             file.
     */
    static public void appendLog(String text) {
        File logFile = new File("sdcard/" + SmartApplication.REF_SMART_APPLICATION.LOGFILENAME);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            Calendar calendar = Calendar.getInstance();
            try {
                System.err.println("Logged Date-Time : " + ((String) DateFormat.format("yyyy-MM-dd kk:mm:ss", calendar)));
            } catch (Throwable e) {
            }
            buf.append("Logged Date-Time : " + ((String) DateFormat.format("yyyy-MM-dd kk:mm:ss", calendar)));
            buf.append("\n\n");
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method will return android device UDID.
     *
     * @return DeviceID = String DeviceId will be the Unique Id of android
     * device.
     */
    static public String getDeviceUDID(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }


    public static String getB64Auth(String userName, String password) {
        String source = userName + ":" + password;
        String ret = "Basic " + Base64.encodeToString(source.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
        return ret;
    }

    /**
     * This method used to set image uri.
     *
     * @return represented {@link Uri}
     */
    public static Uri setImageUri() {
        // Store image in dcim
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "image" + new Date().getTime() + ".png");
        Uri imgUri = Uri.fromFile(file);
        imgPath = file.getAbsolutePath();
        return imgUri;
    }

    /**
     * This method used to get Image path.
     *
     * @return
     */
    public static String getImagePath() {
        return imgPath;
    }

    /**
     * This method used to get absolute path from uri.
     *
     * @param uri represented uri
     * @return represented {@link String}
     */
    public static String getAbsolutePath(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT < 11)
            return RealPathUtil.getRealPathFromURI_BelowAPI11(context, uri);

            // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19)
            return RealPathUtil.getRealPathFromURI_API11to18(context, uri);

            // SDK > 19 (Android 4.4)
        else
            return RealPathUtil.getRealPathFromURI_API19(context, uri);
    }

    static public Uri getUri() {
        String state = Environment.getExternalStorageState();
        if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    /**
     * This method used to hide soft keyboard.
     */
    static public void hideSoftKeyboard(Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    /**
     * This method used to show soft keyboard.
     */
    static public void showSoftKeyboard(Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception e) {
        }
    }

    /**
     * This method used to do ellipsize to textview.
     *
     * @param tv      represented TextView do ellipsize
     * @param maxLine represented max line to show
     */
    static public void doEllipsize(final SmartTextView tv, final int maxLine) {
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine <= 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - 3) + "...";
                    tv.setText(text);
                } else if (tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - 3) + "...";
                    tv.setText(text);
                }
            }
        });
    }

    /**
     * This method used to convert json to map.
     *
     * @param object represented json object
     * @return represented {@link Map <String, String>}
     * @throws JSONException represented {@link JSONException}
     */
    static public Map<String, String> jsonToMap(JSONObject object) throws JSONException {
        Map<String, String> map = new HashMap();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)).toString());
        }
        return map;
    }

    /**
     * This method used to convert json to Object.
     *
     * @param json represented json object
     * @return represented {@link Object}
     * @throws JSONException represented {@link JSONException}
     */
    static public Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            return jsonToMap((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        } else {
            return json;
        }
    }

    /**
     * This method used to convert json array to List.
     *
     * @param array represented json array
     * @return represented {@link List}
     * @throws JSONException represented {@link JSONException}
     */
    static public List toList(JSONArray array) throws JSONException {
        List list = new ArrayList();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    /**
     * This method used to string array from string with (,) separated.
     *
     * @param value represented value
     * @return represented {@link String} array
     */
    static public String[] getStringArray(final String value) {
        try {
            if (value.length() > 0) {
                final JSONArray temp = new JSONArray(value);
                int length = temp.length();
                if (length > 0) {
                    final String[] recipients = new String[length];
                    for (int i = 0; i < length; i++) {
                        recipients[i] = temp.getString(i).equalsIgnoreCase("null") ? "1" : temp.getString(i);
                    }
                    return recipients;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * This method used to string array from arraylist.
     *
     * @param value represented value
     * @return represented {@link String} array
     */
    static public String[] getStringArray(final ArrayList<String> value) {
        try {
            String[] array = new String[value.size()];
            for (int i = 0; i < value.size(); i++) {
                array[i] = value.get(i);
            }
            return array;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static public void exportDatabse(Context context, String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + context.getPackageName() + "//databases//" + databaseName + "";
                String backupDBPath = "backupname.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
        }
    }


    /**
     * This method used to add clickable part on {@link TextView}.
     *
     * @param strSpanned represented {@link Spanned} string
     * @param tv         represented {@link TextView}
     * @param maxLine    represented max line
     * @param expandText represented expand text
     * @return represented {@link SpannableStringBuilder}
     */
    public static SpannableStringBuilder addClickablePartSmartTextViewResizable(Context context, final Spanned strSpanned, final SmartTextView tv,
                                                                                final int maxLine, final String expandText) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(expandText)) {
            ssb.setSpan(new SmartSpannable(context.getResources().getColor(R.color.yellow), true) {

                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                }

            }, str.indexOf(expandText), str.indexOf(expandText) + expandText.length(), 0);

        }
        return ssb;

    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_FLOOR);
        return bd.floatValue();
    }

    static public void setAuthPermission() {

        AQuery.setAuthHeader(SmartUtils.getB64Auth(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_HTTP_ACCESSS_USERNAME, ""),
                SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_HTTP_ACCESSS_PASSWORD, "")));
    }

    static public String removeSpecialCharacter(String string) {

        return string.replaceAll("[ ,]", "_");
    }

    static public boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    static public String createExternalDirectory(String directoryName) {

        if (SmartUtils.isExternalStorageAvailable()) {

            File file = new File(Environment.getExternalStorageDirectory(), directoryName);
            if (!file.mkdirs()) {
                Log.e(TAG, "Directory may exist");
            }
            return file.getAbsolutePath();
        } else {

            Log.e(TAG, "External storage is not available");
        }
        return null;
    }

    static public void clearActivityStack(Activity currentActivity, Intent intent) {
        ComponentName cn = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
        currentActivity.startActivity(mainIntent);
    }

    static public int convertSizeToDeviceDependent(Context context, int value) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return ((dm.densityDpi * value) / 160);
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    static public void showSnackBar(Context context, String message, int length) {
        Snackbar snackbar = Snackbar.make(((SmartActivity) context).getSnackBarContainer(), message, length);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(context.getResources().getColor(R.color.accent));
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextSize(15);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
        ((SmartActivity) context).setSnackbar(snackbar);
    }

    static public void hideSnackBar(Context context) {
        if (((SmartActivity) context).getSnackbar() != null) {
            ((SmartActivity) context).getSnackbar().dismiss();
        }
    }

    public static boolean isOSPreLollipop() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    public static String format(String string, String inputFormat, String outputFormat) {
        SimpleDateFormat inputTimeFormat = new SimpleDateFormat(inputFormat);
        SimpleDateFormat outputTimeFormat = new SimpleDateFormat(outputFormat);
        try {
            Log.v("@@@@DATATATA", inputTimeFormat.parse(string).toString());
            return outputTimeFormat.format(inputTimeFormat.parse(string));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isEqualDates(String start, String end, String inputFormat, String outputFormat) {
        SimpleDateFormat inputTimeFormat = new SimpleDateFormat(inputFormat);
        SimpleDateFormat outputTimeFormat = new SimpleDateFormat(outputFormat);
        try {
            Date sDate = inputTimeFormat.parse(start);
            String startDate = outputTimeFormat.format(sDate);
            Log.v("@@@@STARTDATE::", startDate.toString());

            Date eDate = inputTimeFormat.parse(end);
            String endDate = outputTimeFormat.format(eDate);
            Log.v("@@@@ENDDATE::", endDate.toString());

            if (endDate.equals(startDate)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Date format(String string, String inputFormat) {
        SimpleDateFormat inputTimeFormat = new SimpleDateFormat(inputFormat);
        try {
            return inputTimeFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isTimePassed(String string, String hours24) {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR);
        int minute = now.get(Calendar.MINUTE);

        SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm");
        String str = inputParser.format(now.getTime());
        Date currentDate = parseDate(str, hours24);
        Date inputDate = parseDate(string, hours24);

        return currentDate.after(inputDate);
    }

    private static Date parseDate(String date, String hours24) {
        SimpleDateFormat inputParser = new SimpleDateFormat(hours24);
        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    public static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

    public static void stopLocationUpdates(com.google.android.gms.location.LocationListener locationListener, GoogleApiClient googleApiClient) {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
    }

    static public void removeCookie() {
        SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().edit().remove(Constants.SP_COOKIES);
        SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().edit().commit();
    }

    static public String validateResponse(Context context, JSONObject response, String errorMessage) {
        if (response.has("php_server_error")) {
            try {
                System.out.println("WSPHP SERVER_WARNINGS/ERRORS" + response.getString("php_server_error"));
                response.remove("php_server_error");
            } catch (Exception e) {
            }
        }

        if (response.has("code")) {
            try {
                if (response.has("message") && response.getString("message").length() > 0) {
                    errorMessage = response.getString("message");
                } else {
                    try {
                        int code = Integer.parseInt(response.getString("code"));
                        errorMessage = context.getString(context.getResources().getIdentifier("code" + code, "string", context.getPackageName()));
                    } catch (Exception e) {
                    }
                }
            } catch (Throwable e) {
            }
        } else {
            errorMessage = "Invalid Response";
        }

        if (response.has("notification")) {
            try {
                JSONObject obj = response.getJSONObject("notification");
                if (obj.has("friendrequest")) {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_FRIEND_NOTIFICATION, obj.getString("friendrequest"));
                } else {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_FRIEND_NOTIFICATION, "0");
                }
                if (obj.has("inbox")) {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_MESSAGE_NOTIFICATION, obj.getString("inbox"));
                } else {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_MESSAGE_NOTIFICATION, "0");
                }
                if (obj.has("general")) {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_GLOBAL_NOTIFICATION, obj.getString("general"));
                } else {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_GLOBAL_NOTIFICATION, "0");
                }
            } catch (Exception e) {
            }
        }

        removeUnnacessaryFields(response);

        try{
//            ((JomHomeActivity)context).updateTabLayout();
        }catch (Throwable e){
            e.printStackTrace();
        }

        return errorMessage;
    }

    static public int getResponseCode(JSONObject response) {
        if (response.has("code")) {
            try {
                int code = Integer.parseInt(response.getString("code"));
                return code;
            } catch (Throwable e) {
                e.printStackTrace();
                return 108;
            }
        }
        return 108;
    }

    static public boolean isSessionExpire(JSONObject response) {
        if (response != null && response.has("code")) {
            try {
                if (response.getInt("code") == 704) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    static private void removeUnnacessaryFields(JSONObject data) {
        data.remove("code");
        data.remove("full");
        data.remove("notification");
        data.remove("pushNotificationData");
        data.remove("timeStamp");
        data.remove("unreadMessageCount");
    }

    /**
     * This method used to auto login user params.
     *
     * @return represented {@link JSONObject}
     */
    static public JSONObject getLoginParams() {
        JSONObject loginParams = null;
        try {
            loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                    .getString(SP_LOGIN_REQ_OBJECT, ""));
            JSONObject taskData = loginParams.getJSONObject("taskData");
            taskData.put("lat", getLatitude());
            taskData.put("long", getLongitude());
            String udid = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_GCM_REGID, "");
            if (udid.length() > 0) {
                taskData.put("devicetoken", udid);
            }
        } catch (Exception e) {
        }
        return loginParams;
    }


    static public String getCountryPhoneCode(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getSimCountryIso() != null && tm.getSimCountryIso().length() > 0) {
            return Iso2Phone.getPhone(tm.getSimCountryIso());
        } else {
            return Iso2Phone.getPhone(tm.getNetworkCountryIso());
        }
    }

    public static boolean isPhoneValid(String input) {
        return input.length() != 10 ? false : android.util.Patterns.PHONE.matcher(input).matches();
    }

    /**
     * This method used to get option spinner adapter for virtuemart.
     *
     * @param field represented {@link HashMap} data
     * @return represented {@link MyCustomAdapter}
     */
    public static MyCustomAdapter getVirtueMartSpinnerAdapter(Context context, ContentValues data,
                                                              ArrayList<ContentValues> field) {
        int index = 0;
        final ArrayList<String> values = new ArrayList<String>();
        for (int i = 0; i < field.size(); i++) {
            if (field.get(i).containsKey("text")) {
                values.add(field.get(i).getAsString("text"));
            }

            try {
                if (data.containsKey(VALUE) && !TextUtils.isEmpty(data.getAsString(VALUE))) {
                    JSONObject value = new JSONObject(data.getAsString(VALUE));
                    if (value.getString(VALUE).equalsIgnoreCase(field.get(i).getAsString(VALUE))) {
                        index = i;
                        Log.v("@@@SELECTED INDEX", "" + index);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        MyCustomAdapter adapter = new MyCustomAdapter(context, values);
        adapter.setDefaultPosition(index);
        return adapter;
    }

    /**
     * This method used to get option spinner adapter for virtuemart.
     *
     * @param field represented {@link HashMap} data
     * @return represented {@link MyCustomAdapter}
     */
    public static MyCustomAdapter getVirtueMartPreFilledSpinnerAdapter(Context context, JSONArray data, ArrayList<ContentValues> field) {
        int index = 0;
        final ArrayList<String> values = new ArrayList<String>();
        for (int i = 0; i < field.size(); i++) {
            if (field.get(i).containsKey("text")) {
                values.add(field.get(i).getAsString("text"));
            }

            try {
                JSONObject preFilledValue = data.getJSONObject(0);
                if (preFilledValue.getString(VALUE).equalsIgnoreCase(field.get(i).getAsString(VALUE))) {
                    index = i;
                    Log.v("@@@SELECTED INDEX", "" + index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        MyCustomAdapter adapter = new MyCustomAdapter(context, values);
        adapter.setDefaultPosition(index);
        return adapter;
    }

    /**
     * This method used to get option spinner adapter for virtuemart.
     *
     * @return represented {@link MyCustomAdapter}
     */
    public static MyCustomAdapter getVirtueMartSpinnerAdapter(Context context, JSONObject data) {
        int index = 0;
        final ArrayList<String> values = new ArrayList<String>();
        try {
            JSONArray jsonArray = data.getJSONArray(OPTIONS);
            int size = jsonArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject option = jsonArray.getJSONObject(i);
                values.add(option.getString(TEXT));

                if (option.getString(VALUE).equalsIgnoreCase
                        (data.getString(SELECTED))) {
                    index = i;
                    Log.v("@@@SELECTED INDEX", "" + index);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        MyCustomAdapter adapter = new MyCustomAdapter(context, values);
        adapter.setDefaultPosition(index);
        return adapter;
    }

    /**
     * This method used to get option spinner adapter for virtuemart.
     *
     * @return represented {@link MyCustomAdapter}
     */
    public static MyCustomAdapter getVirtueMartSpinnerAdapter(Context context, ContentValues data) {
        int index = 0;
        final ArrayList<String> values = new ArrayList<String>();
        try {
            JSONArray jsonArray = new JSONArray(data.getAsString(OPTIONS));
            int size = jsonArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject options = (JSONObject) jsonArray.get(i);

                if (options.has("text")) {
                    values.add(options.getString("text"));
                }

                try {
                    if (data.containsKey(VALUE) && !TextUtils.isEmpty(data.getAsString(VALUE))) {
                        JSONObject value = new JSONObject(data.getAsString(VALUE));
                        if (value.getString(VALUE).equalsIgnoreCase(options.getString(VALUE))) {
                            index = i;
                            Log.v("@@@SELECTED INDEX", "" + index);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        MyCustomAdapter adapter = new MyCustomAdapter(context, values);
        adapter.setDefaultPosition(index);
        return adapter;
    }

    /**
     * This method used to get option spinner adapter for virtuemart.
     *
     * @return represented {@link MyCustomAdapter}
     */
    public static MyCustomAdapter getVirtueMartPrefilledSpinnerAdapter(Context context, JSONArray data, ContentValues field) {
        int index = 0;
        final ArrayList<String> values = new ArrayList<String>();
        try {
            JSONArray jsonArray = new JSONArray(field.getAsString(OPTIONS));
            int size = jsonArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject options = (JSONObject) jsonArray.get(i);

                if (options.has("text")) {
                    values.add(options.getString("text"));
                }

                try {
                    JSONObject value = data.getJSONObject(0);
                    if (value.getString(VALUE).equalsIgnoreCase(options.getString(VALUE))) {
                        index = i;
                        Log.v("@@@SELECTED INDEX", "" + index);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        MyCustomAdapter adapter = new MyCustomAdapter(context, values);
        adapter.setDefaultPosition(index);
        return adapter;
    }

    public static float parseFloat(String value) {
        float result = (float) 0.00;
        if (value != null && value.length() > 0) {
            try {
                result = round(Float.parseFloat(value), 2);
            } catch (NumberFormatException e) {
                return (float) 0.00;
            }
        }
        return result;
    }

    /**
     * This method used to get latitude-longitude from address.
     *
     * @param address represented address
     * @return represented {@link Address}
     */
    public static Address getLatLongFromAddress(Context context, String address) {
        if (address != null && address.length() > 0) {
            geocoder = new Geocoder(context);

            List<Address> list = null;
            try {
                list = geocoder.getFromLocationName(address, 1);
                return list.get(0);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * This method used to get address list from latitude-longitude
     *
     * @param lat represented latitude (0-for current latitude)
     * @param lng represented longitude (0-for current longitude)
     * @return represented {@link Address}
     */
    public static Address getAddressFromLatLong(Context context, double lat, double lng) {
        if (lat == 0 || lng == 0) {
            lat = Double.parseDouble(getLatitude());
            lng = Double.parseDouble(getLongitude());
        }
        geocoder = new Geocoder(context);

        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(lat, lng, 10);
            return list.get(0);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method used to get address list from latitude-longitude
     *
     * @param lat represented latitude (0-for current latitude)
     * @param lng represented longitude (0-for current longitude)
     * @return represented {@link Address} list
     */
    public static List<Address> getAddressListFromLatLong(Context context, double lat, double lng) {
        if (lat == 0 || lng == 0) {
            lat = Double.parseDouble(getLatitude());
            lng = Double.parseDouble(getLongitude());
        }
        geocoder = new Geocoder(context);

        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(lat, lng, 10);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * This method used to get option spinner adapter.
     *
     * @param field represented {@link HashMap} data
     * @return represented {@link MyCustomAdapter}
     */
    public static MyCustomAdapter getSpinnerAdapter(Context context, ContentValues field) {
        final int[] index={0};
        final ArrayList<String> values = new ArrayList<String>();
        try {
            JSONArray jsonArray = new JSONArray(field.getAsString("options"));
            int size = jsonArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject options = (JSONObject) jsonArray.get(i);

                if (options.has("title")) {
                    values.add(options.getString("title"));
                } else if (options.has("name")) {
                    values.add(options.getString("name").replace("&rsaquo;", " > "));
                } else if (options.has("caption")) {
                    values.add(options.getString("caption"));
                } else if (options.has("country_name")) {
                    values.add(options.getString("country_name"));
                } else if (options.has("state_name")) {
                    values.add(options.getString("state_name"));
                } else if (options.has("text")) {
                    values.add(options.getString("text"));
                } else {
                    values.add(options.getString("value"));
                }

                if (options.has("value") && options.getString("value").equals(field.getAsString("value"))) {
                    index[0]= i;
                } else if (options.has("title") && options.getString("title").equals(field.getAsString("value"))) {
                    index[0]= i;
                } else if (options.has("name") && options.getString("name").equals(field.getAsString("value"))) {
                    index[0]= i;
                } else if (options.has("country_name") && options.getString("country_name").equals(field.getAsString("value"))) {
                    index[0]= i;
                } else if (options.has("state_name") && options.getString("state_name").equals(field.getAsString("value"))) {
                    index[0]= i;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        final MyCustomAdapter adapter = new MyCustomAdapter(context, values);
        adapter.setDefaultPosition(index[0]);

        return adapter;
    }

    /**
     * This method used to get option spinner adapter.
     *
     * @param options represented {@link JSONArray} data
     * @return represented {@link MyCustomAdapter}
     */
    public static MyCustomAdapter getSpinnerAdapter(Context context, JSONArray options) {
        final ArrayList<String> values = new ArrayList<String>();
        try {
            int size = options.length();
            for (int i = 0; i < size; i++) {
                JSONObject option = options.getJSONObject(i);

                if (option.has("title")) {
                    values.add(option.getString("title"));
                } else if (option.has("name")) {
                    values.add(option.getString("name").replace("&rsaquo;", " > "));
                } else if (option.has("caption")) {
                    values.add(option.getString("caption"));
                } else if (option.has("text")) {
                    values.add(option.getString("text"));
                } else {
                    values.add(option.getString("value"));
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        final MyCustomAdapter adapter = new MyCustomAdapter(context, values);
        adapter.setDefaultPosition(0);
        return adapter;
    }

    /**
     * This method used to get my albums list.
     *
     * @param fields represented {@link JSONArray} data
     * @return represented {@link ArrayList < String >}
     */
    public static ArrayList<String> getMyAlbumsList(JSONArray fields) {
        ArrayList<String> values = new ArrayList<String>();
        try {
            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.getJSONObject(i);
                values.add(field.getString("name"));
            }
            return values;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Custom adapter
     *
     * @author tasol
     */
    public static class MyCustomAdapter extends ArrayAdapter<String> {

        Context context;
        ArrayList<String> list;
        private int defaultPosition;
        private int layout = 0;

        public int getDefaultPosition() {
            return defaultPosition;
        }

        public MyCustomAdapter(Context context, ArrayList<String> objects) {
            super(context, 0, objects);
            this.context = context;
            list = objects;
        }

        public MyCustomAdapter(Context context, ArrayList<String> objects, int layout) {
            super(context, 0, objects);
            this.context = context;
            list = objects;
            this.layout = layout;
        }

        public void setDefaultPosition(int position) {
            this.defaultPosition = position;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustom(position, convertView, parent);
        }

        public View getCustom(int position, View convertView, ViewGroup parent) {

            View row = LayoutInflater.from(context).inflate(layout == 0 ? R.layout.spinner_item : layout, parent, false);
            SmartTextView label = (SmartTextView) row.findViewById(R.id.text1);
            label.setText(list.get(position));

            return row;
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            View row = LayoutInflater.from(context).inflate(R.layout.spinner_dropdown_item, parent, false);
            SmartTextView label = (SmartTextView) row.findViewById(R.id.text1);
            label.setText(list.get(position));

            return row;
        }
    }

    /**
     * This method used to get multi-selection dialog.
     *
     * @param name       represented dialog title
     * @param jsonString represented json data
     * @param value      represented value pre-selected
     * @param target     represented {@link CustomClickListener}
     */
    public static void getMultiSelectionDialog(Context context, final String name, String jsonString,
                                               final String value, final CustomClickListener target) {

        final ArrayList<String> values = new ArrayList<String>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            int size = jsonArray.length();
            for (int i = 0; i < size; i++) {
                values.add(((JSONObject) jsonArray.get(i)).getString("value").trim());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final boolean[] selections = new boolean[values.size()];
        final StringBuilder newValue = new StringBuilder();

        AlertDialog alert = null;

        if (value.length() > 0) {
            String[] oldValue = value.split(",");
            int size = values.size();
            for (int i = 0; i < size; i++) {
                int len = oldValue.length;
                for (int j = 0; j < len; j++) {
                    if (values.get(i).toString().trim().equalsIgnoreCase(oldValue[j].trim())) {
                        selections[i] = true;
                        break;
                    }
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(name);
        builder.setMultiChoiceItems(values.toArray(new CharSequence[values.size()]), selections,
                new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        selections[which] = isChecked;
                    }
                });

        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int size = selections.length;
                for (int i = 0; i < size; i++) {
                    if (selections[i]) {
                        newValue.append(newValue.length() > 0 ? "," + values.get(i) : values.get(i));
                    }
                }
                target.onClick(newValue.toString());

            }
        });
        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                target.onClick(value);
            }
        });
        alert = builder.create();
        alert.show();
    }


    /**
     * This method used to get multi-selection dialog.
     * Specially for customFilter Pro Dynamic Form
     *
     * @param name           represented dialog title
     * @param jsonString     represented json data
     * @param selectedValues represented value pre-selected
     * @param selectedIDs    represented ids pre-selected
     * @param target         represented {@link CustomClickListener}
     */




    /**
     * This method used to get multi-selection dialog.
     *
     * @param name           represented dialog title
     * @param jsonString     represented json data
     * @param selectedValues represented value pre-selected
     * @param selectedIDs    represented ids pre-selected
     * @param target         represented {@link CustomClickListener}
     */
    public static void getVMMultiSelectionDialog(Context context, final String name, String jsonString,
                                                 final String selectedValues, final String selectedIDs, final MultipleSelectListener target) {

        final ArrayList<String> items = new ArrayList<String>();
        final ArrayList<String> values = new ArrayList<String>();
        final ArrayList<String> ids = new ArrayList<String>();
        try {
            JSONArray options = new JSONArray(jsonString);
            for (int i = 0; i < options.length(); i++) {
                JSONObject option = options.getJSONObject(i);
                items.add(option.getString("text"));
                values.add(option.getString("text"));
                ids.add(option.getString("value"));
                if (option.has("children")) {
                    JSONArray first_children = option.getJSONArray("children");
                    if (first_children != null && first_children.length() > 0) {
                        for (int j = 0; j < first_children.length(); j++) {
                            JSONObject first_children_option = first_children.getJSONObject(j);
                            items.add("-- " + first_children_option.getString("text"));
                            values.add(first_children_option.getString("text"));
                            ids.add(first_children_option.getString("value"));
                            if (first_children_option.has("children")) {
                                JSONArray second_children = first_children_option.getJSONArray("children");
                                if (second_children != null && second_children.length() > 0) {
                                    for (int k = 0; k < second_children.length(); k++) {
                                        JSONObject second_children_option = second_children.getJSONObject(k);
                                        items.add("--- " + second_children_option.getString("text"));
                                        values.add(second_children_option.getString("text"));
                                        ids.add(second_children_option.getString("value"));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            final boolean[] selections = new boolean[values.size()];
            final StringBuilder newValue = new StringBuilder();
            final StringBuilder newIds = new StringBuilder();

            AlertDialog alert = null;

            if (selectedValues.length() > 0) {
                String[] oldValue = selectedValues.split(",");
                for (int i = 0; i < values.size(); i++) {
                    int len = oldValue.length;
                    for (int j = 0; j < len; j++) {
                        if (values.get(i).toString().trim().equalsIgnoreCase(oldValue[j].trim())) {
                            selections[i] = true;
                            break;
                        }
                    }
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(name);
            builder.setMultiChoiceItems(items.toArray(new CharSequence[items.size()]), selections,
                    new DialogInterface.OnMultiChoiceClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            selections[which] = isChecked;
                        }
                    });

            builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int size = selections.length;
                    for (int i = 0; i < size; i++) {
                        if (selections[i]) {
                            newValue.append(newValue.length() > 0 ? "," + values.get(i) : values.get(i));
                            newIds.append(newIds.length() > 0 ? "," + ids.get(i) : ids.get(i));
                        }
                    }
                    target.onClick(newValue.toString(), newIds.toString());

                }
            });
            builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    target.onClick(selectedValues, selectedIDs);
                }
            });
            alert = builder.create();
            alert.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {
        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });
    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {

                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "View Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, "View More", true);
                    }

                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);
        }
        return ssb;
    }

    public static boolean isValidLatLng(double lat, double lng) {
        if (lat < -90 || lat > 90) {
            return false;
        } else if (lng < -180 || lng > 180) {
            return false;
        }
        return true;
    }

//    public static void ShowImageDialog(Context context, String image) {
//        View openFileView = LayoutInflater.from(context).inflate(R.layout.jom_group_all_files_display, null);
//        final ImageView imgDisplay = (ImageView) openFileView.findViewById(R.id.imgDisplay);
//        final ImageView closeDialog = (ImageView) openFileView.findViewById(R.id.attached_file_close);
//        final ProgressBar progressBarImage = (ProgressBar) openFileView.findViewById(R.id.progressBarImage);
//        progressBarImage.setIndeterminate(true);
//
//        new AQuery(context).id(imgDisplay).progress(progressBarImage).image(image, true, true);
//
//        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
//                .setCancelable(false);
//
//
//        builder.setView(openFileView);
//        final AlertDialog alert = builder.create();
//        alert.setCancelable(true);
//        alert.show();
//        alert.setCanceledOnTouchOutside(true);
//        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        closeDialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alert.dismiss();
//            }
//        });
//    }

    /**
     * This method used to show user privacy selection dialog.
     */
//    static public void ShowPrivacyDialog(final Context context, final PrivacyListener target) {
//        View privacyView = LayoutInflater.from(context).inflate(R.layout.ijoomer_privacy_dialog, null);
//
//        LinearLayout btnPrivacyPublic = (LinearLayout) privacyView.findViewById(R.id.btnPrivacyPublic);
//        LinearLayout btnPrivacyMembers = (LinearLayout) privacyView.findViewById(R.id.btnPrivacyMembers);
//        LinearLayout btnPrivacyFriends = (LinearLayout) privacyView.findViewById(R.id.btnPrivacyFriends);
//        LinearLayout btnPrivacyOnlyMe = (LinearLayout) privacyView.findViewById(R.id.btnPrivacyOnlyMe);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
//        builder.setView(privacyView);
//
//        final AlertDialog alert = builder.create();
//
//        btnPrivacyPublic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                target.onClick(context.getString(R.string.privacy_public));
//                alert.dismiss();
//            }
//        });
//
//        btnPrivacyMembers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                target.onClick(context.getString(R.string.privacy_site_members));
//                alert.dismiss();
//            }
//        });
//
//        btnPrivacyFriends.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                target.onClick(context.getString(R.string.privacy_friends));
//                alert.dismiss();
//            }
//        });
//
//        btnPrivacyOnlyMe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                target.onClick(context.getString(R.string.privacy_only_me));
//                alert.dismiss();
//            }
//        });
//
//        alert.show();
//    }

    /**
     * This method used to show video upload type selection dialog.
     */
//    static public void ShowUploadVideoDialog(final Context context, final UploadVideoListener target) {
//        View videoSelectionView = LayoutInflater.from(context).inflate(R.layout.jom_upload_video_dialog, null);
//
//        LinearLayout btnLinkVideo = (LinearLayout) videoSelectionView.findViewById(R.id.btnLinkVideo);
//        LinearLayout btnUploadVideo = (LinearLayout) videoSelectionView.findViewById(R.id.btnUploadVideo);
//        SmartTextView txtVideoUploadLimit = (SmartTextView) videoSelectionView.findViewById(R.id.txtVideoUploadLimit);
//        txtVideoUploadLimit.setText(String.format(context.getString(R.string.max_upload_limit), JomConfigurations.getVideoUploadSize()));
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
//        builder.setView(videoSelectionView);
//
//        final AlertDialog alert = builder.create();
//
//        btnLinkVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                target.onClick(0);
//                alert.dismiss();
//            }
//        });
//
//        btnUploadVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                target.onClick(1);
//                alert.dismiss();
//            }
//        });
//
//        alert.show();
//    }

    /**
     * This method used to show edit post dialog.
     */
//    static public void editPostDialog(final Context context, ContentValues row, final int position,
//                                      boolean isCancelable, final EditPostListener target) {
//        View editPostView = LayoutInflater.from(context).inflate(R.layout.jom_edit_post_dialog, null);
//
//        ImageView imgWallOrActivityUserAvatar = (ImageView) editPostView.findViewById(R.id.imgWallOrActivityUserAvatar);
//        SmartTextView txtWallOrActivityUserName = (SmartTextView) editPostView.findViewById(R.id.txtWallOrActivityUserName);
//        SmartTextView txtWallOrActivityDate = (SmartTextView) editPostView.findViewById(R.id.txtWallOrActivityDate);
//        final SmartEditText edtWallOrActivityMessage = (SmartEditText) editPostView.findViewById(R.id.edtWallOrActivityMessage);
//
//        imgWallOrActivityUserAvatar.setVisibility(View.GONE);
//        txtWallOrActivityUserName.setVisibility(View.GONE);
//        txtWallOrActivityDate.setVisibility(View.GONE);
//
//        final String[] status = {null};
//        String wallID=null;
//        try{
//             wallID=row.getAsString(ID);
//            JSONObject body= new JSONObject(row.getAsString(BODYIJ));
//             status[0] =body.getString(CONTENT);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
////
//
//
//        edtWallOrActivityMessage.setText(Html.fromHtml(status[0]));
//
//
//        final String finalStatus = status[0];
//
//        final String finalWallID = wallID;
//        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
//                .setCancelable(false)
//                .setPositiveButton(context.getString(R.string.save), new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, final int id) {
//                        status[0] =edtWallOrActivityMessage.getText().toString();
//                        target.onClick(finalWallID, status[0],position);
//                        dialog.cancel();
//                    }
//                })
//                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, final int id) {
//                        dialog.cancel();
//                    }
//                });
//
//        builder.setView(editPostView);
//        final AlertDialog alert = builder.create();
//        alert.setCancelable(isCancelable);
//        alert.show();
//    }

//    static public void shareActivityDialog(final Context context, ContentValues row, final int position,
//                                           boolean isCancelable, final ShareActivityListener target, boolean isImagedialog){
//        View shareActivityView = LayoutInflater.from(context).inflate(R.layout.jom_share_activity_dialog, null);
//
//        final String[] selectedPrivacy = {"0"};
//        String actID="",appType="";
//        final String[] message = {""};
//        String shareDescTit="";
//        String imageThumbUrl="";
//        String shareDescDet="";
//        String actorName="";
//        String bodyContent="";
//        aQuery= new AQuery(context);
//        final ImageView btnPrivacy = (ImageView) shareActivityView.findViewById(R.id.btnPrivacy);
//        final ImageView shareDescImage = (ImageView) shareActivityView.findViewById(R.id.shareDescImage);
//        SmartButton btnShare = (SmartButton) shareActivityView.findViewById(R.id.btnShare);
//        SmartButton btnCancel = (SmartButton) shareActivityView.findViewById(R.id.btnCancel);
//        SmartTextView shareDescTitle = (SmartTextView) shareActivityView.findViewById(R.id.shareDescTitle);
//        SmartTextView shareDescDetail = (SmartTextView) shareActivityView.findViewById(R.id.shareDescDetail);
//        final SmartEditText saySomeThing = (SmartEditText) shareActivityView.findViewById(R.id.saySomeThing);
//
//        try{
//            actID=row.getAsString(ID);
//            appType=row.getAsString(APP_IJ);
//            JSONObject body=new JSONObject(row.getAsString(BODYIJ));
//            JSONObject header=new JSONObject(row.getAsString(HEADER_IJ));
//            JSONObject actor=header.getJSONObject(ACTORIJ);
//            JSONObject album,video;
//            JSONArray attachments,photos;
//            actorName=actor.getString(NAME);
//            bodyContent=body.getString(CONTENT);
//            if(isImagedialog){
//                shareDescImage.setVisibility(View.VISIBLE);
//                if(appType.equals("photos")){
//                    try{
//                        if(body.has(PHOTOS)&&body.get(PHOTOS)instanceof JSONArray){
//                            photos=body.getJSONArray(PHOTOS);
//                            JSONObject jObj=photos.getJSONObject(0);
//                            imageThumbUrl=jObj.getString(EDITURL);
//                        }
//                    }catch (Exception je){
//                        je.printStackTrace();
//                    }
//                }else if(appType.equals("videos.linking")){
//                    try{
//                        if(body.has(ATTACHMENTS)&&body.get(ATTACHMENTS)instanceof JSONArray){
//                            attachments =body.getJSONArray(ATTACHMENTS);
//                            if(attachments!=null&&attachments.length()>0){
//                                JSONObject jObj=attachments.getJSONObject(0);
//                                if(jObj.has(VIDEO)&&jObj.get(VIDEO)instanceof JSONObject){
//                                    video=jObj.getJSONObject(VIDEO);
//                                    imageThumbUrl=video.getString(THUMB);
//                                }
//                            }
//                        }
//                    }catch (Exception je){
//                        je.printStackTrace();
//                    }
//                }
//            }else{
//                shareDescImage.setVisibility(View.GONE);
//            }
//            if(imageThumbUrl.trim().length()>0){
//                aQuery.id(shareDescImage).image(imageThumbUrl);
//            }
//            shareDescTitle.setText(bodyContent);
//            shareDescDetail.setText("By "+actorName);
//
//        }catch (Exception je){
//            je.printStackTrace();
//        }
//
//        btnPrivacy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SmartUtils.ShowPrivacyDialog(context, new PrivacyListener() {
//                    @Override
//                    public void onClick(String privacy) {
////                        txtWhoCanSee.setText(privacy);
//                        selectedPrivacy[0] = getPrivacyCode(privacy,context);
//                        if (selectedPrivacy[0].equals("0")) {
//                            btnPrivacy.setBackgroundResource(R.drawable.ijoomer_privacy_public);
//                        } else if (selectedPrivacy[0].equals("20")) {
//                            btnPrivacy.setBackgroundResource(R.drawable.ijoomer_privacy_sitemember);
//                        } else if (selectedPrivacy[0].equals("30")) {
//                            btnPrivacy.setBackgroundResource(R.drawable.ijoomer_privacy_friend);
//                        } else if (selectedPrivacy[0].equals("40")) {
//                            btnPrivacy.setBackgroundResource(R.drawable.ijoomer_privacy_onlyme);
//                        }
//                    }
//                });
//            }
//        });
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
//                .setCancelable(false);
//        builder.setView(shareActivityView);
//        final AlertDialog alert = builder.create();
//        alert.setCancelable(isCancelable);
//        alert.show();
//
//        final String finalActID = actID;
//
//        btnShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                message[0] =saySomeThing.getText().toString();
//                target.onClick(finalActID, message[0],selectedPrivacy[0]);
//                alert.cancel();
//            }
//        });
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alert.cancel();
//            }
//        });
//
//
//    }

//    public static String getPrivacyCode(String privacy, Context context) {
//        ArrayList<String> list = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(R.array.wall_post_type)));
//        if (privacy.equals(list.get(0))) {
//            return "0";
//        } else if (privacy.equals(list.get(1))) {
//            return "20";
//        } else if (privacy.equals(list.get(2))) {
//            return "30";
//        } else if (privacy.equals(list.get(3))) {
//            return "40";
//        }
//        return "0";
//    }


 public static String getCurrentDate(){
     String registerDate1="";
     Calendar c = Calendar.getInstance();
     SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     registerDate1 = df.format(c.getTime());
     return registerDate1;
 }
    public static long getTimeStampInMillisecond(String dateStr){
        long dateTime=0;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = df.parse(dateStr);
            dateTime = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTime;
    }

    public static String getDateFromTimeStamp(long timeStamp){
        String dateTime="";
        SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, ''yy 'at' HH:mm");
        Date date = null;
        try {
            Date netDate = (new Date(timeStamp));
            dateTime=df.format(netDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTime;
    }

}
