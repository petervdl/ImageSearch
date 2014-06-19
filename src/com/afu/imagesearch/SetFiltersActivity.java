package com.afu.imagesearch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

public class SetFiltersActivity extends Activity implements OnCheckedChangeListener {
	private RadioGroup rgroup=null;
	private RadioButton rb1;
	private RadioButton rb2;
	private RadioButton rb3;
	private RadioButton rb4;
	private RadioButton rb5;
	private String result_size="";
	private String result_type="";
	private String result_color="";
	private String result_site="";
	private Spinner spincolor;
	private Spinner spintype;
	private EditText et_site;

	private String[] colors;
	private String[] types;

	private void setUpViews(){
		EditText et_site = (EditText) findViewById(R.id.et_site);
		Log.e ("imagesearch", "in setUpViews 1, et_site="+et_site);
		// we don't need to listen to et_site, just take input if it is there.

		rgroup = (RadioGroup) findViewById(R.id.rgroup);
		rgroup.setOnCheckedChangeListener(this);
		rb1 = (RadioButton) findViewById(R.id.rb1);
		rb2 = (RadioButton) findViewById(R.id.rb2);
		rb3 = (RadioButton) findViewById(R.id.rb3);
		rb4 = (RadioButton) findViewById(R.id.rb4);
		rb5 = (RadioButton) findViewById(R.id.rb5);

		spincolor = (Spinner) findViewById(R.id.spincolor);
		colors= getResources().getStringArray(R.array.colors);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, colors);
		spincolor.setAdapter(adapter);

