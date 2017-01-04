package com.berserx.dede;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Anton on 2016-09-19.
 */
public class ProfileAdapter extends ArrayAdapter<Profile> {

    public static int TYPE_PROFILE = 0;
    public static int TYPE_PEERS = 1;

    private int type;
    public ProfileAdapter(Context context, ArrayList<Profile> profile) {
        super(context, 0, profile);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Profile profile = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_item, parent, false);
        }
        // Lookup view for data population
        TextView property = (TextView) convertView.findViewById(R.id.profile_property);
        TextView value = (TextView) convertView.findViewById(R.id.profile_value);
        // Populate the data into the template view using the data object
        property.setText(profile.property);
        value.setText(profile.value);
        // Return the completed view to render on screen
        return convertView;
    }
}
