package com.afu.imagesearch;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ImageResult implements Serializable {
	private static final long serialVersionUID = 1L;
	
	// parses results and turns JSON text into Java objects
	private String fullUrl;
	private String thumbUrl;
	
	public ImageResult(JSONObject j) {
		try {
			this.fullUrl = j.getString("url");
			this.thumbUrl = j.getString("tbUrl");
		} catch (JSONException e) {
			this.fullUrl = null;
			this.thumbUrl = null;
			Log.e("ImageResult", e.getMessage());
		}
	}
	
	public String getFullUrl() { return fullUrl; }
	public String getThumbUrl() { return thumbUrl; }
	public String toString() { return thumbUrl; }

	public static ArrayList<ImageResult> fromJSONArray(
			JSONArray array) {
		ArrayList<ImageResult> ir = new ArrayList<ImageResult>();
		for (int i=0; i<array.length(); i++) {
			try {
				JSONObject jo = array.getJSONObject(i);
				ir.add( new ImageResult(jo));
				Log.d("ImageSearch", "ir  = "+ir.toString() );
			} catch ( JSONException e) {
				e.printStackTrace();
			}
		}
		return ir;
	}

}
