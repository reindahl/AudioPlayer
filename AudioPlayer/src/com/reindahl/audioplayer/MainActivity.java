package com.reindahl.audioplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import org.jaudiotagger.tag.TagOptionSingleton;

import com.reindahl.audioplayer.fileExplore.FileExploreActivity;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.reindahl.audioplayer.library.Library;
import com.reindahl.audioplayer.player.AudioView;
import com.reindahl.audioplayer.player.Player;
import com.reindahl.audioplayer.player.Player.PlayMode;


@SuppressWarnings("unused")
public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.reindahl.audioplayer.MESSAGE";
	public final static String EXTRA_MESSAGE2 = "com.reindahl.audioplayer.MESSAGE2";
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	public static Player player;
	private static boolean instantiated =false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TagOptionSingleton.getInstance().setAndroid(true);
		//FIXME should not create new objects when called after first time
		if(!instantiated){
			instantiated=true;
			Library.init(this);
			player= new Player(this);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (BuildConfig.DEBUG) {
			Log.i(Constants.LOG, "main created");
		} 



		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);




	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_settings:
			if (BuildConfig.DEBUG) {
				Log.i(Constants.LOG, "Settings");
			}
			Intent intent = new Intent(this, FileExploreActivity.class);
			startActivity(intent); 
			return true;
		case R.id.action_search:
			if (BuildConfig.DEBUG) {
				Log.i(Constants.LOG, "Search");
			}
			onSearchRequested();
			return true;
		case R.id.action_rescan:
			if (BuildConfig.DEBUG) {
				Log.i(Constants.LOG, "Rescan Library");
			}
			Library.rescanLibrary(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}

	}



	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).
			switch (position) {
			case 0:
				return new SongsFragment();
			case 1:
				return new AlbumsFragment();
			case 2:
				return new ArtistsFragment();
			default:
				return null;

			}

		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_Songs).toUpperCase(l);
			case 1:
				return getString(R.string.title_Albums).toUpperCase(l);
			case 2:
				return getString(R.string.title_Artist).toUpperCase(l);
			default:
				return "unkown";
			}

		}
	}



	//called when song item is pressed
	public void selectedSong(View view){
		String path =(String)view.getTag(R.string.key_file);
		if (BuildConfig.DEBUG) {
			Log.i(Constants.LOG, "file selected "+path);
		}
		//lets start the music!!!		
		player.setTrack(path);
		player.StartPlaying();

		Intent intent = new Intent(this, AudioView.class);
		intent.putExtra(EXTRA_MESSAGE, path);
		startActivity(intent);

	}

	//called when album item is pressed
	public void selectedAlbum(View view){
		String message = (String) view.getTag(R.string.key_album);
		String message2 = (String) view.getTag(R.string.key_artist);
		if (BuildConfig.DEBUG) {
			Log.i(Constants.LOG,new String( "album selected "+message + " by "+ message2));
		}

		Intent intent = new Intent(this, AlbumActivity.class);
		intent.putExtra(EXTRA_MESSAGE, message);
		intent.putExtra(EXTRA_MESSAGE2, message2);
		startActivity(intent);
	}
	//called when album item is pressed
	public void selectedArtist(View view){
		String message = (String) view.getTag(R.string.key_artist);
		if (BuildConfig.DEBUG) {
			Log.i(Constants.LOG, "file selected "+message);
		}

		Intent intent = new Intent(this, ArtistActivity.class);
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();  // Always call the superclass

		// Stop method tracing that the activity started during onCreate()
		player.close();
		if (BuildConfig.DEBUG) {
			Log.i(Constants.LOG, "main View Destroyed");
		} 
	}
}
