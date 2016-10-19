package edu.wm.cs.cs301.MackenzieMorrowFoster.falstad;

import edu.wm.cs.cs301.MackenzieMorrowFoster.falstad.Robot.Direction;

/**
 * Wizard is a robot driver that uses the information
 * from the distance matrix to find the closest
 * path to the exit. It is not a search algorithm
 * in its strict sense. It can serve as a baseline
 * to see what the minimum effort for finding the
 * exit is.
 * Requires a robot with all four sensors to operate.
 * @author pk
 *
 */
public class Wizard extends WallFollower {
	Distance dist ;
	
	@Override
	public void setDistance(Distance distance) {
		dist = distance;
	}

	@Override
	/** 
	 * Wizard follows the path to the exit via a
	 * neighboring cell that is closer to the exit
	 * than the current position. 
	 * It requires that robot and distance were set before.
	 */
	public boolean drive2Exit() throws Exception {
		// check if object is configured
		if (null == robot) {
			throw new Exception("robot.drive2exit: no robot, use method setRobot() before!") ;			
		}
		if (null == dist) {
			throw new Exception("robot.drive2exit: no distances, use method setDistance() before!") ;			
		}
		if (!robotHasAllSensors()) {
			throw new Exception("robot.drive2exit: robot does not have all four direction sensors, can't work!") ;			
		}
			
		Robot.Direction d ;
		// search for the exit
		while (!robot.isAtGoal()) {
			//debug() ;
			// find promising direction
			d = findBestDirection() ;
			// move there
			switch (d) {
			case FORWARD :
				move1StepForward();
				break ;
			case LEFT :
				robot.rotate(Robot.Turn.LEFT);
				move1StepForward();
				break ;
			case RIGHT :
				robot.rotate(Robot.Turn.RIGHT);
				move1StepForward();
				break ;
			case BACKWARD :
				robot.rotate(Robot.Turn.LEFT);
				robot.rotate(Robot.Turn.LEFT);
				move1StepForward();
				break ;
			default :
				throw new Exception("Unexpected best direction: " + d) ;
			}
		}
		// at exit position: rotate towards exit and stop
		rotateRobot2FaceExit() ;
		return true ;
	}

	/**
	 * Checks if robot has all four distance sensors
	 * @return true if all fours sensors are present, false otherwise
	 */
	private boolean robotHasAllSensors() {
		return robot.hasDistanceSensor(Robot.Direction.BACKWARD) &&
				robot.hasDistanceSensor(Robot.Direction.FORWARD) &&
				robot.hasDistanceSensor(Robot.Direction.LEFT) &&
				robot.hasDistanceSensor(Robot.Direction.RIGHT);
	}

	/**
	 * Helper method for debugging
	 * @throws Exception
	 */
	private void debug() throws Exception {
		int[] loc = robot.getCurrentPosition();
		int x = loc[0] ;
		int y = loc[1] ;
		System.out.println("Current position: " + x + "," + y + " distance: " + dist.dists[x][y]) ;
	}
	/**
	 * Helper method to identify the neighbor cell
	 * one can move to and that is closer to the exit
	 * @precondition current position is not the exit position
	 * @return Direction to neighbor cell that is closer to the exit
	 */
	private Direction findBestDirection() {
		
		int[] min = {Integer.MAX_VALUE} ; // max is worst value
		Direction result = Robot.Direction.FORWARD ;
		// try all for directions, 
		// min and result store best current choice
		for (Direction dir : Direction.values()) {
			if (checkDirection(dir,min))
				result = dir ;
		}
		/*
		if (checkDirection(Robot.Direction.LEFT,min))
			result = Robot.Direction.LEFT ;
		if (checkDirection(Robot.Direction.RIGHT,min))
			result = Robot.Direction.RIGHT ;
		if (checkDirection(Robot.Direction.FORWARD,min))
			result = Robot.Direction.FORWARD ;
		if (checkDirection(Robot.Direction.BACKWARD,min))
			result = Robot.Direction.BACKWARD ;
		*/
		//System.out.println("best direction: " + result + " for dist " + min[0]) ;
		return result;
	}

	/** 
	 * Helper method to see if the neighbor in this direction
	 * is reachable (no wall) and if its distance to the exit is
	 * closer than the given minimum distance
 	 * @param direction of interest, relative to the direction of the robot
	 * @param min is input/output parameter, current closest distance
	 * @return true if minimum distance is updated, false if not
	 */
	private boolean checkDirection(Direction direction, int[] min) {
		if (0 < robot.distanceToObstacle(direction)) {
			// no wall as distance is positive
			// check distance of neighbor in this direction
			int d = getDistance(direction) ;
			if (d < min[0]) {
				min[0] = d ;
				return true ;
			}
		}
		return false ;
	}
	/**
	 * Method computes the distance to the exit from the
	 * neighboring cell for the given direction.
	 * @param direction of choice relative to the robots direction
	 * @return
	 */
	private int getDistance(Direction direction) {
		int result = Integer.MAX_VALUE ; // high value is poor choice
		
		try {
			// get the current position and direction in terms of the maze
			// absolute direction on the maze matrix
			int[] loc = robot.getCurrentPosition();
			int[] dir = robot.getCurrentDirection();
			int x = loc[0] ;
			int y = loc[1] ;
			int dx = dir[0] ;
			int dy = dir[1] ;
			
			switch (direction) {
			case LEFT :
				// if dx is 0 : looking north or south, 
				// => neighbor is on x coordinate +- 1 left or right
				// otherwise dy is 0: looking east or west
				// => neighbor is on y coordinate += 1 up or down
				if (dx == 0) 
					result = dist.dists[x - dy][y];
				else
					result = dist.dists[x][y + dx];	
				break ;
			case RIGHT :
				// symmetric to LEFT case
				if (dx == 0)
					result = dist.dists[x + dy][y];
				else
					result = dist.dists[x][y - dx];	
				break ;
			case FORWARD :
				// forward neighbor is x+dx, y+dy
				result = dist.dists[x + dx][y + dy];
				break ;
			case BACKWARD :
				// forward neighbor is x-dx, y-dy
				result = dist.dists[x - dx][y - dy];
				break ;
			default: 
				break ;
			}
		} catch (Exception e) {
			// Can happen if we ask for position outside of the maze
			e.printStackTrace();
		}
		return result;
	}
}
