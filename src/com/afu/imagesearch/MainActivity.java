// Peter van der Linden
// Grid Image Search
// June 14 2014.

package com.afu.imagesearch;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class MainActivity extends Activity {
	EditText et_query;
	GridView gv_results;
	// Button bt_search;
	ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();
	ImageResultArrayAdapter imageAdapter;
	@Override
	public boolean onCreateOptionsMenu(Menu m) {
		//Log.d("imagesearch","has called onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.settings, m);
		return true;
	}

	public void doSearchAction(MenuItem mi) {
		// the "Search" action in Action bar has been pressed
		// go to the filters activity
		onImageSearch(null);
	}

	private static final int REQCODE =1;

	public void doFiltersAction(MenuItem mi) {
		// the "Filters" action in Action bar has been pressed
		// go to the filters activity
		Intent i = new Intent(getApplicationContext(), SetFiltersActivity.class);
		// i.putExtra("url", ir.getFullUrl());
		// i.putExtra("result", ir); // pass image, not the url to it.
		startActivityForResult(i, REQCODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// get the search params from the Intent
		Bundle b = data.getExtras();
		String query = et_query.getText().toString();
		String size= b.getString("imgsz");

		if (query==null || query =="") {
			return;
		}
		filterArgs= filterArgs + size;
		Log.d("imagesearch", "result returned from filters, filterArgs="+filterArgs);
		onImageSearch(null);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupViews();
		imageAdapter = new ImageResultArrayAdapter(this, imageResults);
		gv_results.setAdapter(imageAdapter);

		gv_results.setOnItemClickListener( new OnItemClickListener() {
			// when a thumbnail is clicked, invoke the "display (large) image" Activity
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {			
				Intent i = new Intent(getApplicationContext(), ImageDisplayActivity.class);
				ImageResult ir = imageResults.get(position);
				// i.putExtra("url", ir.getFullUrl());
				i.putExtra("result", ir); // pass image, not the url to it.

				startActivity(i);
			}
		});

		gv_results.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your AdapterView
				getNextPageOfResults(); 
			}
		});
	}

	private int totalThumbsRead =0;

	private void setupViews( ){
		et_query = (EditText) findViewById(R.id.et_query);
		gv_results = (GridView) findViewById(R.id.gv_results);
	}

	private String filterArgs="";

	public void getNextPageOfResults() {
		// this method asks for the next page of 8 results.
		String query = et_query.getText().toString();
		AsyncHttpClient client = new AsyncHttpClient();
		// request looks like
		// https://ajax.googleapis.com/ajax/services/search/images?q=Android&v=1.0
		String url = "https://ajax.googleapis.com/ajax/services/search/images?";
		String size = "rsz=8";
		String offset = "&start=";     //change offset to paginate
		String ver = "&v=1.0";
		String q = "&q=" + Uri.encode(query);  // drops space, illegal chars, etc
		String url_with_args= url + size + offset + totalThumbsRead + ver + q + filterArgs;
		client.get(url_with_args, jh);
		Log.d("imagesearch", "url=" +url_with_args);
	}

	JsonHttpResponseHandler jh = new JsonHttpResponseHandler () {

		// put an on failure here
		@Override
		public void onSuccess(JSONObject response) {
			JSONArray imageJsonResults = null;

			try {
				JSONObject jso = response.getJSONObject("responseData");
				Log.d("imagesearch", "onSuccess, responseData ="+jso);

				imageJsonResults = jso.getJSONArray("results");

				Log.d("imagesearch", "onSuccess, imageJsonResults ="+imageJsonResults);


				JSONObject jo = response.getJSONObject("responseData");
				String formatJo = jo.toString(4);
				Log.d("imagesearch", "onSuccess, response object ="+formatJo);

				ArrayList<ImageResult> alir = ImageResult.fromJSONArray(imageJsonResults);
				totalThumbsRead += alir.size();

				imageAdapter.addAll(alir);
				imageAdapter.notifyDataSetChanged();
				Log.d("ImageSearch", "got images "+ imageResults.toString());
			} catch (JSONException e) {
				Log.e("ImageSearch", "WHOA!  Error! "+ e.getMessage() );
				e.printStackTrace();
			}
		}

		@Override
		public void  onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.String responseBody, java.lang.Throwable e) {
			Toast.makeText(getApplicationContext(), "Check network connection...  Wifi and Data not working", Toast.LENGTH_LONG).show();
			try {				
				Log.d("imagesearch", "failure response object ="+responseBody);
				Log.d("imagesearch", "... statusCode ="+statusCode + " excpn0="+e.getMessage());
			} catch (Exception ex) {
				Log.e("ImageSearch", "Error! "+ ex.getMessage() );
				e.printStackTrace();
			}
		}

		@Override
		public void	onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable e, org.json.JSONArray errorResponse) {
			try {				
				Log.d("imagesearch", "failure response object ="+errorResponse);
				Log.d("imagesearch", "... statusCode ="+statusCode + ", excpn1="+e.getMessage());
			} catch (Exception ex) {
				Log.e("ImageSearch", "WHOA!  Error! "+ ex.getMessage() );
				e.printStackTrace();
			}
		}

		@Override
		public void	onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable e, org.json.JSONObject errorResponse) {
			try {				

				String formated = errorResponse.toString(4);
				Log.d("imagesearch", "failure response object ="+formated);
				Log.d("imagesearch", "... statusCode ="+statusCode +", e="+e.getMessage());
			} catch (Exception ex) {
				Log.e("ImageSearch", "WHOA!  Error! "+ ex.getMessage() );
				e.printStackTrace();
			}
		}

		@Override
		public void	onFailure(int statusCode, java.lang.Throwable e, org.json.JSONArray errorResponse) {
			try {				
				Log.d("imagesearch", "failure response object ="+errorResponse);
				Log.d("imagesearch", "... statusCode ="+statusCode +", e="+e.getMessage());
			} catch (Exception ex) {
				Log.e("ImageSearch", "WHOA!  Error! "+ ex.getMessage() );
				e.printStackTrace();
			}
		}


		@Override
		public void	onFailure(int statusCode, java.lang.Throwable e, org.json.JSONObject errorResponse) {
			try {				
				Log.d("imagesearch", "failure response object ="+errorResponse);
				Log.d("imagesearch", "... statusCode ="+statusCode +", e="+e.getMessage());
			} catch (Exception ex) {
				Log.e("ImageSearch", "WHOA!  Error! "+ ex.getMessage() );
				e.printStackTrace();
			}
		}

		@Override
		public void	onFailure(java.lang.Throwable e, org.json.JSONArray errorResponse) {
			try {				
				Log.d("imagesearch", "failure response object ="+errorResponse + ", e="+e.getMessage());
			} catch (Exception ex) {
				Log.e("ImageSearch", "WHOA!  Error! "+ ex.getMessage() );
				e.printStackTrace();
			}
		}


		@Override
		public void	onFailure(java.lang.Throwable e, org.json.JSONObject errorResponse) {
			try {				
				Log.d("imagesearch", "failure response object ="+errorResponse + ", excpn2 ="+e.getMessage());
			} catch (Exception ex) {
				Log.e("ImageSearch", "Error! "+ ex.getMessage() );
				e.printStackTrace();
			}
		}


	};

	public void onImageSearch(View v) {
		// a new search means throw away all the current image results.
		imageResults.clear();
		totalThumbsRead =0;
		getNextPageOfResults();

	}
}


