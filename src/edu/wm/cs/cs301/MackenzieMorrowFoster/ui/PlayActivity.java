package edu.wm.cs.cs301.MackenzieMorrowFoster.ui;

import edu.wm.cs.cs301.MackenzieMorrowFoster.R;
import edu.wm.cs.cs301.MackenzieMorrowFoster.falstad.BasicRobot;
import edu.wm.cs.cs301.MackenzieMorrowFoster.falstad.Maze;
import edu.wm.cs.cs301.MackenzieMorrowFoster.falstad.MazePanel;
import edu.wm.cs.cs301.MackenzieMorrowFoster.falstad.Robot;
import edu.wm.cs.cs301.MackenzieMorrowFoster.falstad.RobotDriver;
import edu.wm.cs.cs301.MackenzieMorrowFoster.falstad.WallFollower;
import edu.wm.cs.cs301.MackenzieMorrowFoster.falstad.Wizard;
import edu.wm.cs.cs301.MackenzieMorrowFoster.falstad.Robot.Turn;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


/**
 * This PlayActivity class creates the functionality for the activity_play layout screen.
 * The mazepanel is displayed in the middle of this screen with a first person view of the maze shown.
 * Depending on which driver and algorithm the player chose in AMazeActivity, buttons to pause/play the robot driver
 * or buttons to manually navigate the maze are displayed along with an energy bar for the player to keep track of 
 * how much energy has been lost.
 * @author MackenzieFoster
 *
 */
public class PlayActivity extends Activity {

	Intent intent;
	Intent intentMove;
	
	String driverString;
	
	int energyLevel;
	int pathLength;
	ProgressBar progressBar;
	TextView progressText;
	
	ImageButton upArrow;
	ImageButton downArrow;
	ImageButton rightArrow;
	ImageButton leftArrow;
	Button pauseButton;
	Button playButton;
	FrameLayout frameLayout;
	
	Maze maze;
	static MazePanel mazepanel;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("PlayActivity", "Created activity_play");
		
		//get the intents from GeneratingActivity and pass them on to FinishActivity
		intent = getIntent();
		intent.setClass(PlayActivity.this, FinishActivity.class);
		intentMove = new Intent(this, FinishActivity.class);
		
		//turn the top bar on the app screen into an action bar with an arrow that takes you back a page
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		driverString = intent.getStringExtra("Driver");
		
		setContentView(R.layout.activity_play);
		
		maze = GeneratingActivity.getMaze();
		mazepanel = new MazePanel(this);
		maze.setMazePanel(mazepanel);
		maze.mazebuilder.callNewMaze();
		
		//adds out custom view to a frame layout
		frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
		frameLayout.addView(mazepanel);
		
		//keeps track of the progress bar, energy level, and path length
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		progressText = (TextView) findViewById(R.id.textView4);
		energyLevel = 2500;
		pathLength = 0;
		
