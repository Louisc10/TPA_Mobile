package com.example.readup.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    static final String KEY_USER_ID ="user_id";
    static final String KEY_USER_EMAIL ="user_email";
    static final String KEY_GENRE ="user_genre";

    private static SharedPreferences getSharedPreference(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setKeyUserId(Context context, String username){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_USER_ID, username);
        editor.apply();
    }

    public static String getKeyUserId(Context context){
        return getSharedPreference(context).getString(KEY_USER_ID,"");
    }

    public static void setKeyUserEmail(Context context, String username){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_USER_EMAIL, username);
        editor.apply();
    }

    public static String getKeyUserEmail(Context context){
        return getSharedPreference(context).getString(KEY_USER_EMAIL,"");
    }

    public static void setKeyGenre(Context context, String username){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_GENRE, username);
        editor.apply();
    }

    public static String getKeyGenre(Context context){
        return getSharedPreference(context).getString(KEY_GENRE,"");
    }
}
