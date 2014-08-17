package com.reindahl.audioplayer;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.reindahl.audioplayer.library.SQLiteHelper;



public class SearchCursorAdapter extends CursorAdapter {
	static class ViewHolder {
		public ImageView coverArt;
		public TextView Title;
		public TextView Artist;

	}
	private LayoutInflater mInflater;

	public SearchCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}



	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View rowView = mInflater.inflate(R.layout.item_song, parent,false);
		// configure view holder
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.coverArt = (ImageView) rowView.findViewById(R.id.listCover);
		viewHolder.Title = (TextView) rowView.findViewById(R.id.listTitle);
		viewHolder.Artist = (TextView) rowView.findViewById(R.id.listArtist);
		rowView.setTag(R.string.key_holder,viewHolder);
		return rowView;
	}



	@Override
	public void bindView(View rowView, Context context, Cursor cursor) {
		rowView.setTag(R.string.key_file,cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_PATH)));
		
		ViewHolder holder = (ViewHolder) rowView.getTag(R.string.key_holder);
		holder.Title.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_TITLE)));
		holder.Artist.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ARTIST)));

	}
	
	









}
