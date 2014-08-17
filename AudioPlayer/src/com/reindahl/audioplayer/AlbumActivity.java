package com.reindahl.audioplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reindahl.audioplayer.library.Library;
import com.reindahl.audioplayer.library.SQLiteHelper;
import com.reindahl.audioplayer.player.AudioView;

public class AlbumActivity extends Activity {
	private String[] paths;
	
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_album);
		Intent intent = getIntent();
		String album = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		String artist = intent.getStringExtra(MainActivity.EXTRA_MESSAGE2);
		if(album.length()>0){
			setTitle(album);
		}else{
			setTitle("blah");
		}
		Cursor cursor=Library.db.getAllbum(album, artist);
		LinearLayout songs =(LinearLayout) findViewById(R.id.Songs);
		paths=new String[cursor.getCount()];
		int i=0;
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
			paths[i++]=cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_PATH));
			
	        View song = LayoutInflater.from(this).inflate(R.layout.item_song, null); 
	        TextView title=(TextView) song.findViewById(R.id.listTitle);
	        title.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_TITLE)));
	        
	  
	        
	        song.setTag(R.string.key_file, cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_PATH)));
	        songs.addView(song);
		}
		
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.album, menu);
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
		MainActivity.player.setTrack(path, paths);
	
    	Intent intent = new Intent(this, AudioView.class);
    	intent.putExtra(MainActivity.EXTRA_MESSAGE, path);
    	startActivity(intent);

	}
}
