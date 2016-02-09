package com.example.movieproject;

import android.R.string;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbOpenHelper extends SQLiteOpenHelper {
	public final static String TAG = "DbOpenHelper";
	
	public static String DB_NAME = "movies.db";
	public static int DB_VERSION = 5;
	
	public DbOpenHelper(Context context){
		super(context, DB_NAME, null, DB_VERSION);
	}

	public void onCreate(SQLiteDatabase db){
		Log.d(TAG, "creating database");
		String sql = ""
		+ " CREATE TABLE " + DbConstants.TABLE_NAME_MOVIE	+ " ( "
		+ DbConstants.MOVIE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
		+ DbConstants.MOVIE_NAME + " TEXT,"
		+ DbConstants.MOVIE_DESCREPTION + " TEXT,"
		+ DbConstants.MOVIE_URL + " TEXT,"
		+ DbConstants.MOVIE_IMDBID + " TEXT"
		+ ")";
		Log.d(TAG, sql);
		db.execSQL(sql);
		
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG,"updating database from " + oldVersion + " to " + newVersion);
		String sql = "DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_MOVIE;
		Log.d(TAG, sql);
		db.execSQL(sql);
		onCreate(db);
	
	}
}
