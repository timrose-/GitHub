package com.example.knowledgedatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class AddGuide extends Activity {

	private String guideTitle;
	private static final int CAMERA_REQUEST = 1888;
	private ArrayList<Step> stepArray = new ArrayList<Step>();
	private Guide guide = new Guide();
	private int stepNo = 0;
	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userInputs();
		// Show the Up button in the action bar.
		setupActionBar();
	}

	private void userInputs() {
		LayoutInflater inflater = getLayoutInflater();
		final View dialoglayout = inflater.inflate(R.layout.dialog_user_input, (ViewGroup) getCurrentFocus());
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
		.setTitle("New Guide")
		.setMessage("Enter a title and select the category for the new guide.")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText textBox = (EditText) dialoglayout.findViewById(R.id.title_edit_view);
				guideTitle = textBox.getText().toString();
				Spinner spinner = (Spinner) dialoglayout.findViewById(R.id.category_edit_view);
				setContentView(R.layout.activity_add_guide);
				TextView titleTextView = (TextView) findViewById(R.id.txtTitle);
				titleTextView.setText(guideTitle);
				guide.setCatID(spinner.getSelectedItemPosition());
				guide.setGuideTitle(guideTitle);

			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				Intent intent = new Intent(AddGuide.this, Home.class);
				startActivity(intent);
			}
		});
		builder.setView(dialoglayout);
		AlertDialog alert = builder.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(16);
		Spinner spinner = (Spinner) alert.findViewById(R.id.category_edit_view);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.categories_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	public void btnImageClick(View v) {
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
		startActivityForResult(cameraIntent, CAMERA_REQUEST);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {  
			Bitmap photo = (Bitmap) data.getExtras().get("data"); 
			ImageView imageView = (ImageView) findViewById(R.id.imgStep);
			imageView.setImageBitmap(photo);
		}  
	} 

	public void btnAddStep(View v) {
		EditText editText1 = (EditText) findViewById(R.id.txtStepTitle);
		EditText editText = (EditText) findViewById(R.id.txtStep);
		ImageView imageView = (ImageView) findViewById(R.id.imgStep);
		Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
		Step step = new Step(stepNo, editText1.getText().toString(), editText.getText().toString(), bitmap);
		stepArray.add(step);
		stepNo += 1;
		editText.setText("");
		imageView.setImageBitmap(null);
		System.out.println(step);
	}

	public void btnFinish(View v) {
		EditText editText1 = (EditText) findViewById(R.id.txtStepTitle);
		EditText editText = (EditText) findViewById(R.id.txtStep);
		ImageView imageView = (ImageView) findViewById(R.id.imgStep);
		Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
		Step step = new Step(stepNo, editText1.getText().toString(), editText.getText().toString(), bitmap);
		stepArray.add(step);
		guide.setStepArray(stepArray);
		new SendToDatabase().execute();
	}

	public void uploadGuide(Guide guide) {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://" + sharedPrefs.getString("serverIP", null) + ":80/project/addguide.php");
		JSONObject json = new JSONObject();
		try {
			// JSON data:
			json.put("catId", guide.catID);
			json.put("guideTitle", guide.guideTitle);
			
			JSONArray jStepArray = new JSONArray();
			
			for (int i = 0; i<guide.stepArray.size(); i++) {
				JSONObject jsonStep = new JSONObject();
				jsonStep.put("stepNo", guide.stepArray.get(i).stepNo);
				jsonStep.put("stepTitle", guide.stepArray.get(i).stepTitle);
				jsonStep.put("stepTxt", guide.stepArray.get(i).stepTxt);
				jsonStep.put("stepImg", guide.stepArray.get(i).stepImg);
				jStepArray.put(jsonStep);
			}
			
			json.put("stepArray", jStepArray);
			

			JSONArray postJSON = new JSONArray();
			postJSON.put(json);

			// Post the data:
			httppost.setHeader("json",json.toString());
			httppost.getParams().setParameter("jsonpost",postJSON);

			// Execute HTTP Post Request
			System.out.println(postJSON);
			HttpResponse response = httpclient.execute(httppost);

			// for JSON:
			if(response != null)
			{
				InputStream is = response.getEntity().getContent();

				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();

				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				String text = sb.toString();
				System.out.println(text);
//				EditText editText = (EditText) findViewById(R.id.txtStep);
//				editText.setText(text);
			}
		}catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        } catch (JSONException e) {
        	
        }
	}

	public class SendToDatabase extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AddGuide.this);
			pDialog.setMessage("Connecting to database...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		@Override
		protected Boolean doInBackground(Void... arg0) {
			uploadGuide(guide);
			return null;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pDialog.dismiss();
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
		getMenuInflater().inflate(R.menu.add_guide, menu);
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
