package com.afu.imagesearch;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.loopj.android.image.SmartImageView;

public class ImageResultArrayAdapter extends ArrayAdapter<ImageResult> {

	public ImageResultArrayAdapter(Context context, List<ImageResult> images) {
		super(context, R.layout.item_image_result, images);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//we put one image url into one ImageView
		Log.d("imagesearch", "In adapter, position="+position +", count="+  this.getCount());
		if (this.getCount()==0) return null;

		ImageResult imageInfo = this.getItem(position);
		SmartImageView ivImage;
		if (convertView == null) {  //if we are not passed in a View, then inflate to create it.
			LayoutInflater inf = LayoutInflater.from(getContext());
			ivImage = (SmartImageView) inf.inflate(R.layout.item_image_result, parent, false);
		} else {
			// take existing view, make bkgrnd transparent to clear it
			// This saves us having to create a new object for every image.
			ivImage = (SmartImageView) convertView;
			ivImage.setImageResource(android.R.color.transparent);
		}
		// put the img from the url into the view we are building
		ivImage.setImageUrl(imageInfo.getThumbUrl());
		return ivImage;
	}



}
