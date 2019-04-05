package com.example.selfmadekid.data;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;

public class AppData {

    private static SparseArray<ChildContainer> children = new SparseArray<ChildContainer>();

    public static SparseArray<ChildContainer> getChildren() {
        return children;
    }
}
