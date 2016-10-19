package edu.wm.cs.cs301.MackenzieMorrowFoster.ui;

import edu.wm.cs.cs301.MackenzieMorrowFoster.R;
import edu.wm.cs.cs301.MackenzieMorrowFoster.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


/**
 * This FinishActivity class is the final screen in the AMazeByMackenzieMorrowFoster game.
 * Depending on whether the player successfully completed the maze or not, a congratulations or failed message is shown.
 * The amount of energy used to complete the maze as well as the number of steps taken are also displayed.
 * @author MackenzieFoster
 *
 */
public class FinishActivity extends Activity {
	Intent intent;
	
	TextView congratsTitle;
	TextView failTitle;
	TextView energyText;
	TextView pathText;
	
	int energyLeft;
	int energyConsumed;
	int pathTraveled;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish);
		Log.v("FinishActivity", "Created activity_finish");
		
		intent = getIntent();
		congratsTitle = (TextView) findViewById(R.id.textView1);
		failTitle = (TextView) findViewById(R.id.textView2);
		energyText = (TextView) findViewById(R.id.textView3);
		pathText = (TextView) findViewById(R.id.textView4);
		
//		energyLeft = 0;
//		pathTraveled = 0;
		energyLeft = intent.getIntExtra("Energy", 2500);
		pathTraveled = intent.getIntExtra("Path", 0);
		
		showFinishedTitle();
		showEnergyAndPath();
	}
	
	
	/**
	 * If the player successfully navigated the maze,
	 * a congratulations message is shown.
	 * Otherwise, a failed message is shown.
	 */
	public void showFinishedTitle() {
//		String finishedMessage = intent.getStringExtra("Finished");
//		if (finishedMessage.equals("Success")) {failTitle.setVisibility(View.INVISIBLE);}
		if (energyLeft != 0) {failTitle.setVisibility(View.INVISIBLE);}
		else {congratsTitle.setVisibility(View.INVISIBLE);}
	}
	
	/**
	 * Displays the amount of energy spent navigating the maze
	 * as well as the number of steps it took.
	 */
	public void showEnergyAndPath() {
		energyConsumed = 2500 - energyLeft;
		energyText.setText("Energy Consumed: " + energyConsumed);
		pathText.setText("Path Length: " + pathTraveled);
	}
	
	/**
	 * When this button is clicked, the player is redirected back to the AMazeActivity screen.
	 * @param view
	 */
	public void gotoAMazeActivity(View view) {
		Intent intent = new Intent(this, AMazeActivity.class);
		startActivity(intent);
		Log.v("FinishActivity", "Created the intent to start again and move back to AMazeActivity");
	}
	
	/**
	 * When clicked, it sends the player back to AMazeActivity
	 */
	@Override
	public void onBackPressed() {
		intent.setClass(FinishActivity.this, AMazeActivity.class);
		startActivity(intent);
		super.onBackPressed();
	}
	
}
