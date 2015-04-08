package com.wp.demo.psbcdemo2.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.wp.demo.psbc.count.PSBCCount;
import com.wp.demo.psbc.count.PSBCCount.Company_data;
import com.wp.demo.psbc.count.PSBCCount.Personnel;
import com.wp.demo.psbc.count.PSBCCount.Tables;

public class PSBCDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 10000;

    private static PSBCDatabaseHelper helper;

    public static final String DATABASE_NAME = PSBCCount.DATABASE_NAME;

    public PSBCDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static PSBCDatabaseHelper getInstance(Context context) {
        if (helper == null) {
            helper = new PSBCDatabaseHelper(context);
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS " + Tables.COMPANY_DATA + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Company_data.ID + " TEXT," + Company_data.DATA_TITLE + " TEXT,"
                + Company_data.DATA_INFORMATION + " TEXT," + Company_data.DATA_IMAGE
                + " BLOB," + Company_data.DATA_THUMBNAIL + " TEXT,"
                + Company_data.DATA_5 + " TEXT," + Company_data.DATA_6
                + " TEXT," + Company_data.DATA_7 + " TEXT,"
                + Company_data.DATA_8 + " TEXT," + Company_data.DATA_9
                + " TEXT" + ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + Tables.PERSONNEL + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Personnel.ID + " TEXT," + Personnel.USER_NAME + " TEXT,"
                + Personnel.PASSWORD + " TEXT," + Personnel.VALIDITY + " TEXT,"
                + Personnel.LEVEL + " TEXT," + Personnel.DATA_3 + " TEXT"
                + ");");

        db.execSQL("CREATE TRIGGER " + Tables.PERSONNEL + "_delete "
                + "   BEFORE DELETE ON " + Tables.PERSONNEL + " BEGIN "
                + "   DELETE FROM " + Tables.COMPANY_DATA + "   WHERE "
                + Company_data.ID + " = OLD." + Personnel.ID + ";" + " END");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }
}
