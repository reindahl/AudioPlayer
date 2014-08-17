package com.reindahl.audioplayer.player;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SeekbarListener implements OnSeekBarChangeListener {
	AudioView audioView;
	
	public SeekbarListener(AudioView audioView) {
		this.audioView=audioView;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if(fromUser){
			audioView.player.seekTo(seekBar.getProgress());
		}
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		audioView.usingSeekbar=true;
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		audioView.usingSeekbar=false;
		
	}





}