		spintype = (Spinner) findViewById(R.id.spintype);
		types= getResources().getStringArray(R.array.imagetype);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, types);
		spintype.setAdapter(adapter2);

		ImageButton save = (ImageButton) findViewById(R.id.save);

	}

	private void setUpHandlerForColor() {
		spincolor.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int index=arg0.getSelectedItemPosition();
				// if (index==0) return; // don't do anything in list setup

				result_color = SearchParamConstants.IMGCOLOR[index];
				// Toast.makeText(getApplicationContext(), colors[index], Toast.LENGTH_SHORT).show();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				; // result_color = SearchParamConstants.IMGCOLOR[0];
			}
		});
	}

	private void setUpHandlerForType() {
		spintype.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int index=arg0.getSelectedItemPosition();
				// if (index==0) return; // don't do anything in list setup

				result_type = SearchParamConstants.IMGTYPE[index];

				Toast.makeText(getApplicationContext(), types[index], Toast.LENGTH_SHORT).show();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				; //result_type = SearchParamConstants.IMGTYPE[0];

			}
		});
	}



	public void savePreferences() {
		result_site="";
		if (et_site==null) et_site = (EditText) findViewById(R.id.et_site);
		Editable e = et_site.getText();
		if (e!=null) {
			result_site  = et_site.getText().toString();
		} 

		PreferenceManager.getDefaultSharedPreferences(this)
		.edit()
		.putString("result_size", result_size)
		.putString("result_type", result_type)
		.putString("result_color", result_color)
		.putString("result_site", result_site)
		.commit();
		Log.d("imagesearch", "in savePrefs, size=" + result_size +", type=" + result_type 
				+", color=" + result_color +", site=" + result_site);
	}



	private int getIndex(Spinner spinner, String myString) {
		int index = 0;

		if (myString.equals("")) { 
			return 0;	
		}
		
		if (myString!=null && myString.length() >0) {  // strip off the "param=" text
			int endIndex = myString.lastIndexOf("=");
			if (endIndex!= -1) {
				myString = myString.substring(endIndex+1, myString.length());
			}
		}
		for (int i=0;i<spinner.getCount();i++){
			String s = spinner.getItemAtPosition(i).toString();
			if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
				index = i;
				i=spinner.getCount(); // breaks out of the loop
				Log.d("imagesearch", " in getIndex, matched set spinner item "+i +" is " +myString);
			}
		}
		return index;
	} 

	public void readbackPreferences() {
		SharedPreferences sp =
				PreferenceManager.getDefaultSharedPreferences(this);

		String query;

		query = sp.getString("result_type", "");
		// get the string after the last "="
		Log.d("imagesearch", "query 1 ="+query);
	
		spintype.setSelection(getIndex(spintype, query));

		query = sp.getString("result_color", "");
		spincolor.setSelection(getIndex(spincolor, query));
		Log.d("imagesearch", "query 2 ="+query);

		query = sp.getString("result_size", "");
		Log.d("imagesearch", "query 3 ="+query);

		if (query.contains("icon")) {
			// set the iconsize checked
			rb1.setChecked(true);
			Log.d("imagesearch", "setting rb1 icon");
		} else if (query.contains("medium")) {
			// set the iconsize checked
			rb2.setChecked(true);
			Log.d("imagesearch", "setting rb2 icon");
		} else if (query.contains("xxlarge")) {
			// set the iconsize checked
			rb3.setChecked(true);
		} else if (query.contains("huge")) {
			// set the iconsize checked
			rb4.setChecked(true);
		} else {
			rb1.setChecked(false);
			rb2.setChecked(false);
			rb3.setChecked(false);
			rb4.setChecked(false);
			rb5.setChecked(false);
		}

		query = sp.getString("result_site", "");
		Log.d("imagesearch", "query 4 ="+query);

		if (!query.equals("")) { 
			result_site = query;
			if (et_site==null) et_site = (EditText) findViewById(R.id.et_site);
			Log.e ("imagesearch", "in readbackPrefs 1, et_site="+et_site);
			et_site.setText(query);
		}
		Log.d("imagesearch", "readPrefs, got/set et and result_site ="+result_site);

	}

	public void returnSettings(View v) {
		// this handles the click of the "Save" button
		// we need to save all the results as preferences, 
		// and send them back to use in the search activity

		// Put result strings into an intent, then return result
		Intent i = new Intent();
		String site ="";
		String site_with_args="";
		if (et_site!=null) {
			site = et_site.getText().toString();
		}
		if (site.trim().length() > 0 && site.contains(".")) {
			// this means that site is non blank, so include it in search
			site_with_args="&as_sitesearch="+site;
		}
		result_site= site;
		i.putExtra("imgsz", result_size + result_color + result_type + site_with_args);
		setResult(RESULT_OK, i);

		// remember the values set in preferences
		savePreferences();
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_filters);
		setUpViews();
		setUpHandlerForColor();
		setUpHandlerForType();
		readbackPreferences();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Log.d("imagesearch", "rb checkedId="+checkedId);
		if (rb1.isChecked()) result_size = SearchParamConstants.IMGSZ[0];
		else if (rb2.isChecked()) result_size = SearchParamConstants.IMGSZ[1];
		else if (rb3.isChecked()) result_size = SearchParamConstants.IMGSZ[2];
		else if (rb4.isChecked()) result_size = SearchParamConstants.IMGSZ[3];
		else if (rb5.isChecked()) result_size = SearchParamConstants.IMGSZ[4];
	}
}

class SearchParamConstants {
	static final String [] IMGSZ = { "&imgsz=icon", 
		"&imgsz=medium", "&imgsz=xxlarge", "&imgsz=huge", "" };

	static final String [] IMGCOLOR = { "", "&imgcolor=black",
		"&imgcolor=blue", "&imgcolor=brown", "&imgcolor=gray",
		"&imgcolor=green", "&imgcolor=orange", "&imgcolor=pink", 
		"&imgcolor=purple", "&imgcolor=red", "&imgcolor=teal",
		"&imgcolor=white",  "&imgcolor=yellow" };

	static final String [] IMGTYPE = { "",  "&imgtype=face",
		"&imgtype=photo", "&imgtype=clipart", "&imgtype=lineart"};

}