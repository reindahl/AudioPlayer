package com.reindahl.audioplayer.player;

import java.io.File;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.reindahl.audioplayer.BuildConfig;
import com.reindahl.audioplayer.Constants;
import com.reindahl.audioplayer.MainActivity;
import com.reindahl.audioplayer.R;
import com.reindahl.audioplayer.helper.Time;
import com.reindahl.audioplayer.library.Library;
import com.reindahl.audioplayer.library.SQLiteHelper;

public class AudioView extends Activity {
	Player player;
	SeekBar seekBar;
	TextView currentTime, endTime;
	Handler updateHandler= new Handler();
	int songLenght;

	Boolean usingSeekbar=false;

	private class UpdateSeekBar implements Runnable{

		@Override
		public void run() {
			int time = player.getCurrentPosition();
			if(!usingSeekbar){
				currentTime.setText(Time.Seconds.ToHMS(TimeUnit.MILLISECONDS.toSeconds(time)));
				seekBar.setProgress((int)time);
			}
			if(time<songLenght){
				updateHandler.postDelayed(this, 100);
			}
		}

	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_view);

		Intent intent = getIntent();
		String path=intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		setSongInfo(path);


		player=MainActivity.player;

		seekBar=(SeekBar) this.findViewById(R.id.seekBar);
		endTime=(TextView) this.findViewById(R.id.endTime);
		currentTime=(TextView) this.findViewById(R.id.currentTime);

		seekBar.setOnSeekBarChangeListener(new SeekbarListener(this));


		songLenght=player.getLenght();
		seekBar.setMax(songLenght);
		endTime.setText(Time.Seconds.ToHMS(TimeUnit.MILLISECONDS.toSeconds(songLenght)));
		UpdateSeekBar updateSeekBar =new UpdateSeekBar();
		updateHandler.postDelayed(updateSeekBar, 100);
	}


	private void setSongInfo(String path) {
		Cursor cursor=Library.db.getMusicCursor(path);
		cursor.moveToFirst();
		if(BuildConfig.DEBUG){
			Log.d(Constants.LOGPlayer, "setting music info"+path);
			Log.d(Constants.LOGPlayer, cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_TITLE)));
		}
		String artpath =cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ARTPATH));

		if(artpath!=null){
			File imgFile = new  File(artpath);
			Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			((ImageView) this.findViewById(R.id.CoverArtwork)).setImageBitmap(myBitmap);

		}
		((TextView)this.findViewById(R.id.Title)).setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_TITLE)));
		((TextView)this.findViewById(R.id.Artist)).setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ARTIST)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.audio_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			case R.id.action_settings:
				return true;

			default:
				return super.onOptionsItemSelected(item);

		}

	}


	private boolean playButton=true;

	public void play_pause(View view){
		ImageButton b=(ImageButton)view;

		if (playButton) {
			b.setImageResource(R.drawable.ic_action_play);
			player.PausePlaying();

		}else{
			b.setImageResource(R.drawable.ic_action_pause);
			player.StartPlaying();

		}
		playButton=!playButton;

	}

	boolean repeat =false;
	public void repeat(View view){
		ImageButton b=(ImageButton)view;
		repeat=!repeat;
		if (repeat) {
			b.setImageResource(R.drawable.ic_action_repeat2);
		}else{
			b.setImageResource(R.drawable.ic_action_repeat);


		}

		player.Repeat(repeat);

	}

	public void next(View view){
		setSongInfo(player.nextTrack());

		songLenght=player.getLenght();
		seekBar.setMax(songLenght);
		endTime.setText(Time.Seconds.ToHMS(TimeUnit.MILLISECONDS.toSeconds(songLenght)));
		UpdateSeekBar updateSeekBar =new UpdateSeekBar();
		updateHandler.postDelayed(updateSeekBar, 100);
	}
	public void previous(View view){
		setSongInfo(player.previousTrack());

		songLenght=player.getLenght();
		seekBar.setMax(songLenght);
		endTime.setText(Time.Seconds.ToHMS(TimeUnit.MILLISECONDS.toSeconds(songLenght)));
		UpdateSeekBar updateSeekBar =new UpdateSeekBar();
		updateHandler.postDelayed(updateSeekBar, 100);
	}
	public void shuffle(View view){

	}

	@Override
	public void onDestroy() {
		super.onDestroy();  // Always call the superclass

		// Stop method tracing that the activity started during onCreate()
		if (BuildConfig.DEBUG) {
			Log.i(Constants.LOG, "Audio View Destroyed");
		} 
	}
}
