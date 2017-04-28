package com.smart.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.smartprime.R;

import java.util.ArrayList;

/**
 * This Class Contains All Method Related To IjoomerSpinnerAdapter.
 *
 * @author tasol
 */
public class SmartSpinnerAdapter extends ArrayAdapter<String> {

    Context context;
    ArrayList<String> list;
    ArrayList<String> list1;
    ArrayList<String> list2;

    /**
     * Overrides methods
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public SmartSpinnerAdapter(Context context, int textViewResourceId, String[] objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
    }

    public SmartSpinnerAdapter(Context context, ArrayList<String> objects, ArrayList<String> objects1, ArrayList<String> objects2) {
        super(context, 0, objects);
        this.context = context;
        list = objects;
        list1 = objects1;
        list2 = objects2;
    }


    /**
     * This method used to get custom view.
     *
     * @param position    represented position
     * @param convertView represented convert view
     * @param parent      represented parent view
     * @return represented {@link View}
     */
    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ;
        View row = inflater.inflate(R.layout.smart_custom_spinner, parent, false);
        SmartTextView txtSearch = (SmartTextView) row.findViewById(R.id.txtSearch);
        txtSearch.setText(list.get(position).trim());
        row.setTag(list1.get(position));
        if (list1.get(position).equals("group")) {
            txtSearch.setTypeface(null, Typeface.BOLD);
            txtSearch.setClickable(true);

        } else {
            txtSearch.setPadding(20, 5, 0, 0);
        }
        return row;
    }
}
