package com.example.selfmadekid.adapters;

import com.example.selfmadekid.select_role_tabs.TabChild;
import com.example.selfmadekid.select_role_tabs.TabParent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class RoleSwitchingAdapter extends FragmentStatePagerAdapter {

    private final int NUMBEER_OF_PAGES = 2;


    public RoleSwitchingAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                TabParent tabParent = new TabParent();
                return tabParent;
            case 1:
                TabChild tabChild = new TabChild();
                return tabChild;

        }

        return null;
    }

    @Override
    public int getCount() {
        return NUMBEER_OF_PAGES;
    }
}
