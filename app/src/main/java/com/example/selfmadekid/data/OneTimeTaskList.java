package com.example.selfmadekid.data;

import java.util.ArrayList;
import java.util.Comparator;

public class OneTimeTaskList extends ArrayList<OneTimeTask> {



        @Override
        public boolean add(OneTimeTask o) {
            boolean b = super.add(o);
            sort(new MyComparator());
            return b;
        }

        public class MyComparator implements Comparator<OneTimeTask>
        {
            public int compare(OneTimeTask o1, OneTimeTask o2) {
                return o1.compareTo(o2);
            }
        }
}
