package com.example.movieproject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EditActivity extends Activity implements OnClickListener {

	String imdbIdFromClass;
	private static final String WEB_RESULT = "webresult";
	private static final String TAG = "EditActivity";
	private Bitmap bitMapImage;
	private static final String IMAGE_RESOURCE = "imageresource";
	private int image;
	DbHandler dbhandler;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		if (savedInstanceState != null) {
			String imageSaved = savedInstanceState.getString("urlOutput");
			EditText output1 = (EditText) findViewById(R.id.ShowURL);
			output1.setText(imageSaved);
			GetImage saved = (GetImage) new GetImage().execute(imageSaved);
		}
		

		Button ok = (Button) findViewById(R.id.okEditBtn);
		Button cancel = (Button) findViewById(R.id.CancelEditBtn);
		Button showPic = (Button) findViewById(R.id.ShowPic);
		EditText title = (EditText) findViewById(R.id.EnterMovie);
		EditText url = (EditText) findViewById(R.id.ShowURL);
		EditText body = (EditText) findViewById(R.id.BodyText);

		ok.setOnClickListener(this);
		cancel.setOnClickListener(this);
		showPic.setOnClickListener(this);

		dbhandler = new DbHandler(this);

		Intent intent = getIntent();

		if (intent.getAction().equals(Intent.ACTION_EDIT)) {
			long id = intent.getLongExtra("id", 666);
			Movie movie = dbhandler.query(id);
			String txtname = movie.getTitle();
			String txtdesc = movie.getDescreption();
			String txturl = movie.getUrl();

			url.setText(txturl);
			body.setText(txtdesc);
			title.setText(txtname);
		}

		if (intent.getAction().equals(Intent.ACTION_WEB_SEARCH)) {

			Intent intentFromSearch = getIntent();
			imdbIdFromClass = intentFromSearch.getStringExtra("imdbId");
			// Toast.makeText(this, imdbIdFromClass, Toast.LENGTH_LONG).show();

			getMovie task = new getMovie();
			task.execute(Search.ADDRESS);
		}

	}

	class getMovie extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... params) {
			Log.d(TAG + "doInBackground", "doInBackground start");
			BufferedReader input = null;
			HttpURLConnection connection = null;
			StringBuilder response = new StringBuilder();

			try {

				String queryString = "";
				queryString += "?i="
						+ URLEncoder.encode(imdbIdFromClass, "utf-8");

				URL url = new URL(params[0] + queryString);
				Log.d(TAG + "doInBackground", "url: " + url);
				connection = (HttpURLConnection) url.openConnection();

				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					return null;
				}

				input = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));

				String line = "";
				while ((line = input.readLine()) != null) {
					response.append(line + "\n");
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {

				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (connection != null) {
					connection.disconnect();
				}
			}
			Log.d(TAG + "doInBackground", "doInBackground done");
			return response.toString();

		}

		protected void onPostExecute(String result) {
			Log.d(TAG + "onPostExecute", "onPostExecute start");

			if (result == null || result.length() == 0) {
				Log.d("Search", "RESULT" + result);
				Toast.makeText(EditActivity.this, "imdbid error",
						Toast.LENGTH_LONG).show();
				return;
			}

			String title = "";
			String poster = "";
			String plot = "";
			try {

				JSONObject obj = new JSONObject(result);

				title = obj.getString("Title");
				Log.d(TAG + "onPostExecute", "Title : " + title);
				plot = obj.getString("Plot");
				poster = obj.getString("Poster");
				EditText searchTitle = (EditText) findViewById(R.id.EnterMovie);
				EditText searchPlot = (EditText) findViewById(R.id.BodyText);
				EditText searchPoster = (EditText) findViewById(R.id.ShowURL);

				searchTitle.setText(title);
				searchPlot.setText(plot);
				searchPoster.setText(poster);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	public void onClick(View v) {

		Intent intent = getIntent();
		EditText title = (EditText) findViewById(R.id.EnterMovie);
		EditText url = (EditText) findViewById(R.id.ShowURL);
		EditText body = (EditText) findViewById(R.id.BodyText);

		String editTitle = title.getText().toString();
		String editUrl = url.getText().toString();
		String editBody = body.getText().toString();

		switch (v.getId()) {
		case R.id.okEditBtn:

			Movie movie = new Movie(editTitle, editBody, editUrl);

			if (intent.getAction().equals(Intent.ACTION_EDIT)) {

				long id = intent.getLongExtra("id", 0);
				movie.setId(id);
				dbhandler.update(movie);
				finish();
			}
			if (intent.getAction().equals(Intent.ACTION_WEB_SEARCH)
					|| intent.getAction().equals(Intent.ACTION_INSERT)) {

				dbhandler.insert(movie);
				finish();
			}

			break;

		case R.id.CancelEditBtn:
			finish();

			break;

		case R.id.ShowPic:
			new GetImage().execute(editUrl);
			ImageView image = (ImageView) findViewById(R.id.imageViewBody);
			image.setVisibility(View.VISIBLE);
			image.setImageURI(Uri.parse(editUrl));
		}
	}

	class GetImage extends AsyncTask<String, Integer, Bitmap> {

		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(EditActivity.this);
			progressDialog.setTitle("Coming soon!");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);;
			progressDialog.show();
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			String address = params[0];

			HttpURLConnection connection = null;
			InputStream stream = null;
			ByteArrayOutputStream outputStream = null;

			// the bitmap will go here:
			Bitmap b = null;

			try {
				// build the URL:
				URL posterUrl = new URL(address);
				// open a connection:
				connection = (HttpURLConnection) posterUrl.openConnection();

				// check the connection response code:
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					// not good..
					return null;
				}

				// the input stream:
				stream = connection.getInputStream();

				// get the length:
				int length = connection.getContentLength();
				// if you have a progress dialog - :
				// tell the progress dialog the length:
				// this CAN (!!) be modified outside the UI thread !!!
				progressDialog.setMax(length);

				// a stream to hold the read bytes.
				// (like the StringBuilder we used before)
				outputStream = new ByteArrayOutputStream();

				// a byte buffer for reading the stream in 1024 bytes chunks:
				byte[] buffer = new byte[1024];

				int totalBytesRead = 0;
				int bytesRead = 0;

				// read the bytes from the stream
				while ((bytesRead = stream.read(buffer, 0, buffer.length)) != -1) {
					totalBytesRead += bytesRead;
					outputStream.write(buffer, 0, bytesRead);

					// if you want - you can notify
					// the UI thread on the progress so far:

					publishProgress(totalBytesRead);
					Log.d("TAG", "progress: " + totalBytesRead + " / " + length);
				}

				// flush the output stream - write all the pending bytes in its
				// internal buffer.
				outputStream.flush();

				// get a byte array out of the outputStream
				// theses are the bitmap bytes
				byte[] imageBytes = outputStream.toByteArray();

				// use the BitmapFactory to convert it to a bitmap
				b = BitmapFactory.decodeByteArray(imageBytes, 0, length);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (connection != null) {
					// close connection:
					connection.disconnect();
				}
				if (outputStream != null) {
					try {
						// close output stream:
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return b;

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			progressDialog.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			progressDialog.dismiss();
			ImageView poster = (ImageView) findViewById(com.example.movieproject.R.id.imageViewBody);
			if (result == null) {
				Toast.makeText(EditActivity.this, "no poster found",
						Toast.LENGTH_LONG).show();
			} else {
				poster.setVisibility(ImageView.VISIBLE);
				poster.setImageBitmap(result);
			}

			bitMapImage = result;
		}

	}
	public void saveimage(Context context, Bitmap b, String picName) {
		FileOutputStream stream;
		try {
			stream = context.openFileOutput(picName, Context.MODE_PRIVATE);
			b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public static Bitmap bit(Context context, String picName){
		Bitmap b = null;
		FileInputStream fis;
		try{
		fis = context.openFileInput(picName);
		b = BitmapFactory.decodeStream(fis);
		fis.close();
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return b;
		}
	
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		//find what you want to store:	
		EditText output = (EditText) findViewById(R.id.ShowURL);
		
		//store the state to the bundle:
		outState.putString("urlOutput", output.getText().toString());
	}
		
}

	
