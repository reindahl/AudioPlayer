package com.reindahl.audioplayer.fileExplore;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.reindahl.audioplayer.BuildConfig;
import com.reindahl.audioplayer.Constants;
import com.reindahl.audioplayer.library.Library;
import com.reindahl.audioplayer.R;

public class FileExploreActivity extends Activity implements OnClickListener {

	boolean hasSDCard;
	File sdcard;
	File home;
	File path;
	File[] files;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_explore);

		if (BuildConfig.DEBUG) {
			Log.i(Constants.LOGFiles, Environment.getExternalStorageDirectory()+"");
		} 

		//TODO: check if storage is accessible
		if(!(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)||Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY))){
			Log.wtf(Constants.LOGFiles, "no read acces to storage");
			Log.wtf(Constants.LOGFiles, Environment.getExternalStorageState());
		}
		sdcard = new File("//storage//sdcard1");
		if(!sdcard.exists()){
			LinearLayout fileLayout= (LinearLayout) findViewById(R.id.FileLayout);
			fileLayout.removeView((ImageButton) findViewById(R.id.SDCard));
		}
		path=Environment.getExternalStorageDirectory();
		home=path;

		newLevel(home);

	}

	public boolean newLevel(File path){
		if(path.isDirectory()){

			((TextView) findViewById(R.id.Path)).setText(path.toString());
			LinearLayout fileLayout= (LinearLayout) findViewById(R.id.FileLayout);
			fileLayout.removeAllViews();
			files=path.listFiles();
			Arrays.sort(files, new Comparator<File>() {        
				@Override
				public int compare(File o1, File o2) {
					return o1.getPath().compareToIgnoreCase(o2.getPath());
				}
			});


			for (int i = 0; i < files.length; i++) {
				LinearLayout item =new LinearLayout(getApplicationContext());

				ImageView image=new ImageView(getApplicationContext());
				image.setPadding(0, 0, 3, 0);
				if(files[i].isDirectory()){
					image.setImageResource(R.drawable.folder);
				}else{
					image.setImageResource(R.drawable.file);
				}

				LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				layoutParams.gravity=Gravity.CENTER_VERTICAL;
				image.setLayoutParams(layoutParams);

				item.addView(image);

				TextView file = new TextView(getApplicationContext());
				file.setText(files[i].getName());
				file.setTag(R.string.key_type, "path");
				file.setTag(R.string.key_file, files[i]);
				file.setOnClickListener(this);
				file.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Holo_Medium);
				layoutParams=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT,1);
				layoutParams.gravity=Gravity.CENTER_VERTICAL;
				file.setLayoutParams(layoutParams);

				item.addView(file);



				ImageView add=new ImageView(getApplicationContext());
				add.setPadding(3, 0, 0, 0);
				add.setImageResource(R.drawable.add);
				layoutParams=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				layoutParams.gravity=Gravity.CENTER_VERTICAL;
				add.setLayoutParams(layoutParams);
				add.setTag(R.string.key_type, "add");
				add.setTag(R.string.key_file, files[i]);

				add.setOnClickListener(this);

				item.addView(add);



				fileLayout.addView(item);

			}
			if (BuildConfig.DEBUG) {
				Log.i(Constants.LOGFiles, Arrays.toString(files));
			}
		}else{
			if (BuildConfig.DEBUG) {
				Log.i(Constants.LOGFiles, "is file? "+path.isFile());
			}
			return false;
		}
		this.path=path;
		return true;
	}
	@Override
	public void onClick(View view){
		String type = (String) view.getTag(R.string.key_type);
		if(type.equals("path")){
			newLevel((File)view.getTag(R.string.key_file));
		}else if(type.equals("add")){
			Library.setRoot(((File)view.getTag(R.string.key_file)).toString());
			finish();
		}

	}

	public void up(View view){
		if(path.getParentFile()!=null){
			newLevel(path.getParentFile());
		}
	}

	public void home(View view){
		newLevel(home);
	}
	public void sdcard(View view){
		newLevel(sdcard);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.file_explore, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
