package edu.wm.cs.cs301.MackenzieMorrowFoster.ui;

import java.util.Random;

import edu.wm.cs.cs301.MackenzieMorrowFoster.R;
import edu.wm.cs.cs301.MackenzieMorrowFoster.R.id;
import edu.wm.cs.cs301.MackenzieMorrowFoster.R.layout;
import edu.wm.cs.cs301.MackenzieMorrowFoster.falstad.Maze;
import edu.wm.cs.cs301.MackenzieMorrowFoster.falstad.MazeBuilder;
import edu.wm.cs.cs301.MackenzieMorrowFoster.falstad.MazeBuilderKruskal;
import edu.wm.cs.cs301.MackenzieMorrowFoster.falstad.MazeBuilderPrim;
import edu.wm.cs.cs301.MackenzieMorrowFoster.falstad.MazePanel;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


/**
 * This GeneratingActivity class uses the choices selected by the player in AMazeActivity to create a maze.
 * However, the generation of the maze is not shown and is done in the background while the player
 * only sees a progress bar being filled. Once the maze is done being generating, the player is automatically
 * taken to the PlayActivity screen.
 * @author MackenzieFoster
 *
 */
public class GeneratingActivity extends Activity {
	//code for progressbar taken from: http://www.compiletimeerror.com/2013/09/android-progress-bar-example.html#.VY-s_WB3qgE
	private ProgressBar progressBar;
	private int progressStatus = 0;
	private Handler handler = new Handler();
	private Intent intent;
	
	public static Maze maze;
	public static MazePanel mazepanel;
	public Bitmap myBit;
	
	public MazeBuilder mazebuilder;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generate);
		Log.v("GeneratingActivity", "Created activity_generate");

		//get the intents from AMazeActivity and pass them on to PlayActivity
		intent = getIntent();
		intent.setClass(GeneratingActivity.this, PlayActivity.class);

		//turn the top bar on the app screen into an action bar with an arrow that takes you back a page
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		//call in the methods to set up the maze and progress bar.
		setUpMaze();
		setUpProgressBar();
		
	}	
	
	/**
	 * Based on the options selected back in AMazeActivity, 
	 * a maze is created using the driver the player selected,
	 * the algorithm the player selected, and the difficulty that the player selected.
	 */
	public void setUpMaze() {
		String builder = intent.getStringExtra("Algorithm");
		if (builder.equals("Falstad")) {
			maze = new Maze();
			Log.v("GeneratingActivity", "Creating maze using Falstad's algorithm");
		}
		else if (builder.equals("Prim")) {
			maze = new Maze(1);
			Log.v("GeneratingActivity", "Creating maze using Prim's algorithm");
		}
		else if (builder.equals("Kruskal")) {
			maze = new Maze(2);
			Log.v("GeneratingActivity", "Creating maze using Kruskal's algorithm");
		}
		else {
			Random r = new Random();
			int randomInt = r.nextInt(3-0) + 0;
			maze = new Maze(randomInt);
			Log.v("GeneratingActivity", "Creating maze using a random algorithm");
		}
		maze.init();
		
		boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
		int frameWidth = isLandscape ? 1280 : 786;
		int frameHeight = isLandscape ? 786 : 1280;
		
		myBit = Bitmap.createBitmap(frameWidth, frameHeight, Config.ARGB_8888);
		mazepanel = new MazePanel(this);
		
		int difficulty = intent.getIntExtra("Difficulty", 0);
		maze.build(difficulty);
	}
	
	
	/**
	 * Setting up a progressBar to know when the maze is done generating.
	 */
	public void setUpProgressBar() {
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);

		// Start long running operation in a background thread
		new Thread(new Runnable() {
			public void run() {
				while (progressStatus < maze.mazebuilder.getMaxPercentDone()) {
					progressStatus = maze.getPercentdone();
					// Update the progress bar and display the current value in the text view
					handler.post(new Runnable() {
						public void run() {
							progressBar.setProgress(progressStatus);
						}
					});
					try {
						//sleeps for 50 milliseconds
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				startActivity(intent);
			}
		}).start();
	}
	
	
	
	/**
	 * Takes the player back a screen when clicked. On the action bar.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Simple getter to access the maze.
	 * @return
	 */
	public static Maze getMaze() {
		return maze;
	}
}