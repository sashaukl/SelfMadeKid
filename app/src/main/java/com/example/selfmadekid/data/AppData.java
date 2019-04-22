package com.example.selfmadekid.data;

import android.util.SparseArray;

public class AppData {
    public static final int CHILD = 1;
    public static final int PARENT = 2;
    private static int currentUserID = -1;

    private static int currentState = PARENT;
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

    public static void setCirrentUserID(int cirrentUserID) {
        AppData.currentUserID = cirrentUserID;
    }

    public static int getCurrentState() {
        return currentState;
    }
}
