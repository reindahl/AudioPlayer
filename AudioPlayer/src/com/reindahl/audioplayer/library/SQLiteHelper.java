package com.reindahl.audioplayer.library;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.reindahl.audioplayer.BuildConfig;
import com.reindahl.audioplayer.Constants;
import com.reindahl.audioplayer.helper.Time;

public class SQLiteHelper extends SQLiteOpenHelper {
	//FIXME: when to close() db?


	// Database Version
	private static final int DATABASE_VERSION = 16;
	// Database Name
	private static final String DATABASE_NAME = "Library.db";

	private static final String TABLE_ARTWORK = "artwork";
	private static final String KEY_HASH = "hash";
	public static final String KEY_ARTPATH = "artpath";

	// Music table name
	private static final String TABLE_MUSIC = "music";

	// Music Table Columns names
	private static final String KEY_ID = "_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_ARTIST = "artist";
	public static final String KEY_ALBUM = "album";
	public static final String KEY_GENRE = "genre";
	public static final String KEY_COMMENT = "comment";
	public static final String KEY_PATH = "path";
	public static final String KEY_POSITION = "position";
	

	public static final String[] COLUMNS = {KEY_ID,KEY_TITLE,KEY_ARTIST, KEY_ALBUM, KEY_GENRE,KEY_PATH, KEY_HASH, KEY_POSITION};

	private Context context;
	
	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statement to create music table
		String CREATE_MUSIC_TABLE = "CREATE VIRTUAL TABLE "+TABLE_MUSIC+" USING fts4( " +
				KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				KEY_TITLE+" TEXT, "+
				KEY_ARTIST+" TEXT, "+
				KEY_ALBUM+" TEXT, "+
				KEY_GENRE+" TEXT, "+
				KEY_COMMENT+" TEXT, "+
				KEY_PATH+" TEXT, "+
				KEY_HASH+" INTEGER, "+
				KEY_POSITION+" INTEGER )";

		// create books table
		db.execSQL(CREATE_MUSIC_TABLE);


		// SQL statement to create artwork table
		String CREATE_ARTWORK_TABLE = "CREATE TABLE "+TABLE_ARTWORK+" ( " +
				KEY_HASH+" INTEGER PRIMARY KEY, " + 
				KEY_ARTPATH+" TEXT)";

		// create books table
		db.execSQL(CREATE_ARTWORK_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		bobbyDropTables(db);
	}

	//---------------------------------------------------------------------

	/**
	 * CRUD operations (create "add", read "get", update, delete) book + get all books + delete all books
	 */

