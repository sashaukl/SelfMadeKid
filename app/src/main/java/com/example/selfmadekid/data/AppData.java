package com.example.selfmadekid.data;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;

public class AppData {
    public static final int CHILD = 1;
    public static final int PARENT = 2;


    private static int currentState = PARENT;
    private static SparseArray<ChildContainer> children = new SparseArray<ChildContainer>();
    public static SparseArray<ChildContainer> getChildren() {
        return children;
    }



    public static void setCurrentState(int currentState) {
        AppData.currentState = currentState;
    }

    public static int getCurrentState() {
        return currentState;
    }
}
