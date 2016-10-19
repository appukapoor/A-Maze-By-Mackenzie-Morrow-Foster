package edu.wm.cs.cs301.MackenzieMorrowFoster.ui;

import edu.wm.cs.cs301.MackenzieMorrowFoster.R;
import edu.wm.cs.cs301.MackenzieMorrowFoster.R.id;
import edu.wm.cs.cs301.MackenzieMorrowFoster.R.layout;
import edu.wm.cs.cs301.MackenzieMorrowFoster.R.menu;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Creates the option to go back a screen on the action bar.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Creates the intent to move to the AMazeActivity screen when this button is clicked.
	 * @param view
	 */
	public void gotoAMazeActivity(View view) {
		Intent intent = new Intent(this, AMazeActivity.class);
		startActivity(intent);
		Log.v("MainActivity", "Created the intent to move to AMazeActivity");
	}
}
