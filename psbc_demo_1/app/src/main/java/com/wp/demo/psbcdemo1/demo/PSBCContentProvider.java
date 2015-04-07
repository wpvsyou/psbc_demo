package com.wp.demo.psbcdemo1.demo;

import android.content.*;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.wp.demo.psbc.count.PSBCCount;
import com.wp.demo.psbc.count.PSBCCount.*;

public class PSBCContentProvider extends ContentProvider {

	public static final String AUTHORITY = PSBCCount.DATABASE_NAME;
	public static final String COMPANY_DATA_TYPE = "vnd.android.cursor.dir/"
			+ Tables.COMPANY_DATA;
	public static final String PERSONNEL_TYPE = "vnd.android.cursor.dir/"
			+ Tables.PERSONNEL;
	private static final int COMPANY_DATA = 1;
	private static final int PERSONNEL = 2;
	private static final UriMatcher sUriMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		sUriMatcher.addURI(AUTHORITY, Tables.COMPANY_DATA, COMPANY_DATA);
		sUriMatcher.addURI(AUTHORITY, Tables.PERSONNEL, PERSONNEL);
	}

	private PSBCDatabaseHelper mDbHelper;

	@Override
	public boolean onCreate() {
		mDbHelper = PSBCDatabaseHelper.getInstance(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		int i = sUriMatcher.match(uri);
		if (i == COMPANY_DATA) {
			return COMPANY_DATA_TYPE;
		} else if (i == PERSONNEL) {
			return PERSONNEL_TYPE;
		} else {
			throw new IllegalArgumentException("Unkwon uri: " + uri.toString());
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		int i = sUriMatcher.match(uri);
		String limit = getLimit(uri);
		if (i == PERSONNEL) {
			qb.setTables(PSBCCount.Tables.PERSONNEL);
		} else if (i == COMPANY_DATA) {
			qb.setTables(PSBCCount.Tables.COMPANY_DATA);
		}
		Cursor cursor = qb.query(db, projection, selection, selectionArgs,
				null, null, null, limit);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		long id;
		int i = sUriMatcher.match(uri);
		if (i == COMPANY_DATA) {
			id = db.insert(Tables.COMPANY_DATA, null, values);
		} else if (i == PERSONNEL) {
			id = db.insert(Tables.PERSONNEL, null, values);
		} else {
			throw new IllegalArgumentException("Unkwon uri: " + uri.toString());
		}
		if (id > 0) {
			notifyDataChange(uri, null);
			return ContentUris.withAppendedId(uri, id);
		}
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int count = 0;
		String append = null;
		int i = sUriMatcher.match(uri);
		if (i == PERSONNEL) {
			append = BaseColumns._ID + "=" + ContentUris.parseId(uri);
			if (!TextUtils.isEmpty(selection)) {
				selection = append + " AND (" + selection + ")";
			} else {
				selection = append;
			}
			count = db.update(Tables.COMPANY_DATA, values, selection,
					selectionArgs);
		} else if (i == COMPANY_DATA) {
			count = db.update(Tables.COMPANY_DATA, values, selection,
					selectionArgs);
		} else {
			throw new IllegalArgumentException("Unkwon uri: " + uri.toString());
		}
		notifyDataChange(uri, null);
		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int count = 0;
		String append = null;
		int i = sUriMatcher.match(uri);
		if (i == PERSONNEL) {
			append = BaseColumns._ID + "=" + ContentUris.parseId(uri);
			if (!TextUtils.isEmpty(selection)) {
				selection = append + " AND (" + selection + ")";
			} else {
				selection = append;
			}
			count = db.delete(Tables.PERSONNEL, selection, selectionArgs);
		} else if (i == COMPANY_DATA) {
            count = db.delete(Tables.COMPANY_DATA, selection, selectionArgs);
        } else {
			throw new IllegalArgumentException("Unkwon uri: " + uri.toString());
		}
		notifyDataChange(uri, null);
		return count;
	}

	private String getLimit(Uri uri) {
		String limitParam = getQueryParameter(uri,
				ContactsContract.LIMIT_PARAM_KEY);
		if (limitParam == null) {
			return null;
		}
		// make sure that the limit is a non-negative integer
		try {
			int l = Integer.parseInt(limitParam);
			if (l < 0) {
				return null;
			}
			return String.valueOf(l);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	/**
	 * A fast re-implementation of {@link Uri#getQueryParameter}
	 */
	/* package */
	static String getQueryParameter(Uri uri, String parameter) {
		String query = uri.getEncodedQuery();
		if (query == null) {
			return null;
		}

		int queryLength = query.length();
		int parameterLength = parameter.length();

		String value;
		int index = 0;
		while (true) {
			index = query.indexOf(parameter, index);
			if (index == -1) {
				return null;
			}

			// Should match against the whole parameter instead of its suffix.
			// e.g. The parameter "param" must not be found in "some_param=val".
			if (index > 0) {
				char prevChar = query.charAt(index - 1);
				if (prevChar != '?' && prevChar != '&') {
					// With "some_param=val1&param=val2", we should find second
					// "param" occurrence.
					index += parameterLength;
					continue;
				}
			}

			index += parameterLength;

			if (queryLength == index) {
				return null;
			}

			if (query.charAt(index) == '=') {
				index++;
				break;
			}
		}

		int ampIndex = query.indexOf('&', index);
		if (ampIndex == -1) {
			value = query.substring(index);
		} else {
			value = query.substring(index, ampIndex);
		}

		return Uri.decode(value);
	}

	private void notifyDataChange(Uri uri, ContentObserver observer) {
		getContext().getContentResolver().notifyChange(uri, observer);
	}
}
