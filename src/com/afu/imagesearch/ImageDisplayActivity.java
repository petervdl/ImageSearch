package com.afu.imagesearch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;

import com.loopj.android.image.SmartImageView;

public class ImageDisplayActivity extends Activity {
    String sivUrl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);
		ImageResult result = (ImageResult) getIntent().getSerializableExtra("result");
		SmartImageView siv = (SmartImageView) findViewById(R.id.ivResult);
		sivUrl=result.getFullUrl();
		siv.setImageUrl(sivUrl);
	}
	

	
 public  Uri getUriFromImageView() {
		SmartImageView siv = (SmartImageView) findViewById(R.id.ivResult);
		Drawable mDrawable = siv.getDrawable();
		Bitmap mBitmap = ((BitmapDrawable)mDrawable).getBitmap();
		String path = Images.Media.insertImage(getContentResolver(), mBitmap, "Peter", null);

		Uri uri = Uri.parse(path);
		return uri;
	}
 
	public void onClick(View v) {
		// handles the "share to email" button
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
		String strBody="Here's the image from " + sivUrl;
		String[] strAddr=new String[] {"pvdl@afu.com"};
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, strAddr); 
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Image from search app"); 
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, strBody);

		emailIntent.setType("application/image");
		
        Uri myUri = getUriFromImageView();
        emailIntent.putExtra(Intent.EXTRA_STREAM, myUri);

		startActivity(Intent.createChooser(emailIntent, "Send mail..."));

	}
}


//Bitmap mutableBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
//View view  = new View(this);
//view.draw(new Canvas(mutableBitmap));

