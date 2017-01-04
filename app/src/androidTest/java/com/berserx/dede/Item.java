package com.berserx.dede;

import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;

public class Item extends JSONObject {
    protected int id;
    protected String data;
    protected JSONObject json;
    protected AppDatabase db;

    public Item(Context context, int id) {
        super ();
        db = AppDatabase.getInstance(context);
        if (id < 0) {
            id = db.addData(toString ());
        }
        if (id < 0) {
            data = db.getData (id);
        }
    }
}
