package edu.wm.cs.cs301.MackenzieMorrowFoster.falstad;

public class BasicRobot implements Robot {
	// Constants for robot configuration
	private final float ENERGY_FULL_LEVEL = 2500;
	private final float ENERGY_FOR_FULL_ROTATION = 12;
	private final float ENERGY_FOR_STEP_FORWARD = 5;
	private final float ENERGY_FOR_DISTANCE_SENSING = 1;
	
	private float battery_level;
	private Maze my_maze;
	// TODO: my_pos and my_dir are redundant if in sync
	// with my_maze position and direction
	// double check and remove as redundancy is inefficient and risky
	private int[] my_pos;
	private int[] my_dir;
	private boolean stopped;
	private int num_steps;
	
	private boolean forward_sensor;
	private boolean backward_sensor;
	private boolean left_sensor;
	private boolean right_sensor;
	private boolean room_sensor;

	/**
	 * Default robot configuration has only a left and forward sensor. 
	 */
	public BasicRobot() {
		my_pos = new int[2];
		my_dir = new int[2];
		battery_level = ENERGY_FULL_LEVEL;
		my_maze = null;
		stopped = false;
		num_steps = 0;
		
		forward_sensor = true;
		left_sensor = true;
		right_sensor = false;
		backward_sensor = false;
		room_sensor = true;
	}

	/**
	 * Allow setting the use of the different direction sensors
	 * @param forward
	 * @param backward
	 * @param left
	 * @param right
	 * @param room
	 */
	public BasicRobot(boolean forward, boolean backward, boolean left, boolean right, boolean room) {
		this();
		forward_sensor = forward;
		backward_sensor = backward;
		left_sensor = left;
		right_sensor = right;	
		room_sensor = room;
	}
	
	@Override
	public void rotate(Turn turn) throws Exception {
		if (hasStopped()) {
			throw new Exception("BasicRobot.rotate: robot stopped");
		}
			
		switch (turn) {
		case AROUND: my_maze.rotate(1);
					 my_maze.rotate(1);
					 setBatteryLevel(getBatteryLevel() - (float) 0.5 * getEnergyForFullRotation());
					 break;
		case LEFT:   my_maze.rotate(1);
					 setBatteryLevel(getBatteryLevel() - (float) 0.25 * getEnergyForFullRotation());
					 break;
		case RIGHT:  my_maze.rotate(-1);
					 setBatteryLevel(getBatteryLevel() - (float) 0.25 * getEnergyForFullRotation());
					 break;
		default: 	 break;
		}
		if (hasStopped()) {
			throw new Exception("BasicRobot.rotate: robot run out of energy: stopped");
		}
		// keep maze direction and redundant local copy in sync
		my_dir = getCurrentDirection();
		
	}
	
	/**
	 * Determined whether robot is facing a wall
	 * @return true if the forward position is directly next to a wall
	 */
	private boolean isFacingWall() {
		return my_maze.hasWall(my_pos[0], my_pos[1], getBit(my_dir));
	}
	
	@Override
	public void move(int distance) throws Exception {
		boolean finished = false ;
		for (int i = 0; i < distance; i++) {
			// check if we going to run into a wall
			if ( isFacingWall() ) {
				throw new Exception("BasicRobot.move: hitting wall");
			}
			// check if we are at the exit, better stop here
			// we refuse to move out of bounds
			if (isAtGoal()) {
				System.out.println("BasicRobot.move: reaching exit position, halt here") ;
				finished = true ;
			}
			// perform step forward
			my_maze.walk(1);
			
			// what happens if we reach the exit
			/*
			boolean atExit = isAtGoal();
			try {
				my_pos = getCurrentPosition();
			}
			catch (Exception e) {
				if (!atExit)
					throw e;
			}
			*/
			// update position unless we are done and left the maze
			// direction is unchanged
			if (!finished)
				my_pos = getCurrentPosition();
			// update statistics
			num_steps++;
			setBatteryLevel(getBatteryLevel() - getEnergyForStepForward());
			if (hasStopped())
				throw new Exception("BasicRobot.move: robot ran out of energy, stopped");
		}
	}

	/**
	 * Updates the hasStopped status of the robot
	 */
	private void updateStopped() {
		//System.out.println("Battery level: " + getBatteryLevel()) ;
		if (getBatteryLevel() <= 0) {
			stopped = true ;
		}
	}
	
	@Override
	public int[] getCurrentPosition() throws Exception {
		int[] pos = my_maze.getCurrentPosition();
		
		if (validPosition(pos[0], pos[1])) {
			my_pos = pos.clone() ;
			return pos;
		}
	
		throw new Exception();
	}

	/**
	 * Determines whether the given coordinated designate a valid location on the maze
	 * @param x
	 * @param y
	 * @return true if the position is valid; false otherwise
	 */
	private boolean validPosition(int x, int y) {
		return my_maze.withinBounds(x, y);
	}
	
