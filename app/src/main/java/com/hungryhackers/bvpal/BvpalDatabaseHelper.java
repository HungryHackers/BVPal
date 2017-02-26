package com.hungryhackers.bvpal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by YourFather on 26-02-2017.
 */

public class BvpalDatabaseHelper extends SQLiteOpenHelper {
    final static String BVPAL_TABLE_NAME = "PersonalEvents";
    final static String BVPAL_TABLE_COLUMN_TITLE = "Title";
    final static String BVPAL_TABLE_COLUMN_CATEGORY = "Category";
    final static String BVPAL_TABLE_COLUMN_DESCRIPTION = "Description";
//    final static String BVPAL_TABLE_COLUMN_TITLE = "PersonalEvents";
//    final static String BVPAL_TABLE_COLUMN_TITLE = "PersonalEvents";
//    final static String BVPAL_TABLE_COLUMN_TITLE = "PersonalEvents";

    public BvpalDatabaseHelper(Context context, int version) {
        super(context, "BVPalDb", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
