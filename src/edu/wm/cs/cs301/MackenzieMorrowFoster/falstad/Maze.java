package edu.wm.cs.cs301.MackenzieMorrowFoster.falstad;

//import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

import edu.wm.cs.cs301.MackenzieMorrowFoster.ui.GeneratingActivity;
import android.util.EventLog.Event;
import android.view.View;

/**
 * Class handles the user interaction for the maze. 
 * It implements a state-dependent behavior that controls the display and reacts to key board input from a user. 
 * After refactoring the original code from an applet into a panel, it is wrapped by a MazeApplication to be a java application 
 * and a MazeApp to be an applet for a web browser. At this point user keyboard input is first dealt with a key listener
 * and then handed over to a Maze object by way of the keyDown method.
 *
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
// MEMO: original code: public class Maze extends Applet {
//public class Maze extends Panel {
public class Maze  {

	// Model View Controller pattern, the model needs to know the viewers
	// however, all viewers share the same graphics to draw on, such that the share graphics
	// are administered by the Maze object
	final protected ArrayList<Viewer> views = new ArrayList<Viewer>() ; 
	MazePanel panel ; // graphics to draw on, shared by all views
		

	protected int state;			// keeps track of the current GUI state, one of STATE_TITLE,...,STATE_FINISH, mainly used in redraw()
	// possible values are defined in Constants
	// user can navigate 
	// title -> generating -(escape) -> title
	// title -> generation -> play -(escape)-> title
	// title -> generation -> play -> finish -> title
	// STATE_PLAY is the main state where the user can navigate through the maze in a first person view

	protected int percentdone = 0; // describes progress during generation phase
	protected boolean showMaze;		 	// toggle switch to show overall maze on screen
	protected boolean showSolution;		// toggle switch to show solution in overall maze on screen
	protected boolean solving;			// toggle switch 
	protected boolean mapMode; // true: display map of maze, false: do not display map of maze
	// map_mode is toggled by user keyboard input, causes a call to draw_map during play mode

	//static final int viewz = 50;    
	int viewx, viewy, angle;
	int dx, dy;  // current direction
	int px, py ; // current position on maze grid (x,y)
	int walkStep;
	int viewdx, viewdy; // current view direction

	float battery_level;
	int num_steps;
	
	boolean configured;
	// debug stuff
	boolean deepdebug = false;
	boolean allVisible = false;
	boolean newGame = false;

	// properties of the current maze
	int mazew; // width of maze
	int mazeh; // height of maze
	Cells mazecells ; // maze as a matrix of cells which keep track of the location of walls
	Distance mazedists ; // a matrix with distance values for each cell towards the exit
	Cells seencells ; // a matrix with cells to memorize which cells are visible from the current point of view
	// the FirstPersonDrawer obtains this information and the MapDrawer uses it for highlighting currently visible walls on the map
	BSPNode rootnode ; // a binary tree type search data structure to quickly locate a subset of segments
	// a segment is a continuous sequence of walls in vertical or horizontal direction
	// a subset of segments need to be quickly identified for drawing
	// the BSP tree partitions the set of all segments and provides a binary search tree for the partitions
	

	// Mazebuilder is used to calculate a new maze together with a solution
	// The maze is computed in a separate thread. It is started in the local Build method.
	// The calculation communicates back by calling the local newMaze() method.
	public MazeBuilder mazebuilder;

	
	// fixing a value matching the escape key
	final int ESCAPE = 27;

	// generation method used to compute a maze
	int method = 0 ; // 0 : default method, Falstad's original code
	// method == 1: Prim's algorithm

	int zscale = Constants.VIEW_HEIGHT/2;

	protected RangeSet rset;
	
	RobotDriver driver ;
	Robot robot ;
	GeneratingActivity generateMe;
	boolean finished;
	
	
	/**
	 * Constructor
	 */
	public Maze() {
		super() ;
		battery_level = Integer.MAX_VALUE;
		num_steps = Integer.MAX_VALUE;
		configured = false;
		finished = false;
		
		generateMe = new GeneratingActivity();
		panel = generateMe.mazepanel;
	}
	/**
	 * Constructor that also selects a particular generation method
	 */
	public Maze(int method)
	{
		super() ;
		battery_level = Integer.MAX_VALUE;
		num_steps = Integer.MAX_VALUE;
		configured = false;
		finished = false;
		// 0 is default, do not accept other settings but 0 and 1
		if (1 == method)
			this.method = 1 ;
		else if (2 == method)
			this.method = 2 ;
		panel = generateMe.mazepanel ;
	}
	/**
	 * Method to initialize internal attributes. Called separately from the constructor. 
	 */
	public void init() {
//		state = Constants.STATE_TITLE;
		rset = new RangeSet();
//		panel.initBufferImage() ;
//		addView(new MazeView(this)) ;
//		notifyViewerRedraw() ;
	}
	
	/**
	 * Method to set the robot and robot driver
	 */
	public void setRobotAndDriver(Robot robot, RobotDriver driver) {
		this.robot = robot ;
		this.driver = driver ;
	}
	/**
	 * Method obtains a new Mazebuilder and has it compute new maze, 
	 * it is only used in keyDown()
	 * @param skill level determines the width, height and number of rooms for the new maze
	 */
	public void build(int skill) {
		// switch screen
		state = Constants.STATE_GENERATING;
		percentdone = 0;
//		notifyViewerRedraw() ;
		// select generation method
		switch(method){
		case 2 : mazebuilder = new MazeBuilderKruskal();
		break;
		case 1 : mazebuilder = new MazeBuilderPrim(); // generate with Prim's algorithm
		break ;
		case 0: mazebuilder = new MazeBuilder();
		break;
		default : 
		break ;
		}
		// adjust settings and launch generation in a separate thread
		mazew = Constants.SKILL_X[skill];
		mazeh = Constants.SKILL_Y[skill];
		mazebuilder.build(this, mazew, mazeh, Constants.SKILL_ROOMS[skill], Constants.SKILL_PARTCT[skill]);
		// mazebuilder performs in a separate thread and calls back by calling newMaze() to return newly generated maze
	}
	
	/**
	 * Call back method for MazeBuilder to communicate newly generated maze as reaction to a call to build()
	 * @param root node for traversals, used for the first person perspective
	 * @param cells encodes the maze with its walls and border
	 * @param dists encodes the solution by providing distances to the exit for each position in the maze
	 * @param startx current position, x coordinate
	 * @param starty current position, y coordinate
	 */
	public void newMaze(BSPNode root, Cells c, Distance dists, int startx, int starty) {
		if (Cells.deepdebugWall)
		{   // for debugging: dump the sequence of all deleted walls to a log file
			// This reveals how the maze was generated
			c.saveLogFile(Cells.deepedebugWallFileName);
		}
		// adjust internal state of maze model
		showMaze = showSolution = solving = false;
		mazecells = c ;
		mazedists = dists;
		seencells = new Cells(mazew+1,mazeh+1) ;
		rootnode = root ;
		setCurrentDirection(1, 0) ;
		setCurrentPosition(startx,starty) ;
		walkStep = 0;
		viewdx = dx<<16; 
		viewdy = dy<<16;
		angle = 0;
		mapMode = false;
		// set the current state for the state-dependent behavior
		state = Constants.STATE_PLAY;
		configured = true;
		cleanViews() ;
		// register views for the new maze
		// mazew and mazeh have been set in build() method before mazebuider was called to generate a new maze.
		// reset map_scale in mapdrawer to a value of 10
		addView(new FirstPersonDrawer(Constants.VIEW_WIDTH,Constants.VIEW_HEIGHT,
				Constants.MAP_UNIT,Constants.STEP_SIZE, mazecells, seencells, 10, mazedists.getDists(), mazew, mazeh, root, this)) ;
		// order of registration matters, code executed in order of appearance!
		addView(new MapDrawer(Constants.VIEW_WIDTH,Constants.VIEW_HEIGHT,Constants.MAP_UNIT,Constants.STEP_SIZE, mazecells, seencells, 10, mazedists.getDists(), mazew, mazeh, this)) ;

		// notify viewers
		notifyViewerRedraw() ;
		
		// start driver if set
		if (null != driver) {
//		String driverString = String.valueOf(driver);
//		if (!driverString.equals("Manual")) {
			System.out.println("Maze.newMaze: starting driver") ;
			robot.setMaze(this);
			driver.setRobot(robot);
			driver.setDistance(mazedists);
			
			this.keyDown('m') ;
			this.keyDown('z') ;
			this.keyDown('s') ;
			try {
				driver.drive2Exit() ;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (robot.hasStopped()) {
					// trigger switch to exit screen
					state = Constants.STATE_FINISH;
					notifyViewerRedraw() ;
				}
			}
		}
	}

	public boolean isConfigured() {
		return configured;
	}
	/////////////////////////////// Methods for the Model-View-Controller Pattern /////////////////////////////
	/**
	 * Register a view
	 */
	public void addView(Viewer view) {
		views.add((Viewer) view) ;
	}
	/**
	 * Unregister a view
	 */
	public void removeView(Viewer view) {
		views.remove(view) ;
	}
	/**
	 * Remove obsolete FirstPersonDrawer and MapDrawer
	 */
	protected void cleanViews() {
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = (Viewer) it.next() ;
			if ((v instanceof FirstPersonDrawer)||(v instanceof MapDrawer))
			{
				//System.out.println("Removing " + v);
				it.remove() ;
			}
		}

	}
	/**
	 * Notify all registered viewers to redraw their graphics
	 */
	public void notifyViewerRedraw() {
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = it.next() ;
			// viewers draw on the buffer graphics
			v.redraw(Constants.STATE_PLAY, px, py, viewdx, viewdy, walkStep, Constants.VIEW_OFFSET, rset, angle, battery_level, num_steps) ;
		}
		// update the screen with the buffer graphics
		panel.update() ;
	}
	/** 
	 * Notify all registered viewers to increment the map scale
	 */
	private void notifyViewerIncrementMapScale() {
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = (Viewer) it.next() ;
			v.incrementMapScale() ;
		}
		// update the screen with the buffer graphics
		panel.update() ;
	}
	/** 
	 * Notify all registered viewers to decrement the map scale
	 */
	private void notifyViewerDecrementMapScale() {
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = it.next() ;
			v.decrementMapScale() ;
		}
		// update the screen with the buffer graphics
		panel.update() ;
	}
	////////////////////////////// get methods ///////////////////////////////////////////////////////////////
	boolean isInMapMode() { 
		return mapMode ; 
	} 
	boolean isInShowMazeMode() { 
		return showMaze ; 
	} 
	boolean isInShowSolutionMode() { 
		return showSolution ; 
	} 
	public String getPercentDone(){
		return String.valueOf(percentdone) ;
	}
	public int getPercentdone() {
		return percentdone;
	}
