package com.github.martoreto.aademo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class foe storing primitive preference values for the application.
 * <p/>
 * The singleton scope: a new instance of the bean is created the first time it
 * is needed. It is then retained and the same instance is always injected.
 */
public class ApplicationPreference {

    SharedPreferences preferences;

    // Name of the preference file under data/data/application_preference package
    static String preference_name = "advanced_entry_preference";

    /**
     * Put boolean value to preference.
     *
     * @param key     the key
     * @param value   the value
     * @param context the context
     */
    @SuppressLint("NewApi")
    public static void setBoolean(String key, boolean value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(preference_name,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Gets the boolean value from preference.
     *
     * @param key      the key
     * @param defValue the default value
     * @param context  the context
     * @return the boolean
     */
    public static boolean getBoolean(String key, boolean defValue, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(preference_name,
                Context.MODE_PRIVATE);
        boolean value = preferences.getBoolean(key, defValue);
        return value;
    }

    /**
     * Put string value to preference.
     *
     * @param key     the key
     * @param value   the value
     * @param context the Context
     */
    @SuppressLint("NewApi")
    public static void setString(String key, String value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                preference_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Gets the string value from preference.
     *
     * @param key      the key
     * @param defValue the default value
     * @param context  the context
     * @return the string
     */
    public static String getString(String key, String defValue, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                preference_name, Context.MODE_PRIVATE);
        String value = preferences.getString(key, defValue);
        return value;

    }


    /**
     * Put string value to preference.
     *
     * @param key     the key
     * @param value   the value
     * @param context the context
     */
    @SuppressLint("NewApi")
    public static void setLong(String key, long value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                preference_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * Gets the string value from preference.
     *
     * @param key      the key
     * @param defValue the default value
     * @param context  the context
     * @return the string
     */
    public static long getLong(String key, long defValue, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                preference_name, Context.MODE_PRIVATE);
        long value = preferences.getLong(key, defValue);
        return value;

    }

    /**
     * Put integer value to preference.
     *
     * @param key     the key
     * @param value   the value
     * @param context the context
     */
    @SuppressLint("NewApi")
    public static void setInteger(String key, int value, Context context) {

        SharedPreferences preferences = context.getSharedPreferences(preference_name,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void setFloat(String key, float value, Context context) {

        SharedPreferences preferences = context.getSharedPreferences(preference_name,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static float getFloat(String key, float defValue, Context context) {

        SharedPreferences preferences = context.getSharedPreferences(preference_name, Context.MODE_PRIVATE);
        return preferences.getFloat(key, defValue);
    }

    public static boolean contains(String key, Context context) {

        SharedPreferences preferences = context.getSharedPreferences(preference_name, Context.MODE_PRIVATE);
        return preferences.contains(key);
    }

    /**
     * Gets the integer value from preference.
     *
     * @param key      the key
     * @param defValue the default value
     * @param context  the context
     * @return the integer
     */
    public static int getInteger(String key, int defValue, Context context) {

        SharedPreferences preferences = context.getSharedPreferences(preference_name,
                Context.MODE_PRIVATE);
        int value = preferences.getInt(key, defValue);
        return value;
    }

    public static void clearDriverProfilePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(preference_name,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

}
