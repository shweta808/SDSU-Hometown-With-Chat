package com.example.shwetashahane.assignment5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by Akash on 08-04-2017.
 */

public class DBUtility extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="Person.db";
    public static final String TABLE_NAME="home";
    public static final String nickname = "nickname";
    public static final String country = "country";
    public static final String state = "state";
    public static final String city = "city";
    public static final String year = "year";
    public static final String latitude = "latitude";
    public static final String longitude = "longitude";
    public static final String id = "id";
    public static final String timeStamp = "timeStamp";

    public DBUtility(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (id VARCHAR PRIMARY KEY,nickname TEXT,country TEXT,state TEXT,city TEXT,year BIGINT,latitude REAL,longitude REAL,timeStamp VARCHAR)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public long getRowCount()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        long numberOfRows = DatabaseUtils.queryNumEntries(db, "home");
        System.out.println("The count is "+numberOfRows);
        return numberOfRows;
    }


    public boolean insertIntoDB(Person person) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contValues = new ContentValues();
        contValues.put(id,person.getId());
        contValues.put(nickname,person.getNickname());
        contValues.put(country,person.getCountry());
        contValues.put(state,person.getState());
        contValues.put(city,person.getCity());
        contValues.put(year,person.getYear());
        contValues.put(latitude,person.getLat());
        contValues.put(longitude,person.getLongitude());
        contValues.put(timeStamp,person.getTimeStamp());


        long result = db.insertWithOnConflict(TABLE_NAME,null ,contValues, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();

        if(result == -1)
            return false;
        else
            return true;

    }

    public Cursor fetchData() {
        long limit = SidePanelFragment.updateCount;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" LIMIT " + limit,null);
        return res;
    }

    public Cursor fetchFilterData(String query){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(query,null);
        return res;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }

    public long getMinId() {
        long level=0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT id FROM home WHERE id  = (SELECT MIN(id) FROM home)",null);
        while (res.moveToNext()) {
            level = res.getLong(0);
        }
        return level;

    }
    public JSONArray convertCurToJson(Cursor cursor) {

        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i),
                                cursor.getString(i));
                    } catch (Exception e) {
                        System.out.println("cursor exception");
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        return resultSet;

    }
}
