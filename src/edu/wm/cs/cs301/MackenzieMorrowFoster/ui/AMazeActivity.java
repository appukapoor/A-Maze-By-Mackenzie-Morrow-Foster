package edu.wm.cs.cs301.MackenzieMorrowFoster.ui;

import edu.wm.cs.cs301.MackenzieMorrowFoster.R;
import edu.wm.cs.cs301.MackenzieMorrowFoster.R.array;
import edu.wm.cs.cs301.MackenzieMorrowFoster.R.id;
import edu.wm.cs.cs301.MackenzieMorrowFoster.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AMazeActivity extends Activity implements OnSeekBarChangeListener, OnItemSelectedListener {
	
	//create the spinners and their arrayadapters
	private static Spinner builderSpinner;
	private static Spinner driverSpinner;
	private static ArrayAdapter<CharSequence> builderAdapter;
	private static ArrayAdapter<CharSequence> driverAdapter;
	private String builderSelection;
	private String driverSelection;

	//code for seekbar taken from: http://examples.javacodegeeks.com/android/core/widget/seekbar/android-seekbar-example/
	private SeekBar seekBar;
	private TextView textView;
	int difficulty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_a_maze);
		Log.v("AMazeActivity", "Created activity_a_maze");

		//turn the top bar on the app screen into an action bar with an arrow that takes you back a page
		getActionBar().setDisplayHomeAsUpEnabled(true);

		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		textView = (TextView) findViewById(R.id.textView5);

		difficulty = 0;
		
		seekBarSetUp();
		spinnerSetUp();

	}

	/**
	 * Creates the option to go back a screen on the action bar.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Stores intent with keys and values for later access.
	 * Takes the player to the GeneratingActivity page when the button is clicked.
	 * @param view
	 */
	public void gotoGeneratingActivity(View view) {
		Intent intent = new Intent(this, GeneratingActivity.class);
		intent.putExtra("Difficulty", difficulty);
		intent.putExtra("NewMaze", true);
		intent.putExtra("Algorithm", builderSelection);
		intent.putExtra("Driver", driverSelection);

		startActivity(intent);
		Log.v("AMazeActivity", "Created the intent to move to GeneratingActivity");
	}

	/**
	 * SeekBar.
	 * For the player to choose the level of difficulty to generate the maze.
	 */
	public void seekBarSetUp() {
		//Initialize the textview with '0'.
		textView.setText("Level: " + seekBar.getProgress() + "/" + seekBar.getMax());

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			//notification that the progress level has changed.
			public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
				difficulty = progressValue;
				Log.v("AMazeActivity","Changing the SeekBar");
				Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
			}

			@Override
			//notification that the user has started a touch gesture.
			public void onStartTrackingTouch(SeekBar seekBar) {
				Log.v("AMazeActivity","Starting to change the SeekBar");
				Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
			}

			@Override
			//notification that the user has finished a touch gesture.
			public void onStopTrackingTouch(SeekBar seekBar) {
				textView.setText("Level: " + difficulty + "/" + seekBar.getMax());
				Log.v("AMazeActivity","Stopping change to the SeekBar");
				Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
			}

		});
	}
		


	/**
	 * Spinners
	 * builderSpinner is for the player to choose which algorithm will be used to build the maze.
	 * driverSpinner is for the player to choose which driver will be navigating through the maze.
	 */
	public void spinnerSetUp() {
		//instantiate the spinners
		builderSpinner = (Spinner) findViewById(R.id.spinner1);
		driverSpinner = (Spinner) findViewById(R.id.spinner2);

		//instantiate the arrayAdapters
		builderAdapter = ArrayAdapter.createFromResource(this, R.array.mazebuilder_array, android.R.layout.simple_spinner_item);
		driverAdapter = ArrayAdapter.createFromResource(this, R.array.mazedriver_array, android.R.layout.simple_spinner_item);

		builderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		driverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		builderSpinner.setAdapter(builderAdapter);
		driverSpinner.setAdapter(driverAdapter);

		builderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				builderSelection = (String) parent.getItemAtPosition(position);
				Log.v("AMazeActivity", "Spinner1, onItemSelected");
				Toast.makeText(getApplicationContext(), "Item selected", Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				Log.v("AMazeActivity", "Spinner1, onNothingSelected");
				Toast.makeText(getApplicationContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
			}
		});

		driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				driverSelection = (String) parent.getItemAtPosition(position);
				Log.v("AMazeActivity", "Spinner2, onItemSelected");
				Toast.makeText(getApplicationContext(), "Item selected", Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				Log.v("AMazeActivity", "Spinner2, onNothingSelected");
				Toast.makeText(getApplicationContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * Returns the difficulty level chose by the player
	 * @return difficulty
	 */
	public int getDifficulty() {
		return difficulty;
	}

	@Override
	//notification that the progress level has changed.
	public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
		// TODO Auto-generated method stub
	}
	@Override
	//notification that the user has started a touch gesture.
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}
	@Override
	//notification that the user has finished a touch gesture.
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}

}
