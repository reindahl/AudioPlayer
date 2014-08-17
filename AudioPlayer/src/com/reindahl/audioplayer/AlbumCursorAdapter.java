package com.reindahl.audioplayer;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.reindahl.audioplayer.library.SQLiteHelper;



public class AlbumCursorAdapter extends CursorAdapter {
	static class ViewHolder {
		public ImageView coverArt;
		public TextView Album;
		public TextView Artist;

	}
	private LayoutInflater mInflater;

	public AlbumCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}



	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View rowView = mInflater.inflate(R.layout.item_album, parent,false);
		// configure view holder
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.coverArt = (ImageView) rowView.findViewById(R.id.listCover);
		viewHolder.Album = (TextView) rowView.findViewById(R.id.listAlbum);
		viewHolder.Artist = (TextView) rowView.findViewById(R.id.listArtist);
		rowView.setTag(R.string.key_holder,viewHolder);
		return rowView;
	}



	@Override
	public void bindView(View rowView, Context context, Cursor cursor) {
		rowView.setTag(R.string.key_album,cursor.getString(0));
		rowView.setTag(R.string.key_artist,cursor.getString(1));
		
		ViewHolder holder = (ViewHolder) rowView.getTag(R.string.key_holder);

		String path =cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ARTPATH));
		
		if(path!=null){
			File imgFile = new  File(path);
		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

		    holder.coverArt.setImageBitmap(myBitmap);

		}else{
		
			holder.coverArt.setImageResource(R.drawable.thubnailsquares);
		}		
		
		holder.Album.setText(cursor.getString(0));
		holder.Artist.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ARTIST)));
	}








}
