package com.reindahl.audioplayer.helper;

public class Helper {
	  public static int ByteArrayToInt(byte[] bytes) {
		     return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
		}
}