//	public Panel getPanel() {
//		return panel ;
//	}
	
	////////////////////////////// set methods ///////////////////////////////////////////////////////////////
	////////////////////////////// Actions that can be performed on the maze model ///////////////////////////
	public void setCurrentPosition(int x, int y)
	{
		px = x ;
		py = y ;
	}
	
	public int[] getCurrentPosition() {
		int[] pos = new int[2];
		pos[0] = px;
		pos[1] = py;
		return pos;
	}
	
	public boolean isAtExit(int x, int y) {
		return mazecells.isExitPosition(x, y);
	}
	
	public boolean isInRoom() {
		return mazecells.isInRoom(px, py);
	}
	
	public void setCurrentDirection(int x, int y)
	{
		dx = x ;
		dy = y ;
	}
	
	public int[] getCurrentDirection() {
		int[] dir = new int[2];
		dir[0] = dx;
		dir[1] = dy;
		return dir;
	}
	
	void buildInterrupted() {
		state = Constants.STATE_TITLE;
		notifyViewerRedraw() ;
		mazebuilder = null;
	}

	final double radify(int x) {
		return x*Math.PI/180;
	}


	/**
	 * Allows external increase to percentage in generating mode with subsequence graphics update
	 * @param pc gives the new percentage on a range [0,100]
	 * @return true if percentage was updated, false otherwise
	 */
	public boolean increasePercentage(int pc) {
		if (percentdone < pc && pc < 100) {
			percentdone = pc;
//			if (state == Constants.STATE_GENERATING)
//			{
//				notifyViewerRedraw() ;
//			}
//			else
//				dbg("Warning: Receiving update request for increasePercentage while not in generating state, skip redraw.") ;
			return true ;
		}
		return false ;
	}



	/////////////////////// Methods for debugging ////////////////////////////////
	private void dbg(String str) {
		//System.out.println(str);
	}

	private void logPosition() {
		if (!deepdebug)
			return;
		dbg("x="+viewx/Constants.MAP_UNIT+" ("+
				viewx+") y="+viewy/Constants.MAP_UNIT+" ("+viewy+") ang="+
				angle+" dx="+dx+" dy="+dy+" "+viewdx+" "+viewdy);
	}
	///////////////////////////////////////////////////////////////////////////////

	/**
	 * Helper method for walk()
	 * @param dir
	 * @return true if there is no wall in this direction
	 */
	public boolean checkMove(int dir) {
		// obtain appropriate index for direction (CW_BOT, CW_TOP ...) 
		// for given direction parameter
		int a = angle/90;
		if (dir == -1)
			a = (a+2) & 3; // TODO: check why this works
		// check if cell has walls in this direction
		// returns true if there are no walls in this direction
		return mazecells.hasMaskedBitsFalse(px, py, Constants.MASKS[a]) ;
	}
	
	public boolean withinBounds(int x, int y) {
		return x >= 0 && y >= 0 && x < mazew && y < mazeh;
	}
	
	public boolean checkMove(int dir, int x, int y) {
		return mazecells.hasMaskedBitsFalse(x, y, dir) ;
	}

	private void rotateStep() {
		angle = (angle+1800) % 360;
		viewdx = (int) (Math.cos(radify(angle))*(1<<16));
		viewdy = (int) (Math.sin(radify(angle))*(1<<16));
		moveStep();
	}
	

	/**
	 * Used to get information of battery level from robot if any
	 * @param blvl: float representing battery level
	 * @param ns: int representing number of cell traversed
	 */
	public void updateRobotState(float blvl, int ns) {
		battery_level = blvl;
		num_steps = ns;
	}
	
	public boolean hasBeenSeen(int x, int y) {
		return seencells.hasMaskedBitsTrue(x, y, 1);
	}
	
	public boolean hasWall(int x, int y, int bitmask) {
		return mazecells.hasMaskedBitsTrue(x, y, bitmask);
	}
	
	private void moveStep() {
		notifyViewerRedraw() ;
		try {
			Thread.currentThread().sleep(25);
		} catch (Exception e) { }
	}

	private void rotateFinish() {
		setCurrentDirection((int) Math.cos(radify(angle)), (int) Math.sin(radify(angle))) ;
		logPosition();
	}

	
	private void walkFinish(int dir) {
		setCurrentPosition(px + dir*dx, py + dir*dy) ;
		
		if (isEndPosition(px,py)) {
//			state = Constants.STATE_FINISH;
//			notifyViewerRedraw() ;
			finished = true;
		}
		walkStep = 0;
		logPosition();
	}
	
	public boolean isFinished() {
		if (finished == true) {return true;}
		else {return false;}
	}
	

	/**
	 * checks if the given position is outside the maze
	 * @param x
	 * @param y
	 * @return true if position is outside, false otherwise
	 */
	private boolean isEndPosition(int x, int y) {
		return x < 0 || y < 0 || x >= mazew || y >= mazeh;
	}


	public synchronized void walk(int dir) {
		if (!checkMove(dir))
			return;
		for (int step = 0; step != 4; step++) {
			walkStep += dir;
			moveStep();
		}
		walkFinish(dir);
	}
	


	public synchronized void rotate(int dir) {
		final int originalAngle = angle;
		final int steps = 4;

		for (int i = 0; i != steps; i++) {
			angle = originalAngle + dir*(90*(i+1))/steps;
			rotateStep();
		}
		rotateFinish();
	}



	/**
	 * Method incorporates all reactions to keyboard input in original code, 
	 * after refactoring, Java Applet and Java Application wrapper call this method to communicate input.
	 */
	public boolean keyDown(int key) {
		// possible inputs for key: unicode char value, 0-9, A-Z, Escape, 'k','j','h','l'
		switch (state) {
		// if screen shows title page, keys describe level of expertise
		// create a maze according to the user's selected level
		case Constants.STATE_TITLE:
			if (key >= '0' && key <= '9') {
				build(key - '0');
				break;
			}
			if (key >= 'a' && key <= 'f') {
				build(key - 'a' + 10);
				break;
			}
			break;
		// if we are currently generating a maze, recognize interrupt signal (ESCAPE key)
		// to stop generation of current maze
		case Constants.STATE_GENERATING:
			if (key == ESCAPE) {
				mazebuilder.interrupt();
				buildInterrupted();
			}
			break;
		// if user explores maze, 
		// react to input for directions and interrupt signal (ESCAPE key)	
		// react to input for displaying a map of the current path or of the overall maze (on/off toggle switch)
		// react to input to display solution (on/off toggle switch)
		// react to input to increase/reduce map scale
		case Constants.STATE_PLAY:
			switch (key) {
			case '\t': case 'm':
				mapMode = !mapMode; 		
				notifyViewerRedraw() ; 
				break;
			case 'z':
				showMaze = !showMaze; 		
				notifyViewerRedraw() ; 
				break;
			case 's':
				showSolution = !showSolution; 		
				notifyViewerRedraw() ;
				break;
			}
			break;
		} 
			return true;
	}




	public void setMazePanel(MazePanel mazepanel) {
		panel = mazepanel;
	}
	


}
