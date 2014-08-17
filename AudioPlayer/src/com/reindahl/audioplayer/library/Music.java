package com.reindahl.audioplayer.library;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.AndroidImageHandler;
import org.jaudiotagger.tag.images.Artwork;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.reindahl.audioplayer.BuildConfig;
import com.reindahl.audioplayer.Constants;
import com.reindahl.audioplayer.helper.Helper;

public class Music {
	public boolean valid=true;
	private String title;
	private String artist;
	private String album;
	private String genre;
	private String comment;
	private String path;
	private String albumArtist;
	private String composer;
	private String grouping;
	private String year;
	private Bitmap artwork;
	private int hash;
	static HashFunction hf = Hashing.murmur3_32();
	public Music(){}
	
	public Music(String path) {
		super();
		this.path = path;

		try {
			 AudioFile f= AudioFileIO.read(new File(path));
			 Tag tag = f.getTag();
			 title=tag.getFirst(FieldKey.TITLE);
			 album=tag.getFirst(FieldKey.ALBUM);
			 artist=tag.getFirst(FieldKey.ARTIST);
			 albumArtist=tag.getFirst(FieldKey.ALBUM_ARTIST);
			 genre=tag.getFirst(FieldKey.GENRE);
			 comment=tag.getFirst(FieldKey.COMMENT);
			 composer=tag.getFirst(FieldKey.COMPOSER);
			 grouping=tag.getFirst(FieldKey.GROUPING);
			 year=tag.getFirst(FieldKey.YEAR);
			 
			 for (Artwork artwork : tag.getArtworkList()) {
				 try {
					this.artwork=(Bitmap) artwork.getImage();
					Hasher hasher=hf.newHasher();
					hasher.putBytes(artwork.getBinaryData());
					
					byte[] tmp= hasher.hash().asBytes();
					hash=Helper.ByteArrayToInt(tmp);
					if (BuildConfig.DEBUG) {
						Log.d(Constants.LOGLibrary, "hash "+Arrays.toString(tmp));
						Log.d(Constants.LOGLibrary, "hash "+hash);
					}


				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 break;
			 }
		} catch (CannotReadException e) {
			if (BuildConfig.DEBUG) {
				Log.e(Constants.LOGLibrary, "CannotReadException",e);
			}
			// TODO try using MediaMetadataRetriever
			e.printStackTrace();
			title="unkown";
			album="unkown";
			artist="unkown";
		} catch (TagException e) {
			if (BuildConfig.DEBUG) {
				Log.e(Constants.LOGLibrary, "TagException: "+path,e);

			}
			e.printStackTrace();
			valid=false;
		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				Log.e(Constants.LOGLibrary, "Exception: "+path,e);
				e.printStackTrace();
			}
			valid=false;
		}

//		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//		
//		mmr.setDataSource(path);
//
//		title=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//		album=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
//		artist=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
//		
//		mmr.release();
		Log.i(Constants.LOGLibrary, toString());
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	@Override
	public String toString() {
		return "Music [title=" + title + ", artist=" + artist +", album artist="+albumArtist
				+ "]";
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getAlbumArtist() {
		return albumArtist;
	}

	public void setAlbumArtist(String albumArtist) {
		this.albumArtist = albumArtist;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getGrouping() {
		return grouping;
	}

	public void setGrouping(String grouping) {
		this.grouping = grouping;
	}

	public String getComposer() {
		return composer;
	}

	public void setComposer(String composer) {
		this.composer = composer;
	}

	public int getHash() {
		return hash;
	}

	public void setHash(int hash) {
		this.hash = hash;
	}

	public Bitmap getArtwork() {
		return artwork;
	}

	public byte[] ArtworkToBytes(){
		
		try {
			return AndroidImageHandler.getInstanceOf().writeImage(artwork, "JPEG");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public byte[] ThubnailArtworkToBytes(){
		try{
		float scale=artwork.getHeight()>artwork.getWidth()?64.f/artwork.getHeight():64.f/artwork.getWidth();
		if(artwork.getHeight()>64 || artwork.getWidth()>64){
			return AndroidImageHandler.getInstanceOf().writeImage(Bitmap.createScaledBitmap(artwork, (int)(artwork.getWidth()*scale), (int)(artwork.getHeight()*scale), true), "JPEG");
		}else {
			return ArtworkToBytes();
		}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public void setArtwork(Bitmap artwork) {
		this.artwork = artwork;
	}

}
