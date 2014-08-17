package com.reindahl.audioplayer.player;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.reindahl.audioplayer.BuildConfig;
import com.reindahl.audioplayer.Constants;
import com.reindahl.audioplayer.helper.Time;

public class Player implements MediaPlayer.OnCompletionListener{
	public enum PlayMode{shufle, list, once}
	boolean AudioFocus=false;

	PlayMode playMode;
	private LinkedList<String> previousSongs=new LinkedList<String>();
	//TODO: find a way to make sure that nextsongs are updated
	private LinkedList<String> nextSongs=new LinkedList<String>();
	private String playingSong;

	private AudioManager mAudioManager;
	private ComponentName mRemoteControlResponder;

	MediaPlayer currentMediaPlayer;
	//	private RemoteControlReceiver receiver = new RemoteControlReceiver();

	private AudioFocusChangeListener mAudioFocusChangeListener;


	public Player(Context mContext) {
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

		mRemoteControlResponder = new ComponentName(mContext.getPackageName(), RemoteControlReceiver.class.getName());

		//		IntentFilter filter = new IntentFilter();
		//		filter.addAction(Intent.ACTION_MEDIA_BUTTON);
		//		filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		//		filter.setPriority(1000);
		//		registerReceiver(receiver,filter);

		// Start listening for button presses
		mAudioManager.registerMediaButtonEventReceiver(mRemoteControlResponder);

		mAudioFocusChangeListener=new AudioFocusChangeListener(this);
		currentMediaPlayer=new MediaPlayer();
		//		mediaPlayer= MediaPlayer.create(mContext, R.raw.fallen_angels);

	}


	//cleans up and prepare the mediaplayer to play
	private void prepareTrack(String path){
		if(currentMediaPlayer.isPlaying()){
			currentMediaPlayer.stop();
		}
		//ensure nothing is playing and clean up
		currentMediaPlayer.reset();

		//prepare new audio
		try {
			currentMediaPlayer.setDataSource(path);
			currentMediaPlayer.prepare();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setTrack(String path, String... collection) {

		prepareTrack(path);

		//prepare following audio
		nextSongs.clear();
		for (int i = 0; i < collection.length; i++) {
			nextSongs.add(collection[i]);
		}


		// start playing
		playingSong=path;
		StartPlaying();

	}


	public void StartPlaying(){
		// Request audio focus for playback
		if(!AudioFocus){
			int result = mAudioManager.requestAudioFocus(mAudioFocusChangeListener,
					// Use the music stream.
					AudioManager.STREAM_MUSIC,
					// Request permanent focus.
					AudioManager.AUDIOFOCUS_GAIN);

			if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				if (BuildConfig.DEBUG) {
					Log.i(Constants.LOGPlayer, "did not gained audiofocus");
				} 
				return;
			}
		}
		// Start playback.
		currentMediaPlayer.start();
	}

	public void StopPlaying(){
		if (BuildConfig.DEBUG) {
			Log.i(Constants.LOGPlayer, "abandon audiofocus");
		} 
		// Stop playback
		if(currentMediaPlayer!=null){
			currentMediaPlayer.stop();
			currentMediaPlayer.reset();
		}
		// Abandon audio focus when playback complete    
		mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
		mAudioManager.unregisterMediaButtonEventReceiver(mRemoteControlResponder);

	}

	public void PausePlaying(){
		if (BuildConfig.DEBUG) {
			Log.i(Constants.LOGPlayer, "abandon audiofocus");
		} 
		// Abandon audio focus when playback complete  
		mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
		// Stop playback
		currentMediaPlayer.pause();
	}


	boolean repeat =false;
	public void Repeat(Boolean repeat){


		currentMediaPlayer.setLooping(repeat);
	}

	public void close(){
		// Stop listening for button presses
		//		unregisterReceiver(receiver);
		mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
		mAudioManager.unregisterMediaButtonEventReceiver(mRemoteControlResponder);
		if (BuildConfig.DEBUG) {
			Log.i(Constants.LOGPlayer, "player killed");
		} 
	}

	/**
	 * kills CurrentMediaplayer
	 * sets nextMediaplayer as current
	 * prepares a new nextMediaPlayer
	 */
	public String nextTrack(){
	

		String tmpTrack=nextSongs.pollFirst();
		if(tmpTrack==null){
			//no next song
			return playingSong;
		}
		if(currentMediaPlayer.isPlaying()){
			currentMediaPlayer.stop();
		}
		previousSongs.addFirst(playingSong);
		
		playingSong=tmpTrack;
		prepareTrack(playingSong);
		StartPlaying();
		return playingSong;
	}
	
	public String previousTrack(){

		String tmpTrack =previousSongs.pollFirst();
		if(tmpTrack==null){
			//no previous song
			return playingSong;
		}
		
		if(currentMediaPlayer.isPlaying()){
			currentMediaPlayer.stop();
		}
		nextSongs.addFirst(playingSong);
		playingSong=tmpTrack;
		prepareTrack(playingSong);
		StartPlaying();
		return playingSong;
	}


	@Override
	public void onCompletion(MediaPlayer mp) {
		nextTrack();
	}
	public int getCurrentPosition() {
		return currentMediaPlayer.getCurrentPosition();
	}
	public int getLenght() {

		return currentMediaPlayer.getDuration();
	}
	public void seekTo(int time) {
		if (BuildConfig.DEBUG) {
			Log.i(Constants.LOGPlayer, "seeking to "+ Time.Seconds.ToHMS(TimeUnit.MILLISECONDS.toSeconds(time)));
		} 
		currentMediaPlayer.seekTo(time);

	}


}