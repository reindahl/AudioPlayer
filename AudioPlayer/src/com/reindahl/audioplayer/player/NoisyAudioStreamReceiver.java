package com.reindahl.audioplayer.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

/**
 * 
 * @author Kasper Reindahl Rasmussen
 * 
 *	Receives notice when changing from headphones to speakers
 */
public class NoisyAudioStreamReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            // Pause the playback
        	
        }
    }
	
}