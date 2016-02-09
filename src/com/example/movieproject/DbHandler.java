package com.example.movieproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbHandler {
	final static String TAG = "DbManager";
	
	private DbOpenHelper dbOpenHelper;
	
	public DbHandler(Context context) {
		dbOpenHelper = new DbOpenHelper(context);
	}
	
	//////
	//DATA BASE:
	
	public void insert(Movie movie){
		Log.d(TAG + "insert", "insert start");
		//get a WRITABLE database instance:
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		
		// the values to insert:
		ContentValues values = new ContentValues();
		values.put(DbConstants.MOVIE_NAME, movie.getTitle());
		values.put(DbConstants.MOVIE_DESCREPTION, movie.getDescreption());
		values.put(DbConstants.MOVIE_URL, movie.getUrl());
		//values.put(DbConstants.MOVIE_IMDBID, movie.getImdbId());
		Log.d(TAG + "insert", "values ok");
		//insert
		//db.insertOrThrow(table, nullColumnHack, values)
		db.insertOrThrow(DbConstants.TABLE_NAME_MOVIE, null, values);
		db.close();

	}
	
	public void update(Movie movie){
		//get a WRITABLE database instance:
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		
		// the values to insert:
		ContentValues values = new ContentValues();
		values.put(DbConstants.MOVIE_NAME, movie.getTitle());
		values.put(DbConstants.MOVIE_ID, movie.getId());
		values.put(DbConstants.MOVIE_DESCREPTION, movie.getDescreption());
		values.put(DbConstants.MOVIE_URL, movie.getUrl());
		
		//insert
		//db.update(table, values, whereClause, whereArgs)
		db.updateWithOnConflict(DbConstants.TABLE_NAME_MOVIE, 
				values, 
				DbConstants.MOVIE_ID + " =?",
				new String[]{String.valueOf(movie.getId())},
				SQLiteDatabase.CONFLICT_REPLACE);
		
		// writable databases must be closed!
		db.close();
	}
	
	public void delete(long id) {
		//get a WRITABLE database instance:
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		
		//delete
		//db.delete(table, whereClause, whereArgs)
		db.delete(DbConstants.TABLE_NAME_MOVIE, DbConstants.MOVIE_ID+"=?",new String[]{String.valueOf(id)});
		Log.d(TAG, "delete happend");
		// writable databases must be closed!
		db.close();
	}
	public void deleteAll(){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		
		db.delete(DbConstants.TABLE_NAME_MOVIE, null ,null);
		Log.d(TAG, "delete all happend");

		db.close();
	}
	
	
	public Cursor queryAll(){
		//get a READABLE database instance:
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		
		//query all:
		//db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		Cursor cursor = db.query(DbConstants.TABLE_NAME_MOVIE, null, null, null, null, null, DbConstants.MOVIE_NAME + " COLLATE LOCALIZED ASC");
		return cursor ;
	}
	
	public Movie query(long id){
		//get a READABLE database instance:
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

		//query one (where _id=id):
		//db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		Cursor cursor = db.query(DbConstants.TABLE_NAME_MOVIE, null, DbConstants.MOVIE_ID+"=?", new String[]{String.valueOf(id)}, null, null, null);
		
		Log.d(TAG,"count = " + cursor.getCount());
		
		Movie movie = null; //for now
		
		//get the data:
		if (cursor.moveToNext()){
			String name = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_NAME));
			int _id = (int) cursor.getFloat(cursor.getColumnIndex(DbConstants.MOVIE_ID));
			String descreption = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_DESCREPTION));
			String url = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_URL));
			
			movie = new Movie(name, descreption, url);
			//remember the id :
			movie.setId(_id);
			
		}
		
		//cursors must be closed!
		cursor.close();
		
		//return the student
		return movie;
	}
}
