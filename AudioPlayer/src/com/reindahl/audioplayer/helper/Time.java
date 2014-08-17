package com.reindahl.audioplayer.helper;

import java.util.concurrent.TimeUnit;

public class Time {


public static class NanoSeconds{
		
		public static String ToHMS(long nano){
			int mi= (int) (nano%1000000);
			int h=(int) ((nano/1000000000) / 3600 );
			int m=(int) (((nano/1000000000) % 3600) / 60);
			int s=(int) ((nano/1000000000) % 60);
			
			
			return h+ ":" + (m<10?"0":"") + m + ":" + (s<10?"0":"") + s+ (mi<100?"0":"") + (mi<10?"0":"") + mi;
		}
		
		public static double ToSeconds(long nano){
			
			return nano/1000000000.0;
		}
	}
	
	public static class MilliSeconds{
		
		public long ToSeconds(int MilliSeconds){
			return TimeUnit.MILLISECONDS.toSeconds(MilliSeconds);
		}
		
		public static String ToHMS(long MilliSeconds){
			long mi= MilliSeconds%1000;
			long s=(MilliSeconds/1000) % 60;
			long m=((MilliSeconds/1000) % 3600) / 60;
			long h= ((MilliSeconds/1000) / 3600 );
			
			
			
			
			return h+ ":" + (m<10?"0":"") + m + ":" + (s<10?"0":"") + s+ (mi<100?"0":"") + (mi<10?"0":"") + mi;
		}
	}
	
	public static class Seconds{
		
		public static String ToHMS(long seconds){
			
			long m=((seconds % 3600) / 60);
			long s=(seconds % 60);
			
			return (seconds / 3600 )+ ":" + (m<10?"0":"") + m + ":" + (s<10?"0":"") + s;
		}
	}
	public static Timer Timer(){
		return new Timer();
	}
}
//class Timer {
//	private long start;
//	private ArrayList<Long> time= new ArrayList<Long>();
//	
//	
//
//	
//	public void start(){
//		start = System.nanoTime(); 
//	}
//
//	/**
//	 * records the current time
//	 * @return
//	 */
//	public double time(){
//		time.add(System.nanoTime()-start);
//		return Time.NanoSeconds.ToSeconds(time.get(time.size()-1));
//	}
//
//	public double difference(){
//		time.add(System.nanoTime()-start);
//		if(time.size()>=2){
//			return Time.NanoSeconds.ToSeconds(time.get(time.size()-1)-time.get(time.size()-2));
//		}else{
//			return Time.NanoSeconds.ToSeconds(time.get(time.size()-1));
//		}
//	}
//	
//	public ArrayList<Long> getTimes(){
//		return new ArrayList<Long>(time);
//	}
//	
//	public String toSring(){
//		return time.size()>0? Time.NanoSeconds.ToHMS(time.get(time.size()-1)):"Time not registered";
//	}
//}