	public void addMusic(Music music){
		//		if (BuildConfig.DEBUG) {
		//			Log.i(Constants.LOGSql, music.toString());
		//		}	  
		// 1. get reference to writable DB
		if (BuildConfig.DEBUG) {
			Log.i(Constants.LOGSql,"adding music "+music.getTitle());

		}
		if(music.getArtwork()!=null && !artworkExist(music.getHash())){
			try {
				File path=new File(context.getFilesDir(),music.getHash()+".jpeg");
//				FileUtils.writeByteArrayToFile(new File("//storage//sdcard1//"+music.getHash()+".jpeg"), music.getArtwork());	
				FileUtils.writeByteArrayToFile(path, music.ThubnailArtworkToBytes());
				addArtwork(music.getHash(), path.getPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(KEY_TITLE, music.getTitle()); // get title 
		values.put(KEY_ARTIST, music.getArtist()); // get artist
		values.put(KEY_ALBUM, music.getAlbum());
		values.put(KEY_GENRE, music.getGenre());
		values.put(KEY_COMMENT, music.getComment());
		values.put(KEY_PATH, music.getPath()); // get artist
		values.put(KEY_HASH, music.getHash());


		// 3. insert
		db.insert(TABLE_MUSIC, // table
				null, //nullColumnHack
				values); // key/value -> keys = column names/ values = column values

		// 4. close
		//		db.close(); 

	}
	public void addArtwork(int hash, String path){
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(KEY_HASH, hash); // get title 
		values.put(KEY_ARTPATH, path); // get artist

		db.insert(TABLE_ARTWORK, // table
				null, //nullColumnHack
				values); // key/value -> keys = column names/ values = column values
	}

	/**
	 * can be very memory consuming with a high number of files
	 * @param music
	 */
	public void addMusic(Music[] music){
		if (BuildConfig.DEBUG) {
			Log.i(Constants.LOGSql, "inserting music");
		}
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.beginTransaction();
			for (int i = 0; i < music.length; i++) {
				addMusic(music[i]);
			}
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(Constants.LOGSql, "insert error");
		}
		finally
		{
			db.endTransaction();
		}

		if (BuildConfig.DEBUG) {
			Log.i(Constants.LOGSql, "inserting done");
		}
	}


	public boolean artworkExist(int hash) {
		if (BuildConfig.DEBUG) {
			Log.d(Constants.LOGSql, "finding artwork "+hash);
		}

		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();

		// 2. build query
		Cursor cursor = 
				db.query(TABLE_ARTWORK, // a. table
						new String[] {KEY_HASH}, // b. column names
						KEY_HASH+" = ?", // c. selections 
						new String[] { hash+"" }, // d. selections args
						null, // e. group by
						null, // f. having
						null, // g. order by
						null); // h. limit

		// 3. if we got results get the first one
		if (cursor == null ||cursor.getCount() == 0){
			return false;
		}


		// 5. return book
		return true;
	}
	
	public Cursor getMusicCursor(String path) {
		if (BuildConfig.DEBUG) {
			Log.d(Constants.LOGSql, "finding music "+path);
		}

		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();
		
//		Cursor cursor = db.query(true, TABLE_MUSIC, COLUMNS, KEY_PATH+"=?", new String[]{path}, null, null, null, "1");
		
		String query = "SELECT  * FROM (SELECT * FROM "+TABLE_MUSIC+" WHERE "+KEY_PATH+" = ?) a" + " LEFT JOIN "+ TABLE_ARTWORK + " b ON a." + KEY_HASH + " = b." + KEY_HASH;

		
		return db.rawQuery(query, new String[]{path});
//		return cursor;
	}
	
	public Music getMusic(String path) {
		if (BuildConfig.DEBUG) {
			Log.d(Constants.LOGSql, "finding music "+path);
		}

		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();


		
		// 2. build query
		Cursor cursor = db.query(TABLE_MUSIC, // a. table
						COLUMNS, // b. column names
						KEY_PATH+" = ?", // c. selections 
						new String[] { path }, // d. selections args
						null, // e. group by
						null, // f. having
						null, // g. order by
						null); // h. limit

		// 3. if we got results get the first one
		if(cursor != null){
			cursor.moveToFirst();
		}

		// 4. build book object
		Music music = new Music();
		music.setTitle(cursor.getString(1));
		music.setArtist(cursor.getString(2));
		music.setPath(cursor.getString(3));

		// log
		if(BuildConfig.DEBUG){

			Log.d(Constants.LOGSql, music.toString());
		}
		// 5. return book
		return music;
	}


	public Cursor getAllMusicCursor() {

		// 1. build the query
		// String query = "SELECT  * FROM " + TABLE_MUSIC +
		// " ORDER BY "+KEY_TITLE+" LEFT JOIN "+
		// TABLE_ARTWORK+" USING "+KEY_HASH;
		String query = "SELECT  * FROM " + TABLE_MUSIC + " a LEFT JOIN "
				+ TABLE_ARTWORK + " b ON a." + KEY_HASH + "=b." + KEY_HASH+" ORDER BY "+KEY_ALBUM;
		// 2. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();
		return db.rawQuery(query, null);

	}

	public Cursor getAllArtistCursor() {

		// 1. build the query
		String query = "SELECT DISTINCT " + KEY_ARTIST + " as _id FROM "
				+ TABLE_MUSIC + " ORDER BY " + KEY_ARTIST;

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		return db.rawQuery(query, null);

	}

	public Cursor getAllPathsCursor() {

		// 1. build the query
		String query = "SELECT DISTINCT " + KEY_PATH + " FROM " + TABLE_MUSIC
				+ " ORDER BY " + KEY_PATH;

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		return db.rawQuery(query, null);

	}

	public int updateMusic(Music music) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put("title", music.getTitle()); // get title
		values.put("author", music.getArtist()); // get author

		// 3. updating row
		int i = db.update(TABLE_MUSIC, // table
				values, // column/value
				KEY_PATH + " = ?", // selections
				new String[] { String.valueOf(music.getPath()) }); // selection
																	// args

		// 4. close
		// db.close();
		if(BuildConfig.DEBUG){
			Log.i("rows updated ", i + "");
		}
		return i;

	}
	public int updatePositionMusic(String path, int position) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(KEY_POSITION, position);

		// 3. updating row
		int i = db.update(TABLE_MUSIC, // table
				values, // column/value
				KEY_PATH + " = ?", // selections
				new String[] { path }); // selection
																	// args

		// 4. close
		// db.close();
		if(BuildConfig.DEBUG){
			Log.i(Constants.LOGSql,"rows updated "+i + "");
			Log.i(Constants.LOGSql, "value updated "+Time.Seconds.ToHMS(TimeUnit.MILLISECONDS.toSeconds(position)));
		}
		return i;

	}
	public void deleteMusic(int id) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. delete
		db.delete(TABLE_MUSIC, // table name
				KEY_ID + " = ?", // selections
				new String[] { String.valueOf(id) }); // selections args

