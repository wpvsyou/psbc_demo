package com.wp.demo.psbcdemo1.demo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class PSBCDatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 10000;

	private static PSBCDatabaseHelper helper;

	public static final String DATABASE_NAME = "psbcdatabase";

	public interface Tables {
		public static final String COMPANY_DATA = "company_data";
		public static final String PERSONNEL = "personnel";
	}

	public interface Personnel {
		public static final String ID = "id";
		public static final String USER_NAME = "user_name";
		public static final String PASSWORD = "password";
		public static final String VALIDITY = "validity";
		public static final String DATA_2 = "data_2";
		public static final String DATA_3 = "data_3";
	}

	public interface Company_data {
		public static final String ID = "id";
		public static final String DATA_1 = "data_1";
		public static final String DATA_2 = "data_2";
		public static final String DATA_3 = "data_3";
		public static final String DATA_4 = "data_4";
		public static final String DATA_5 = "data_5";
		public static final String DATA_6 = "data_6";
		public static final String DATA_7 = "data_7";
		public static final String DATA_8 = "data_8";
		public static final String DATA_9 = "data_9";
	}

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
				+ Company_data.ID + " TEXT," + Company_data.DATA_1 + " TEXT,"
				+ Company_data.DATA_2 + " TEXT," + Company_data.DATA_3
				+ " TEXT," + Company_data.DATA_4 + " TEXT,"
				+ Company_data.DATA_5 + " TEXT," + Company_data.DATA_6
				+ " TEXT," + Company_data.DATA_7 + " TEXT,"
				+ Company_data.DATA_8 + " TEXT," + Company_data.DATA_9
				+ " TEXT" + ");");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + Tables.PERSONNEL + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ Personnel.ID + " TEXT," + Personnel.USER_NAME + " TEXT,"
				+ Personnel.PASSWORD + " TEXT," + Personnel.VALIDITY + " TEXT,"
				+ Personnel.DATA_2 + " TEXT," + Personnel.DATA_3 + " TEXT"
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
