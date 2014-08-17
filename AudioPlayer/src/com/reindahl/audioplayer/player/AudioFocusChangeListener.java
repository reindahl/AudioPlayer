package com.reindahl.audioplayer.player;

import android.media.AudioManager;
import android.util.Log;

import com.reindahl.audioplayer.BuildConfig;
import com.reindahl.audioplayer.Constants;

public class AudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener{
	private Player player;
	
	public AudioFocusChangeListener(Player player){
		this.player=player;
	}
	Boolean focusTransient =false;
	public void onAudioFocusChange(int focusChange) {

		switch (focusChange) {
		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
			player.AudioFocus=false;
			// Pause playback
			if (BuildConfig.DEBUG) {
				  Log.i(Constants.LOGPlayer, "Audio focus lost TRANSIENT");
			} 
			focusTransient=true;
			break;
		case AudioManager.AUDIOFOCUS_GAIN:
			player.AudioFocus=true;
			// Resume playback 
			if (focusTransient) {
//				player.StartPlaying();
				focusTransient=false;
			}
			
			break;
		case AudioManager.AUDIOFOCUS_LOSS:
			player.AudioFocus=false;
			if (BuildConfig.DEBUG) {
				  Log.i(Constants.LOGPlayer, "Audio focus lost");
			} 
			
			// Stop playback
			player.PausePlaying();
			
			//TODO: skift icon på play knappen
			break;
		default:
			break;
		}
		

	}

}

