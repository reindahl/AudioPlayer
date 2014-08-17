package com.reindahl.audioplayer;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reindahl.audioplayer.library.Library;
import com.reindahl.audioplayer.library.SQLiteHelper;
import com.reindahl.audioplayer.player.AudioView;

public class ArtistActivity extends Activity {

	private String[] paths;
	
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
		
		Intent intent = getIntent();
		String artist = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		if(artist.length()>0){
			setTitle(artist);
		}else{
			setTitle("unknown");
		}
		Cursor cursor=Library.db.getArtist(artist);
		LinearLayout songs =(LinearLayout) findViewById(R.id.Songs);
		
		paths=new String[cursor.getCount()];
		int i=0;
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
			paths[i++]=cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_PATH));

	        View song = LayoutInflater.from(this).inflate(R.layout.item_song, null); 
	        TextView title=(TextView) song.findViewById(R.id.listTitle);
	        title.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_TITLE)));
	       
	        String path =cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ARTPATH));
			
			if(path!=null){
				File imgFile = new  File(path);
			    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

			    ImageView coverart=(ImageView) song.findViewById(R.id.listCover);
			    coverart.setImageBitmap(myBitmap);

			}
	        
	        song.setTag(R.string.key_file, cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_PATH)));
	        songs.addView(song);
		}
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.artist, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
    //called when song item is pressed
	public void selectedSong(View view){
		//TODO: do something different
		
		String path =(String)view.getTag(R.string.key_file);
		if (BuildConfig.DEBUG) {
			Log.i(Constants.LOG, "file selected "+path);
		}

		//lets start the music!!!		
		
		MainActivity.player.setTrack(path,paths);
	
    	Intent intent = new Intent(this, AudioView.class);
    	intent.putExtra(MainActivity.EXTRA_MESSAGE, path);
    	startActivity(intent);

	}
}
