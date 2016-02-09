package com.example.movieproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Search extends Activity implements OnClickListener,
		OnItemClickListener {

	private static final String WEB_RESULT = "webresult";
	private static final String TAG = "SearchAcivity";
	public static final String ADDRESS = "http://www.omdbapi.com/";

	String searchQuery;

	List<MovieSearch> movieSearchList = new ArrayList<MovieSearch>();
	ArrayAdapter<MovieSearch> adapter;
	DbHandler dbHandler;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		dbHandler = new DbHandler(this);
		Button btnok = (Button) findViewById(R.id.ok);
		Button btncancel = (Button) findViewById(R.id.cancel);
		EditText edit = (EditText) findViewById(android.R.id.list);
		TextView title = (TextView) findViewById(R.id.title);

		ListView searchList = (ListView) findViewById(R.id.searchList);
		adapter = new ArrayAdapter<MovieSearch>(this, R.layout.movielist,
				android.R.id.text1, movieSearchList);
		searchList.setAdapter(adapter);

		btnok.setOnClickListener(this);
		btncancel.setOnClickListener(this);
		searchList.setOnItemClickListener(this);

	}

	public void onClick(View v) {

		EditText edit = (EditText) findViewById(android.R.id.list);

		switch (v.getId()) {
		case R.id.ok:
			new internetSearch().execute(ADDRESS);

			break;

		case R.id.cancel:
			finish();
			break;
		}

	}

	class internetSearch extends AsyncTask<String, Void, String> {

		ProgressDialog dialog = new ProgressDialog(Search.this);

		protected void onPreExecute() {

			dialog.show();
			EditText search = (EditText) findViewById(R.id.search);
			searchQuery = search.getText().toString();
		}

		protected String doInBackground(String... params) {
			BufferedReader input = null;
			HttpURLConnection connection = null;
			StringBuilder response = new StringBuilder();

			try {

				String queryString = "";
				queryString += "?s=" + URLEncoder.encode(searchQuery, "utf-8");

				URL url = new URL("http://www.omdbapi.com/" + queryString);

				connection = (HttpURLConnection) url.openConnection();

				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					// not good.
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

			return response.toString();

		}

		protected void onPostExecute(String result) {

			dialog.dismiss();
			movieSearchList.clear();

			if (result == null || result.length() == 0) {
				Log.d("Search", "RESULT" + result);
				Toast.makeText(Search.this, "no files found", Toast.LENGTH_LONG)
						.show();
			}

			else {
				try {

					JSONObject obj = new JSONObject(result);
					JSONArray arr = obj.getJSONArray("Search");

					for (int i = 0; i < arr.length(); i++) {

						JSONObject moviedata = arr.getJSONObject(i);

						String title = moviedata.getString("Title");
						String year = moviedata.getString("Year");
						String imdbId = moviedata.getString("imdbID");
						movieSearchList
								.add(new MovieSearch(title, year, imdbId));

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				adapter.notifyDataSetChanged();
			}
		}
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.d(TAG + "onItemSearch", "onItemClick start");
		MovieSearch movie = movieSearchList.get(position);
		String imdbIdFromClass = movie.getImdbId();
		Intent intent = new Intent(this, EditActivity.class);
		intent.putExtra("imdbId", imdbIdFromClass);
		intent.setAction(Intent.ACTION_WEB_SEARCH);
		startActivity(intent);
		finish();

	}
}
