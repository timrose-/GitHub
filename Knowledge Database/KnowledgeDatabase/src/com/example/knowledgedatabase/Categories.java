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
// import android.widget.Toast;

public class Categories extends Activity {
	
	private static final String TAG_CATID = "cat_id";
	private static final String TAG_CATEGORY = "category";
	private static final String TAG_CATEGORIES = "categories";
	private JSONArray jsonCategories = null;
	private ArrayList<HashMap<String, String>> categoryList;
	private boolean jsonResult;
	
	private ProgressDialog pDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories);
		// Show the Up button in the action bar.
		setupActionBar();
		new LoadCategories().execute();
		
	}
	
	private void updateListView() {
		
		ListAdapter adapter = new SimpleAdapter(this, categoryList,
				R.layout.list_view, new String[] { TAG_CATEGORY }, new int[] { R.id.textView });
		
		ListView catLV = (ListView) findViewById(R.id.listView1);
		catLV.setAdapter(adapter);	
		catLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            	Intent intent = new Intent(Categories.this, GuidesList.class);
            	Bundle extras = new Bundle();
        		extras.putString("GUIDEID",categoryList.get(position).get(TAG_CATID));
        		extras.putString("KEYWORD", "");
        		intent.putExtras(extras);
            	startActivity(intent);

            }
        });
		
	}
	
	public class LoadCategories extends AsyncTask<Void, Void, Boolean> {

    	@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Categories.this);
			pDialog.setMessage("Loading Categories...");
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
            	Toast.makeText(Categories.this, "Connection error: Please check your server's IP settings.", 
            			Toast.LENGTH_SHORT).show();
            }
        }
    }
	
	private void getJSON() {
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		String getURL = ("http://" + sharedPrefs.getString("serverIP", null) + ":80/project/categories.php");
		
		categoryList = new ArrayList<HashMap<String, String>>();
        JSONParser jParser = new JSONParser();
        JSONObject json = jParser.getJSONFromUrl(getURL);
        
        if (json != null){

        try {
            
            jsonCategories = json.getJSONArray(TAG_CATEGORIES);

            for (int i = 0; i < jsonCategories.length(); i++) {
                JSONObject c = jsonCategories.getJSONObject(i);

                String catId = c.getString(TAG_CATID);
                String category = c.getString(TAG_CATEGORY);
                
                HashMap<String, String> map = new HashMap<String, String>();
              
                map.put(TAG_CATID, catId);
                map.put(TAG_CATEGORY, category);
             
                categoryList.add(map);
                
                jsonResult = true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        } else {
        	jsonResult = false;
        }
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
		getMenuInflater().inflate(R.menu.categories, menu);
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
