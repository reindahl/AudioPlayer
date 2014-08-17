package com.reindahl.audioplayer.player;

import com.reindahl.audioplayer.BuildConfig;
import com.reindahl.audioplayer.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class RemoteControlReceiver extends BroadcastReceiver{


	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
			KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
			if(event.getAction()==KeyEvent.ACTION_DOWN){
				if (BuildConfig.DEBUG) {
					  Log.i(Constants.LOG, "Remote key "+event.getKeyCode()+" Down pressed");
				} 
				return;
			}
			switch (event.getKeyCode()) {
			
			case KeyEvent.KEYCODE_MEDIA_PLAY:
				// Handle key press.
				if (BuildConfig.DEBUG) {
					  Log.i(Constants.LOG, "Remote Play pressed");
				} 
				
				break;
			case KeyEvent.KEYCODE_MEDIA_PAUSE:
				// Handle key press.
				if (BuildConfig.DEBUG) {
					  Log.i(Constants.LOG, "Remote Pause pressed");
				} 
				break;
			case KeyEvent.KEYCODE_HEADSETHOOK:
//			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
				
				// Handle key press.
				if (BuildConfig.DEBUG) {
					  Log.i(Constants.LOG, "Remote Play/Pause pressed");
				} 
				
				break;			
			default:
				Log.i(Constants.LOG, "Remote unknown button pressed: "+event.getKeyCode());
				break;
			}

		}

	}
}
