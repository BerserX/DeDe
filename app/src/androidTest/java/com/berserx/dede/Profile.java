package com.berserx.dede;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Anton on 2016-09-19.
 */
public class Profile {
    private String id;
    private String age;
    private String gender;
    private String alias;
    private String broadcast;

    public static Profile fromJson(JSONObject jsonObject) {
        Profile profile = new Profile();
        try {
            profile.id = jsonObject.getString("id");
            profile.age = jsonObject.getString("age");
            profile.gender = jsonObject.getString("gender");
            profile.alias = jsonObject.getString("alias");
            profile.broadcast = jsonObject.getString("broadcast");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return profile;
    }

    public static ArrayList<Profile> fromJson(JSONArray jsonArray) {
        JSONObject jsonObject;
        ArrayList<Profile> profiles = new ArrayList<Profile>(jsonArray.length());

        for (int x=0; x < jsonArray.length(); x++) {
            try {
                jsonObject = jsonArray.getJSONObject(x);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Profile profile = Profile.fromJson(jsonObject);
            if (profile != null) {
                profiles.add(profile);
            }
        }

        return profiles;
    }

    public Item getId() {
        return new Item ("id", this.id);
    }

    public Item getAge() {
        return this.age;
    }

    public Item getGender() {
        return this.gender;
    }

    public Item getAlias() {
        return this.alias;
    }

    public String getBroadcast() {
        return this.broadcast;
    }
}
