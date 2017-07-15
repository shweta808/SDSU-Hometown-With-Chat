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

public class CustomListAdapter extends ArrayAdapter<Person> {

    Context context;
    int layoutResourceId;
    ArrayList<Person> data = null;

    public CustomListAdapter(Context context, int layoutResourceId, ArrayList<Person> data) {
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
            holder.nickNameCL = (TextView) row.findViewById(R.id.nickNameCL);
            holder.countryCL = (TextView) row.findViewById(R.id.countryCL);
            holder.stateCL = (TextView) row.findViewById(R.id.stateCL);
            holder.cityCL = (TextView) row.findViewById(R.id.cityCL);
            holder.yearCL = (TextView) row.findViewById(R.id.yearCL);

            row.setTag(holder);
        } else {
            holder = (PersonHolder) row.getTag();
        }

        Person person = data.get(position);
        holder.nickNameCL.setText(person.getNickname());
        holder.countryCL.setText(person.getCountry());
        holder.stateCL.setText(person.getState());
        holder.cityCL.setText(person.getCity());
        holder.yearCL.setText(String.valueOf(person.getYear()));
        return row;
    }



    static class PersonHolder {
        TextView nickNameCL;
        TextView countryCL;
        TextView stateCL;
        TextView cityCL;
        TextView yearCL;
    }


    public String getNickNameAtPosition(int position) {
        return super.getItem(position).getNickname();
    }
}