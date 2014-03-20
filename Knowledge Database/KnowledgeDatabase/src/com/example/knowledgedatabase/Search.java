package com.example.technicalsupportv2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Search extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupActionBar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}
	public void btnClickSearch(View v) {
		EditText input = (EditText) findViewById(R.id.editText1);
		if (input.getText().toString().isEmpty()) {
			Toast.makeText(Search.this, "Please enter a keyword to search for." , Toast.LENGTH_SHORT).show();
		} else {
			Intent intent = new Intent(this, GuidesList.class);
			Bundle extras = new Bundle();
			extras.putString("GUIDEID", "search");
			extras.putString("KEYWORD", input.getText().toString());
			System.out.println(input.getText().toString());
			intent.putExtras(extras);
			startActivity(intent);
		}
	}
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

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