		// 3. close
		// db.close();

		// log
		if(BuildConfig.DEBUG){
			Log.i("delete music", id + "");
		}
	}

	public void deleteMusic(String path) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. delete
		db.delete(TABLE_MUSIC, // table name
				KEY_PATH + " = ?", // selections
				new String[] { path }); // selections args

		// 3. close
		// db.close();

		// log
		if(BuildConfig.DEBUG){
			Log.i(Constants.LOGSql, "delete " + path);
		}
	}

	public void deleteMusic(String[] paths) {
		if(BuildConfig.DEBUG){
			Log.i(Constants.LOGSql, "delete music");
		}
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			db.beginTransaction();
			for(int i = 0; i < paths.length; i++){
				deleteMusic(paths[i]);
			}
			db.setTransactionSuccessful();
		}catch(SQLException e){
			Log.e(Constants.LOGSql, "delete error");
		}finally{
			db.endTransaction();
		}

		if(BuildConfig.DEBUG){
			Log.i(Constants.LOGSql, "delete done");
		}
	}

	public void bobbyDropTables() {
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		bobbyDropTables(db);
	}

	public void bobbyDropTables(SQLiteDatabase db) {

		// Drop older tables if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MUSIC);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTWORK);

		for(File file: context.getFilesDir().listFiles()){
			if(!context.deleteFile(file.getName())){
				if(BuildConfig.DEBUG){
					Log.e(Constants.LOGSql,
							"Failed to delete " + file.getName());
				}
			}
		}

		// create fresh tables
		this.onCreate(db);

		// log
		if(BuildConfig.DEBUG){
			Log.i(Constants.LOGSql, "bobbyDropTables");
		}
	}

	public Cursor getAllAlbumsCursor() {

		// 1. build the query
		String query = "SELECT DISTINCT " + KEY_ALBUM + " as _id, "
				+ KEY_ARTIST + ", "+KEY_HASH+" FROM " + TABLE_MUSIC + " ORDER BY " + KEY_ID;

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		return db.rawQuery(joinArtwork(query), null);

	}

	public Cursor search(String selectionArg) {

		// FIXME: check for injections
		// FIXME: use a tokenizer to allow special charaters
		// TODO: limit search specific colums
		// 1. build the query
		String query = "SELECT  * FROM (SELECT * FROM "+TABLE_MUSIC+" WHERE "+TABLE_MUSIC+" MATCH ?) a" + " LEFT JOIN "+ TABLE_ARTWORK + " b ON a." + KEY_HASH + " = b." + KEY_HASH;
		
//		String query = "SELECT  * FROM " + TABLE_MUSIC + " WHERE "
//				+ TABLE_MUSIC + " MATCH ?" + " ORDER BY " + KEY_TITLE
//				+ " LEFT JOIN " + TABLE_ARTWORK + " USING " + KEY_HASH;

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		return db.rawQuery(query, new String[]{selectionArg+"*"});

	}

	public Cursor getAllbum(String album, String artist) {

		// 1. build the query
		String query = "SELECT * FROM " + TABLE_MUSIC + " WHERE " + KEY_ALBUM
				+ " = ? AND " + KEY_ARTIST + " = ?";

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		return db.rawQuery(query, new String[] { album, artist });
	}

	public Cursor getArtist(String artist) {
		// 1. build the query
		String query = "SELECT * FROM " + TABLE_MUSIC + " WHERE " + KEY_ARTIST
				+ " = ? ORDER BY " + KEY_ALBUM;

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		return db.rawQuery(joinArtwork(query), new String[] { artist });
	}

	public String joinArtwork(String query){
		
		return "SELECT  * FROM ("+query+") a" + " LEFT JOIN "+ TABLE_ARTWORK + " b ON a." + KEY_HASH + " = b." + KEY_HASH;
	}


}
