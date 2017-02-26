package com.hungryhackers.bvpal;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;

/**
 * Created by YourFather on 20-02-2017.
 */

public class MyTabLayout extends TabLayout {
    Context mContext;

    public MyTabLayout(Context context) {
        super(context);
        mContext = context;
    }

    @NonNull
    public Tab newTab(int resId, int colorId) {
        Tab tab = super.newTab();
        tab.setIcon(resId);
        int unselectedTabIconColor = ContextCompat.getColor(mContext, colorId);
        tab.getIcon().setColorFilter(unselectedTabIconColor, PorterDuff.Mode.SRC_IN);
        return tab;
    }

    @Override
    public void addTab(@NonNull Tab tab, boolean setSelected) {
        super.addTab(tab, setSelected);
    }
}
