package com.example.technicalsupportv2;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class GuidesList extends Activity {
	
	private static final String TAG_CATID = "cat_id";
	private static final String TAG_GUIDES = "guides";
	private static final String TAG_GUIDEID = "guide_id";
	private static final String TAG_TITLE = "title";
	private JSONArray jsonGuides = null;
	private ArrayList<HashMap<String, String>> guidesList;
	private boolean jsonResult;
	
	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guides_list);
		// Show the Up button in the action bar.
		setupActionBar();
		new LoadGuides().execute();
	}
	
	public class LoadGuides extends AsyncTask<Void, Void, Boolean> {

    	@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(GuidesList.this);
			pDialog.setMessage("Loading Guides...");
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
                updateListView();
            } else {
            	Toast.makeText(GuidesList.this, "Connection error: Please check your server's IP settings.", 
            			Toast.LENGTH_SHORT).show();
            }
        }
    }
	
	private boolean getJSON() {
		guidesList = new ArrayList<HashMap<String, String>>();
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		String getGuidesURL = ("http://" + sharedPrefs.getString("serverIP", null) + ":80/project/guidesList.php?catid=");
		String getSearchURL = ("http://" + sharedPrefs.getString("serverIP", null) + ":80/project/search.php?catid=");
        JSONParser jParser = new JSONParser();
        JSONObject json = new JSONObject();
        //System.out.println(getIntent().getExtras().getString("GUIDEID"));
		if (getIntent().getExtras().getString("GUIDEID").equals("search")) {
			json = jParser.getJSONFromUrl(getSearchURL + getIntent().getExtras().getString("KEYWORD"));
		} else {
			json = jParser.getJSONFromUrl(getGuidesURL + getIntent().getExtras().getString("GUIDEID"));
		}
		
		if (json != null) {

        try {
            
            jsonGuides = json.getJSONArray(TAG_GUIDES);

            for (int i = 0; i < jsonGuides.length(); i++) {
                JSONObject c = jsonGuides.getJSONObject(i);

                String guideId = c.getString(TAG_GUIDEID);
                String catId = c.getString(TAG_CATID);
                String title = c.getString(TAG_TITLE);
                
                HashMap<String, String> map = new HashMap<String, String>();
              
                map.put(TAG_GUIDEID, guideId);
                map.put(TAG_CATID, catId);
                map.put(TAG_TITLE, title);
             
                guidesList.add(map);
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
	
	private void updateListView() {
		ListAdapter adapter = new SimpleAdapter(this, guidesList,
				R.layout.list_view, new String[] { TAG_TITLE }, new int[] { R.id.textView });
		
		ListView catLV = (ListView) findViewById(R.id.listView1);
		catLV.setAdapter(adapter);	
		catLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            	Intent intent = new Intent(GuidesList.this, TestGuide.class);
            	intent.putExtra("cat", Integer.parseInt(guidesList.get(position).get(TAG_GUIDEID)));
            	startActivity(intent);

            }
        });
		
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
		getMenuInflater().inflate(R.menu.guides_list, menu);
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
