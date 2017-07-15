package com.example.shwetashahane.assignment5;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by shwetashahane on 3/19/17.
 */

public class CustomUserListAdapter extends ArrayAdapter<String> {

    Context context;
    int layoutResourceId;
    ArrayList<String> data = null;

    public CustomUserListAdapter(Context context, int layoutResourceId, ArrayList<String> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PersonHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new PersonHolder();
            holder.nickNameUCL = (TextView) row.findViewById(R.id.nickNameUCL);

            row.setTag(holder);
        } else {
            holder = (PersonHolder) row.getTag();
        }
        if(holder.nickNameUCL != null)
            holder.nickNameUCL.setText(data.get(position));
        return row;
    }



    static class PersonHolder {
        TextView nickNameUCL;

    }

}