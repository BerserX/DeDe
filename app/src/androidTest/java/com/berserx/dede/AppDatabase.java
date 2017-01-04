package com.berserx.dede;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import io.requery.android.database.sqlite.SQLiteDatabase;
import io.requery.android.database.sqlite.SQLiteOpenHelper;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

public class AppDatabase extends SQLiteOpenHelper {
    // Instance variables
    private static AppDatabase instance;
    private static final String TAG = "AppDatabase";

    // Database Info
    private static final String DATABASE_NAME = "AppDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_NAME = "generic";

    // Post Table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_DATA = "data";
/*
    public AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
*/
    public static synchronized AppDatabase getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (instance == null) {
            instance = new AppDatabase(context.getApplicationContext());
        }
        return instance;
    }

    private AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PROFILES_TABLE = "CREATE TABLE " + TABLE_NAME +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_DATA + " TEXT" +
                ")";
        db.execSQL(CREATE_PROFILES_TABLE);
        Log.d(TAG, "Creating database");
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            Log.d(TAG, "Upgrading database, found old version");
            onCreate(db);
        }
    }

    public int addData (String data) {
        SQLiteDatabase db = getWritableDatabase();
        int id = 0;
        db.beginTransaction();
        try {
            Cursor cursor = db.rawQuery("INSERT or FAIL INTO " + TABLE_NAME + " " +
                    "(" + KEY_DATA + ") " +
                    "VALUES (" + data + ")", null);
            if (cursor.moveToFirst()) {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add data to database");
            return -1;
        } finally {
            db.endTransaction();
            return id;
        }
    }

    public String getData (int id) {
        SQLiteDatabase db = getReadableDatabase();
        String data = "{}";
        db.beginTransaction();
        try {
            Cursor cursor = db.rawQuery("SELECT data FROM " + TABLE_NAME + " " +
                    "WHERE data.id = " + id, null);
            if (cursor.moveToFirst()) {
                data = cursor.getString(cursor.getColumnIndex(KEY_DATA));
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get data from database");
        } finally {
            db.endTransaction();
            return data;
        }
    }

    public int setData (int id, String data) {
        SQLiteDatabase db = getWritableDatabase();
        int success = 0;
        db.beginTransaction();
        try {
            Cursor cursor = db.rawQuery("UPDATE or FAIL " + TABLE_NAME + " " +
                    "SET " + KEY_DATA + " = " + data + " " +
                    "WHERE " + KEY_ID + " = " + id, null);
            if (cursor.moveToFirst()) {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to set data in database");
            return -1;
        } finally {
            db.endTransaction();
            return id;
        }
    }

    public String matchData (JSONObject filter) {
        String result = "{}";
        String clause = "WHERE ";
        /*
        Example:

        {
            field1: {
                _between: [0, 10],
                _and: {

                    }
                },
            field2: {
                field3: ["value1", "value2"]
            }
        }
         */
        clause = parseFilters (filter, clause);
        return result;
    }

    // Parse string into JSONObject and generate SELECT pattern according to schema containing ranges, etc.
    private String parseFilters (JSONObject filter, String clause) {
        Iterator<String> keys = filter.keys ();
        String k;
        JSONObject f;
        String value;
        JSONArray array;
        int minValue;
        int maxValue;
        //StringEscapeUtils escape;
        while (keys.hasNext()) {
            k = keys.next();
            if (k.equals("_between")) {
                array = filter.optJSONArray(k);
                if (array != null && array.length() == 2) {
                    try {
                        minValue = array.getInt(0);
                        maxValue = array.getInt(0);
                        clause += k + " > "+ minValue + " AND " + k + " < "+ maxValue;
                    } catch (Exception e) {
                        Log.d(TAG, "Unable to parse filter correctly. Range error.");
                    } finally {
                        minValue = 0;
                        maxValue = 0;
                    }
                }
            } else if (k.equals("_like")) {
                value = filter.optString(k);
                if (value != null) {
                    clause += k + " LIKE '" + value +"'";
                }
            } else if (k.equals("_and")) {

            } else if (k.equals("_or")) {

            } else {
                f = filter.optJSONObject(k);
                if (filter != null) {
                    clause += parseFilters(f, clause);
                } else {
                    // String match
                    value = filter.optString(k);
                    if (value != null) {
                        clause += k + " = '" + value + "'";
                    } else {
                        array = filter.optJSONArray(k);
                        if (array != null) {
                            try {
                                value = array.join(",");
                                clause += k + " IN (" + value + ")";
                            } catch (Exception e) {
                                Log.d(TAG, "Unable to parse filter correctly.");
                            } finally {
                                value = "";
                            }
                        }
                    }
                    if (keys.hasNext()) {
                        clause += " AND ";
                    }
                }
            }
        }
        return clause;
    }

    String escapeText (String text) {
        return text.replaceAll ("(?<!\\)['\"]","");
    }
}

