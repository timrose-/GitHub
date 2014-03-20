package com.example.technicalsupportv2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayGuide extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	private static final String TAG_STEPS = "steps";
	private static final String TAG_STEPID = "step_id";
	private static final String TAG_GUIDEID = "guide_id";
	private static final String TAG_STEPTXT = "step_txt";
	private JSONArray jsonSteps = null;
	private ArrayList<HashMap<String, String>> stepsList;
	
	private ProgressDialog pDialog;


	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_guide);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		/*for (String tab_name : tabs) {
			mViewPager.set
            getActionBar().addTab(getActionBar().newTab().setText(tab_name)
                    .setTabListener(this));
        }*/
		
		new LoadGuides().execute();

	}
	
	public class LoadGuides extends AsyncTask<Void, Void, Boolean> {

    	@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(DisplayGuide.this);
			pDialog.setMessage("Loading Steps...");
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
            updateListView();
        }
    }
	
	private void getJSON() {
		stepsList = new ArrayList<HashMap<String, String>>();
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		String getURL = ("http://" + sharedPrefs.getString("serverIP", null) + ":80/project/guide.php?guideid=");
		
        JSONParser jParser = new JSONParser();
        JSONObject json = jParser.getJSONFromUrl(getURL + getIntent().getExtras().getInt("cat"));

        try {
            
            jsonSteps = json.getJSONArray(TAG_STEPS);

            for (int i = 0; i < jsonSteps.length(); i++) {
                JSONObject c = jsonSteps.getJSONObject(i);

                String stepId = c.getString(TAG_STEPID);
                String guideId = c.getString(TAG_GUIDEID);
                String stepTxt = c.getString(TAG_STEPTXT);
                
                HashMap<String, String> map = new HashMap<String, String>();
              
                map.put(TAG_STEPID, stepId);
                map.put(TAG_GUIDEID, guideId);
                map.put(TAG_STEPTXT, stepTxt);
             
                stepsList.add(map);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
		
	}
	
	private void updateListView() {
		ListAdapter adapter = new SimpleAdapter(this, stepsList,
				R.layout.list_view_guides, new String[] { TAG_STEPID }, new int[] { R.id.title });
		
		ListView catLV = (ListView) findViewById(R.id.listView1);
		catLV.setAdapter(adapter);	
		catLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(DisplayGuide.this, "You Clicked at "+ stepsList.get(+position), 
                		Toast.LENGTH_SHORT).show();

            }
        });
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_guide, menu);
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

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new StepFragment();
			Bundle args = new Bundle();
			args.putInt(StepFragment.ARG_STEP_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class StepFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_STEP_NUMBER = "section_number";

		public StepFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_display_guide_dummy, container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_STEP_NUMBER)));
			return rootView;
		}
	}

}
