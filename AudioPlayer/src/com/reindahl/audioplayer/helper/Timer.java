package com.reindahl.audioplayer.helper;

import java.util.ArrayList;

public class Timer {
	private long start;
	private ArrayList<Long> time= new ArrayList<Long>();
	
	protected Timer(){
		
	}

	
	public void start(){
		start = System.nanoTime(); 
	}

	/**
	 * records the current time
	 * @return
	 */
	public double time(){
		time.add(System.nanoTime()-start);
		return Time.NanoSeconds.ToSeconds(time.get(time.size()-1));
	}

	public double difference(){
		time.add(System.nanoTime()-start);
		if(time.size()>=2){
			return Time.NanoSeconds.ToSeconds(time.get(time.size()-1)-time.get(time.size()-2));
		}else{
			return Time.NanoSeconds.ToSeconds(time.get(time.size()-1));
		}
	}
	
	public ArrayList<Long> getTimes(){
		return new ArrayList<Long>(time);
	}
	
	public String toSring(){
		return time.size()>0? Time.NanoSeconds.ToHMS(time.get(time.size()-1)):"Time not registered";
	}
}
