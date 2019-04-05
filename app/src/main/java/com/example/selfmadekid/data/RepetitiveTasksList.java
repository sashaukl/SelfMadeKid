package com.example.selfmadekid.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RepetitiveTasksList extends ArrayList<RepetitiveTask>{



    @Override
    public boolean add(RepetitiveTask o) {
        boolean b = super.add(o);
        sort(new MyComparator());
        return b;
    }

    public class MyComparator implements Comparator<RepetitiveTask>
    {
        public int compare(RepetitiveTask o1, RepetitiveTask o2) {
            return o1.compareTo(o2);
        }
    }

}
