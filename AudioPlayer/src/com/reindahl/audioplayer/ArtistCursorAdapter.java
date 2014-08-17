package com.reindahl.audioplayer;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class ArtistCursorAdapter extends CursorAdapter {
	static class ViewHolder {
		public ImageView coverArt;
		public TextView Artist;

	}
	private LayoutInflater mInflater;

	public ArtistCursorAdapter(Context context, Cursor cursor, int flags) {
		super(context, cursor, flags);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}



	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View rowView = mInflater.inflate(R.layout.item_artist, parent,false);
		// configure view holder
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.coverArt = (ImageView) rowView.findViewById(R.id.listCover);
		viewHolder.Artist = (TextView) rowView.findViewById(R.id.listArtist);
		rowView.setTag(R.string.key_holder,viewHolder);
		return rowView;
	}



	@Override
	public void bindView(View rowView, Context context, Cursor cursor) {
		rowView.setTag(R.string.key_artist,cursor.getString(0));
		ViewHolder holder = (ViewHolder) rowView.getTag(R.string.key_holder);

		holder.Artist.setText(cursor.getString(0));
	}








}