	@Override
	public void setMaze(Maze maze) {
		if (maze != null) {
			if (maze.isConfigured()) {
				my_maze = maze;
				try {
					my_pos = getCurrentPosition();
				}
				catch (Exception e) {}
				my_dir = getCurrentDirection();
				// reset other instance variables as well
				battery_level = ENERGY_FULL_LEVEL;
				stopped = false;
				num_steps = 0;
			}
		}
	}

	@Override
	public boolean isAtGoal() {
		return my_maze.isAtExit(my_pos[0], my_pos[1]);
	}

	@Override
	public boolean canSeeGoal(Direction direction) throws UnsupportedOperationException {
		return (Integer.MAX_VALUE == this.distanceToObstacle(direction)) ;
	}

	@Override
	public boolean isInsideRoom() throws UnsupportedOperationException {
		if (hasRoomSensor())
			return my_maze.isInRoom();
		
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasRoomSensor() {
		return room_sensor;
	}
	
	@Override
	public int[] getCurrentDirection() {
		return my_maze.getCurrentDirection();
	}

	@Override
	public float getBatteryLevel() {
		return battery_level;
	}

	@Override
	public void setBatteryLevel(float level) {
		battery_level = level;
		my_maze.updateRobotState(level, num_steps);
		updateStopped();
	}

	@Override
	public float getEnergyForFullRotation() {
		return ENERGY_FOR_FULL_ROTATION;
	}

	@Override
	public float getEnergyForStepForward() {
		return ENERGY_FOR_STEP_FORWARD;
	}

	@Override
	public boolean hasStopped() {
		return stopped;
	}

	private boolean hasBeenSeen(int x, int y) {
		return my_maze.hasBeenSeen(x, y);
	}
	
	@Override
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
		if (!hasDistanceSensor(direction))
			throw new UnsupportedOperationException();
		// obtain translated direction
		int[] dir = adjustDirection(direction, my_dir);
		
		int[] pos = my_pos.clone() ;
		
		// check if we have energy to do this
		setBatteryLevel(getBatteryLevel() - ENERGY_FOR_DISTANCE_SENSING);
		
		
		// calculate distance
		int distance = 0; // if we are directly facing a wall this is 0
		int bit = getBit(dir) ;
		while(!my_maze.hasWall(pos[0], pos[1], bit)) {
			// move to neighbor, update distance
			pos[0] = pos[0] + dir[0];
			pos[1] = pos[1] + dir[1];							
			distance++;
			// check if next step is still inside maze
			//if(!my_maze.withinBounds(pos[0] + dir[0], pos[1] + dir[1])) {
			if(!my_maze.withinBounds(pos[0], pos[1])) {
				// we are going through the exit here
				return Integer.MAX_VALUE ;
			}
		}
		return distance;
	}

	/**
	 * For the given direction adjust the array from the current direction
	 * to this direction.
	 * @param direction is relative to current direction
	 * @param dir is input/output parameter, absolute current direction
	 */
	private int[] adjustDirection(Direction direction, int[] currentDirection) {
		int[] result = currentDirection.clone() ;
		switch (direction) {
			case FORWARD: 
				break;
			case BACKWARD: 
				result[0] = -1*result[0];
				result[1] = -1*result[1];
				break;
			case RIGHT:
				if ((result[0]) == 0) {
					result[0] = result[1];
					result[1] = 0;
				}
				else {
					result[1] = -1*result[0];
					result[0] = 0;
				}
				break;
			case LEFT:
				if ((result[0]) == 0) {
					result[0] = -1*result[1];
					result[1] = 0;
				}
				else {
					result[1] = result[0];
					result[0] = 0;
				}
				break;
			default: throw new UnsupportedOperationException();
		}
		return result;
	}
	
	@Override
	public boolean hasDistanceSensor(Direction direction) {
		/*
		if ((direction == Direction.BACKWARD && backward_sensor) ||
				(direction == Direction.RIGHT && right_sensor) ||
				(direction == Direction.LEFT && left_sensor) ||
				(direction == Direction.FORWARD && forward_sensor)) {
			return true;
		}
		
		return false;
		*/
		return ((direction == Direction.BACKWARD && backward_sensor) ||
				(direction == Direction.RIGHT && right_sensor) ||
				(direction == Direction.LEFT && left_sensor) ||
				(direction == Direction.FORWARD && forward_sensor)) ;
	}

	/**
	 * encodes (dx,dy) into a bit pattern for right, left, top, bottom direction
	 * @param dx direction x, in { -1, 0, 1} obtained from dirsx[]
	 * @param dy direction y, in { -1, 0, 1} obtained from dirsy[]
	 * @return bit pattern, 0 in case of an error
	 */
	private int getBit(int dx, int dy) {
		int bit = 0;
		switch (dx + dy * 2) {
		case 1:  bit = Constants.CW_RIGHT; break; //  dx=1,  dy=0
		case -1: bit = Constants.CW_LEFT;  break; //  dx=-1, dy=0
		case 2:  bit = Constants.CW_BOT;   break; //  dx=0,  dy=1
		case -2: bit = Constants.CW_TOP;   break; //  dx=0,  dy=-1
		}
		return bit;
	}
	// convenience method
	private int getBit(int[] dir) { 
		return getBit(dir[0],dir[1]) ;
	}
}
