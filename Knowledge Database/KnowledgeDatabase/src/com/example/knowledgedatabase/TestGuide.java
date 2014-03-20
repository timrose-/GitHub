package com.example.technicalsupportv2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TestGuide extends Activity {

	private static final String TAG_STEPS = "steps";
	private static final String TAG_STEPID = "step_id";
	private static final String TAG_GUIDEID = "guide_id";
	private static final String TAG_STEPTITLE = "step_title";
	private static final String TAG_STEPTXT = "step_txt";
	private static final String TAG_STEPIMG = "step_img";
	private JSONArray jsonSteps = null;
	private ArrayList<HashMap<String, String>> stepsList;
	private int position = 0;
	private ArrayList<Bitmap> bitmapArray;
	private boolean jsonResult;

	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_guide);
		// Show the Up button in the action bar.
		setupActionBar();
		//		getGuidesURL += getIntent().getExtras().getInt("cat");
		new LoadGuides().execute();
	}

	public class LoadGuides extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(TestGuide.this);
			pDialog.setMessage("Loading Guide...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		@Override
		protected Boolean doInBackground(Void... arg0) {
			getJSON();
			return null;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if (jsonResult == true) { 
				updateView(position);
			} else {
				Toast.makeText(TestGuide.this, "Connection error: Please check your server's IP settings.", 
						Toast.LENGTH_SHORT).show();
			}
			updateView(position);
		}
	}

	private boolean getJSON() {
		stepsList = new ArrayList<HashMap<String, String>>();
		bitmapArray = new ArrayList<Bitmap>();
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		String getURL = ("http://" + sharedPrefs.getString("serverIP", null) + ":80/project/guide.php?guideid=");
		JSONParser jParser = new JSONParser();
		JSONObject json = jParser.getJSONFromUrl(getURL + getIntent().getExtras().getInt("cat"));

		if (json != null) {

			try {

				jsonSteps = json.getJSONArray(TAG_STEPS);

				for (int i = 0; i < jsonSteps.length(); i++) {
					JSONObject c = jsonSteps.getJSONObject(i);

					String stepId = c.getString(TAG_STEPID);
					String guideId = c.getString(TAG_GUIDEID);
					String title = c.getString(TAG_STEPTITLE);
					String stepTxt = c.getString(TAG_STEPTXT);
					String stepImg = c.getString(TAG_STEPIMG);

					HashMap<String, String> map = new HashMap<String, String>();

					map.put(TAG_STEPID, stepId);
					map.put(TAG_GUIDEID, guideId);
					map.put(TAG_STEPTITLE, title);
					map.put(TAG_STEPTXT, stepTxt);
					map.put(TAG_STEPIMG, stepImg);

					stepsList.add(map);

					if (stepImg.isEmpty()) {
						bitmapArray.add(null);
					} else {
						URL url;
						try {
							url = new URL("http://" + sharedPrefs.getString("serverIP", null) + ":80/project/images/" + guideId 
									+ "/" + stepImg);
							bitmapArray.add(BitmapFactory.decodeStream(url.openConnection().getInputStream()));

						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					jsonResult = true;


				}



			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			jsonResult = false;
		}
		return jsonResult;

	}

	private void updateView(int position) {

		TextView t1 = (TextView)findViewById(R.id.txtTitle);
		t1.setText(stepsList.get(position).get(TAG_STEPTITLE));
		TextView t2 = (TextView)findViewById(R.id.txtStep);
		t2.setText(stepsList.get(position).get(TAG_STEPTXT));
		ImageView i1 = (ImageView)findViewById(R.id.imgStep);
		if (bitmapArray.isEmpty()) {

		} else {
			if (bitmapArray.get(position) != null) {
				i1.setImageBitmap(bitmapArray.get(position));
			} else {
				i1.setImageBitmap(null);
			}
		}
		Button btnNext = (Button)findViewById(R.id.button1);
		Button btnPrevious = (Button)findViewById(R.id.button2);
		if (position == 0) {
			btnPrevious.setEnabled(false);
		}
		else {
			btnPrevious.setEnabled(true);
		}
		if (position >= (stepsList.size()-1)) {
			btnNext.setEnabled(false);
		}
		else {
			btnNext.setEnabled(true);
		}


	}


	public void btnClickNext(View v) {
		position += 1;
		updateView(position);
	}

	public void btnClickPrevious(View v) {
		position -= 1;
		updateView(position);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_guide, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
