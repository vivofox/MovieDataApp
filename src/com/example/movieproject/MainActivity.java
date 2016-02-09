package com.example.movieproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	DbHandler dbhandler;
	YiftachAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ImageButton settings = (ImageButton) findViewById(R.id.settings);
		Button add = (Button) findViewById(R.id.add);

		settings.setOnClickListener(this);
		add.setOnClickListener(this);

		dbhandler = new DbHandler(this);
		Cursor cursor = dbhandler.queryAll();

		startManagingCursor(cursor);

		adapter = new YiftachAdapter(this, cursor);

		ListView list = (ListView) findViewById(R.id.listView1);
		list.setAdapter(adapter);
		registerForContextMenu(list);
		list.setOnItemClickListener(this);

	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.settings:
			openOptionsMenu();
			break;
		case R.id.add:
			showAlertDialog();
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.delete:
		dbhandler.deleteAll();
		refresh();
			break;

		case R.id.exit:
			finish();
			
			break;
		}
		return true;
	}

	private void showAlertDialog() {

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				switch (which) {
				case AlertDialog.BUTTON_POSITIVE:
					Intent intentedit = new Intent(MainActivity.this,
							Search.class);
					intentedit.setAction(intentedit.ACTION_SEARCH);
					startActivity(intentedit);
					break;

				case AlertDialog.BUTTON_NEGATIVE:
					Intent intentsearch = new Intent(MainActivity.this,
							EditActivity.class);
					intentsearch.setAction(Intent.ACTION_INSERT);
					startActivity(intentsearch);

					break;

				case AlertDialog.BUTTON_NEUTRAL:

					break;
				}

			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		AlertDialog dialog = builder
				.setNegativeButton("insertManualy", listener)
				.setPositiveButton("insertInternet", listener)
				.setNeutralButton("back", listener).setCancelable(false)
				.setTitle("what would you like to do?").create();

		dialog.show();
	}

	public class YiftachAdapter extends CursorAdapter {

		public YiftachAdapter(Context context, Cursor c) {
			super(context, c);

		}

		public void bindView(View view, Context context, Cursor curser) {
			String title = curser.getString(curser
					.getColumnIndex(DbConstants.MOVIE_NAME));
			TextView movietitle = (TextView) view
					.findViewById(android.R.id.text1);
			movietitle.setText(title);
			// date
		}

		public View newView(Context arg0, Cursor arg1, ViewGroup parent) {
			return getLayoutInflater().inflate(R.layout.movielist, parent,
					false);
		}

	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, EditActivity.class);
		intent.putExtra("id", id);
		intent.setAction(Intent.ACTION_EDIT);
		startActivity(intent);

	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		long id = info.id;

		switch (item.getItemId()) {
		case R.id.deleteMovie:
			dbhandler.delete(id);
			refresh();
			break;

		case R.id.edit:
			Intent intent = new Intent(this, EditActivity.class);
			intent.putExtra("id", id);
			intent.setAction(Intent.ACTION_EDIT);
			startActivity(intent);
			break;
		}

		return true;

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.contextmenu, menu);

	}

	public void refresh() {
		Cursor oldCursor = adapter.getCursor();
		Cursor newCursor = dbhandler.queryAll();
		startManagingCursor(newCursor);
		adapter.changeCursor(newCursor);
		stopManagingCursor(oldCursor);
		oldCursor.close();
	}

}
