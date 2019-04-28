package com.example.selfmadekid.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.SparseArray;

public class AppData {
    public static final int CHILD = 1;
    public static final int PARENT = 2;
    private static int currentUserID = -1;
    private static int currentState = PARENT;
    private static long lastUpdate = 0;
    private static boolean loaded = false;
    private static final long UPDATE_DELAY= 1000*60*60;
    private static SparseArray<ChildContainer> children = new SparseArray<ChildContainer>();
    public static SparseArray<ChildContainer> getChildren() {
        return children;
    }



    public static void setCurrentState(int currentState) {
        AppData.currentState = currentState;
    }

    public static int getCurrentUserID() {
        return AppData.currentUserID;
    }

    public static void setCurrentUserID(int currentUserID) {
        AppData.currentUserID = currentUserID;
    }

    public void setLastUpdate(int lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public static void setChildren(SparseArray<ChildContainer> children) {
        AppData.children = children;
    }

    public static int getCurrentState() {
        return currentState;
    }

    public static long getLastUpdate() {
        return AppData.lastUpdate;
    }

    public static void setLastUpdate(long lastUpdate) {
        AppData.lastUpdate = lastUpdate;
    }

    public static long getUpdateDelay() {
        return UPDATE_DELAY;
    }

    public static void writeData(Context context){
        try {
            SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor myEditor = myPreferences.edit();
            myEditor.putInt("id", currentUserID);
            myEditor.putInt("state", currentState);
            myEditor.putLong("lastUpdate", lastUpdate);
            myEditor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readData(Context context){
        try {
            SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            currentUserID = myPreferences.getInt("id", -1);
            currentState = myPreferences.getInt("state", -1);
            //lastUpdate = myPreferences.getLong("lastUpdate", -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static void setLoaded(boolean loaded) {
        AppData.loaded = loaded;
    }

    public static void clearData(Context context){
        currentUserID = -1;
        lastUpdate = 0;
        loaded = false;
        children = new SparseArray<ChildContainer>();
        writeData(context);
    }


    public static void clearChildrenData(Context context){
        children = new SparseArray<ChildContainer>();
    }


}
