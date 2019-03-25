package com.example.selfmadekid.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MyTaskList extends ArrayList<ChildTask>{



    @Override
    public boolean add(ChildTask o) {
        boolean b = super.add(o);
        sort(new MyComparator());
        return b;
    }

    public class MyComparator implements Comparator<ChildTask>
    {
        public int compare(ChildTask o1, ChildTask o2) {
            return o1.compareTo(o2);
        }
    }
}