		//depending on which driver the player chooses, certain buttons will be disabled.
		setUpButtons();

	}
	
	/**
	 * Sets up some of the buttons based on the driver chosen in the AMazeActivity screen.
	 * If the driver chosen is "Manual", then the arrows are visible and enabled for the player to click.
	 * If the driver chosen is anything else, then the "start" and "stop" buttons are enabled for the player to click.
	 */
	public void setUpButtons() {
		if (driverString.equals("Manual")) {
			pauseButton = (Button) findViewById(R.id.button2);
			playButton  = (Button) findViewById(R.id.button3);
			
			pauseButton.setEnabled(false);
			playButton.setEnabled(false);
			pauseButton.setVisibility(View.INVISIBLE);
			playButton.setVisibility(View.INVISIBLE);
		}
		else {
			upArrow = (ImageButton) findViewById(R.id.imageButton2);
			downArrow = (ImageButton) findViewById(R.id.imageButton4);
			rightArrow = (ImageButton) findViewById(R.id.imageButton1);
			leftArrow = (ImageButton) findViewById(R.id.imageButton3);
			
			upArrow.setEnabled(false);
			upArrow.setVisibility(View.INVISIBLE);
			downArrow.setEnabled(false);
			downArrow.setVisibility(View.INVISIBLE);
			rightArrow.setEnabled(false);
			rightArrow.setVisibility(View.INVISIBLE);
			leftArrow.setEnabled(false);
			leftArrow.setVisibility(View.INVISIBLE);
		}
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Sends the player to the FinishActivity Screen.
	 */
	public void gotoFinishActivity(View view) {
		startActivity(intentMove);
		Log.v("PlayActivity", "Created the intent to move to FinishActivity");
	}
	
	/**
	 * When the image button of the up arrow is clicked,
	 * it moves forward one spot in the maze and reduces the energy level by 5 while increasing the path length.
	 * If the maze reaches the finish point, 
	 * then the screen switches to FinishActivity with a congratulations message.
	 * If the energy level runs out, 
	 * the screen is automatically switched to FinishActivity with a failed message.
	 * @param view
	 */
	public void onUpArrowClicked(View view) {
		Log.v("PlayActivity", "Clicked Up");
		Toast.makeText(PlayActivity.this, "Moved Up", Toast.LENGTH_SHORT).show();
		maze.walk(1);
		energyLevel -= 5;
		progressBar.setProgress(energyLevel);
		progressText.setText("Energy Left: " + energyLevel + "/" + progressBar.getMax());
		pathLength++;
		
		if (maze.isFinished()) {
			intent.putExtra("Finished", "Success");
			intent.putExtra("Energy", energyLevel);
			intent.putExtra("Path", pathLength);
			startActivity(intentMove);
		}
		else if (energyLevel <= 0) {
			Log.v("onUpArrowClicked", "Ran out of energy; moving to FinishActivity");
			intent.putExtra("Finished", "Fail");
			startActivity(intentMove);
		}
	}
	
	/**
	 * Turns the robot around to face the opposite direction.
	 * If the energy level runs out, 
	 * the screen is automatically switched to FinishActivity with a failed message.
	 * @param view
	 */
	public void onDownArrowClicked(View view) {
		Log.v("PlayActivity", "Clicked Back");
		Toast.makeText(PlayActivity.this, "Moved Back", Toast.LENGTH_SHORT).show();
		energyLevel -= 5;
		progressBar.setProgress(energyLevel);
		progressText.setText("Energy Left: " + energyLevel + "/" + progressBar.getMax());
		maze.rotate(1);
		maze.rotate(1);
		
		if (energyLevel <= 0) {
			Log.v("onDownArrowClicked", "Ran out of energy; moving to FinishActivity");
			intent.putExtra("Finished", "Fail");
			startActivity(intentMove);
		}
	}
	
	/**
	 * Turns the robot to face right.
	 * If the energy level runs out, 
	 * the screen is automatically switched to FinishActivity with a failed message.
	 * @param view
	 */
	public void onRightArrowClicked(View view) {
		Log.v("PlayActivity", "Clicked Right");
		Toast.makeText(PlayActivity.this, "Turned right", Toast.LENGTH_SHORT).show();
		energyLevel -= 3;
		progressBar.setProgress(energyLevel);
		progressText.setText("Energy Left: " + energyLevel + "/" + progressBar.getMax());
		maze.rotate(-1);
		
		if (energyLevel <= 0) {
			Log.v("onRightArrowClicked", "Ran out of energy; moving to FinishActivity");
			intent.putExtra("Finished", "Fail");
			startActivity(intentMove);
		}
	}
	
	/**
	 * Turns the robot to face left.
	 * If the energy level runs out, 
	 * the screen is automatically switched to FinishActivity with a failed message.
	 * @param view
	 */
	public void onLeftArrowClicked(View view) {
		Log.v("PlayActivity", "Clicked Left");
		Toast.makeText(PlayActivity.this, "Turned left", Toast.LENGTH_SHORT).show();
		energyLevel -= 3;
		progressBar.setProgress(energyLevel);
		progressText.setText("Energy Left: " + energyLevel + "/" + progressBar.getMax());
		maze.rotate(1);
		
		if (energyLevel <= 0) {
			Log.v("onRightArrowClicked", "Ran out of energy; moving to FinishActivity");
			intent.putExtra("Finished", "Fail");
			startActivity(intentMove);
		}
	}
	
	
	/**
	 * Sets up the toggle switch to toggle on/off maps.
	 */
	public void onMazeToggleClicked(View view) {
		boolean on = ((ToggleButton) view).isChecked();
	    if (on) {
	        Log.v("PlayActivity", "ToggleButton is on");
	        Toast.makeText(getApplicationContext(), "Toggle On", Toast.LENGTH_SHORT).show();
	        maze.keyDown('m');
	    } else {
	        Log.v("PlayActivity", "ToggleButton is off");
	        Toast.makeText(getApplicationContext(), "Toggle Off", Toast.LENGTH_SHORT).show();
	        maze.keyDown('m');
	    }
	}
	
	/**
	 * Sets up the toggle switch to toggle on/off the solution to the maze.
	 */
	public void onSolutionToggleClicked(View view) {
		boolean on = ((ToggleButton) view).isChecked();
	    if (on) {
	        Log.v("PlayActivity", "Solution Toggle is on");
	        Toast.makeText(getApplicationContext(), "Solution On", Toast.LENGTH_SHORT).show();
	        maze.keyDown('s');
	    } else {
	        Log.v("PlayActivity", "Solution Toggle is off");
	        Toast.makeText(getApplicationContext(), "Solution Off", Toast.LENGTH_SHORT).show();
	        maze.keyDown('s');
	    }
	}
	
	
	/**
	 * Sets up the toggle switch to toggle on/off the walls.
	 */
	public void onWallToggleClicked(View view) {
		boolean on = ((ToggleButton) view).isChecked();
	    if (on) {
	        Log.v("PlayActivity", "Walls Toggle is on");
	        Toast.makeText(getApplicationContext(), "Walls On", Toast.LENGTH_SHORT).show();
	        maze.keyDown('z');
	    } else {
	        Log.v("PlayActivity", "Walls Toggle is off");
	        Toast.makeText(getApplicationContext(), "Walls Off", Toast.LENGTH_SHORT).show();
	        maze.keyDown('z');
	    }
	}
	
	
	/**
	 * Becomes visible when the robot is moving so that the player can pause the robot.
	 */
	public void onPauseClicked(View view) {
		Log.v("PlayActivity", "onPauseClicked: Paused");
		Toast.makeText(PlayActivity.this, "Paused", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Becomes visible when the robot is paused so that the player can resume moving the robot.
	 */
	public void onPlayClicked(View view) {
		Log.v("PlayActivity", "onPlayClicked: Playing");
		Toast.makeText(PlayActivity.this, "Playing", Toast.LENGTH_SHORT).show();
	}
	
	
	/**
	 * When clicked, it sends the player back to AMazeActivity.
	 */
	@Override
	public void onBackPressed() {
		intent.setClass(PlayActivity.this, AMazeActivity.class);
		startActivity(intent);
		super.onBackPressed();
	}
}
