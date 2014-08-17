package com.reindahl.audioplayer.library;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.io.FilenameUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import com.reindahl.audioplayer.BuildConfig;
import com.reindahl.audioplayer.Constants;
import com.reindahl.audioplayer.helper.Time;
import com.reindahl.audioplayer.helper.Timer;

public class Library {
	private static Context context;
	private static String root;
	private static String preferencesSettings ="com.reindahl.audioplayer.root";

	public static SQLiteHelper db;

	private static ProgressDialog barProgressDialog;
	private static Handler updateHandler = new Handler();

	public static void init(Context context){
		Library.context=context;
		SharedPreferences preferences= context.getSharedPreferences(preferencesSettings, Context.MODE_PRIVATE);
		root=preferences.getString("root", "//.");

		db= new SQLiteHelper(context);
	}


	public static void setRoot(String root){


		SharedPreferences preferences= context.getSharedPreferences(preferencesSettings, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("root", root);
		editor.apply();
		Library.root=root;

		updateLib(true);
		
	}

	public static String getRoot(){
		return new String(root);
	}


	private static void updateLib(Boolean blocking){

		barProgressDialog=new ProgressDialog(context);

		barProgressDialog.setTitle("Loading library ...");
		barProgressDialog.setMessage("in progress ...");
		barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		barProgressDialog.setProgress(0);

		if(blocking){
			barProgressDialog.show();
		}
		class UpdateSlave extends Thread {
			public UpdateSlave() {
				super();
			}
			public void run() {


				ArrayList<String> mediaFiles= new ArrayList<String>();
				if (BuildConfig.DEBUG) {
					Log.i(Constants.LOGFiles, "explorering");
				}
				explore(new File(root), mediaFiles);
				if (BuildConfig.DEBUG) {
					Log.i(Constants.LOGFiles, "Found "+mediaFiles.size()+" files");
				}
				barProgressDialog.setMax(mediaFiles.size());
				db.bobbyDropTables();
//				Music[] music=new Music[mediaFiles.size()];
				
				Timer timer=Time.Timer();
//				Time.Timer timer= new Timer();
				timer.start();
				for (int i = 0; i <mediaFiles.size(); i++) {
					db.addMusic(new Music(mediaFiles.get(i)));

//					music[i]=new Music(mediaFiles.get(i));
					updateHandler.post(new Runnable() {
						public void run() {
							barProgressDialog.incrementProgressBy(1);
						}
					});
					if (BuildConfig.DEBUG) {
						Log.i(Constants.LOGFiles, "file added "+timer.difference()+"\n"+mediaFiles.get(i));

					}
				}
//				db.bobbyDropTables();
//				db.addMusic(music);
				if (BuildConfig.DEBUG) {
					Log.i(Constants.LOGFiles, "file added "+Time.NanoSeconds.ToHMS((long)timer.time()));
				}
				barProgressDialog.dismiss();
				updateHandler.post(new Runnable() {
					public void run() {
						notifyAdapters();
					}
				});
			}
		}
		UpdateSlave update=new UpdateSlave();
		update.start();
		
	}
	public static void rescanLibrary(Boolean blocking){
		class UpdateRunnable implements Runnable{
			String message;
			public UpdateRunnable(String message){
				this.message=message;
			}
			@Override
			public void run() {
				barProgressDialog.setMessage(message);

			}

		}
		barProgressDialog=new ProgressDialog(context);

		barProgressDialog.setTitle("Refreshing library");
		barProgressDialog.setMessage("in progress ...");
		barProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		if(blocking){
			barProgressDialog.show();
		}
		class UpdateSlave extends Thread {
			public UpdateSlave() {
				super();
			}
			public void run() {
				ArrayList<String> mediaFiles=new ArrayList<String>();
				if (BuildConfig.DEBUG) {
					Log.i(Constants.LOGFiles, "explorering");
				}
				updateHandler.post(new UpdateRunnable("Exploring"));
				explore(new File(root), mediaFiles);

				Cursor cursor= db.getAllPathsCursor();
				ArrayList<String> libraryPaths =new ArrayList<String>(cursor.getCount());
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					libraryPaths.add(cursor.getString(0));
				}

				updateHandler.post(new UpdateRunnable("Comparing found files"));
				ArrayList<String> newPaths=new ArrayList<String>();
				ArrayList<String> deletedPaths=new ArrayList<String>();

				arrayListDifference(mediaFiles, libraryPaths, newPaths, deletedPaths);

				String message= "Deleting files";
				updateHandler.post(new UpdateRunnable(message));
				if (BuildConfig.DEBUG) {
					Log.i(Constants.LOGFiles, message);
				}


				updateHandler.post(new UpdateRunnable("Updating database"));
				db.deleteMusic(deletedPaths.toArray(new String[0]));
				
				message= "getting metadata";
				updateHandler.post(new UpdateRunnable(message));
				if (BuildConfig.DEBUG) {
					Log.i(Constants.LOGFiles, message);
				}

//				Music[] music =new Music[newPaths.size()];
				for(int k=0;k<newPaths.size();k++){
					updateHandler.post(new UpdateRunnable(message+k+1+" of "+newPaths));
//					music[k]=new Music(newPaths.get(k));
					db.addMusic(new Music(newPaths.get(k)));
				}
//				db.addMusic(music);
				barProgressDialog.dismiss();
				updateHandler.post(new Runnable() {
					public void run() {
						notifyAdapters();
					}
				});

			}
		}
		UpdateSlave update=new UpdateSlave();
		update.start();


	}
	/**
	 * finds the differences between a and b
	 * sorts a and b
	 * @param a
	 * @param b
	 * @param difA
	 * @param difB
	 */
	public static void arrayListDifference(ArrayList<String> a, ArrayList<String> b, ArrayList<String> difA, ArrayList<String> difB){
		if(a.isEmpty()){
			for (String string:b) {
				difB.add(string);
			}
			Collections.sort(difB);
			return;
		}else if(b.isEmpty()){
			for (String string:a) {
				difA.add(string);
			}
			Collections.sort(difA);
			return;
		}

		Collections.sort(a);
		Collections.sort(b);

		boolean done=false;
		int i=0;
		int j=0;
		while(!done){
			//compare arrays
			int result=b.get(i).compareTo(a.get(j));
			if(result<0){
				difB.add(b.get(i));
				i++;
			}else if(result==0){
				i++;
				j++;
			}else{
				difA.add(a.get(j));
				j++;
			}
			if(i==b.size()){
				for (; j < a.size(); j++) {
					difA.add(a.get(j));
				}
				done=true;
			}else if(j==a.size()){
				for (; i < b.size(); i++) {
					difB.add(b.get(i));
				}
				done=true;
			}
		}
	}

	private static void explore(File file, ArrayList<String> mediaFiles){
		if (BuildConfig.DEBUG) {
			Log.i(Constants.LOGFiles, file.getPath());
		}
		if(file.isDirectory()){
			File[] files= file.listFiles();
			for (int i = 0; i < files.length; i++) {
				explore(files[i], mediaFiles);
			}
		}else if(file.isFile() && isAudioFile(file)){
			mediaFiles.add(file.getPath());
		}
	}

	private static boolean isAudioFile(File file) {
		if(Constants.audioFiles.contains(FilenameUtils.getExtension(file.getName()))){
			return true;
		}else{
			return false;
		}
	}

	private static void notifyAdapters(){
//		FIXME null pointer exception
//		SongsFragment.adapter.notifyDataSetChanged();
//		AlbumsFragment.adapter.notifyDataSetChanged();
//		ArtistsFragment.adapter.notifyDataSetChanged();
	}


}
