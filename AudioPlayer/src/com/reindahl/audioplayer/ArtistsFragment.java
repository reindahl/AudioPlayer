package com.reindahl.audioplayer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.reindahl.audioplayer.library.Library;

public class ArtistsFragment extends Fragment {
	// This is the Adapter being used to display the list's data
	public static ArtistCursorAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_list, container, false);

		ListView view = (ListView)rootView.findViewById(R.id.list);
		view.setEmptyView(rootView.findViewById(R.id.empty_list_item));
		adapter = new ArtistCursorAdapter(getActivity(), Library.db.getAllArtistCursor(), 0);
		view.setAdapter(adapter);

		return rootView;
	}
}
