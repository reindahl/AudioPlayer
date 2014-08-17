package com.reindahl.audioplayer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface Constants {
	public final String LOG = "com.reindahl.audioplayer";
	public final String LOGLibrary = "library";
	public final String LOGPlayer = "player";
	public final String LOGFiles = "files";
	public final String LOGSql = "sql";
	public final Set<String> audioFiles = new HashSet<String>(Arrays.asList("mp3", "m4a", "m4b")) ;

} 