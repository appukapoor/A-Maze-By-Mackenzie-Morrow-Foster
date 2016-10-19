package edu.wm.cs.cs301.MackenzieMorrowFoster.falstad;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class creates a maze and a pathway from any point in the maze to the exit by using Kruskal's algorithm.
 * Unlike the original version of Kruskal's algorithm that involved working with weighted edges
 * this extended version of MazeBuilder uses a randomized version of Kruskal's algorithm 
 * in which edges are randomly selected to be torn down.
 * @author Mackenzie Morrow Foster (mlmorrow)
 *
 */
public class MazeBuilderKruskal extends MazeBuilder implements Runnable {
	
	// create an initial arraylist that will hold of all walls that can be removed
	ArrayList<Wall> listOfWalls = new ArrayList<Wall>();
	// create an arraylist to hold all of the treesets.
	ArrayList<ArrayList<int[]>> listOfLists = new ArrayList<ArrayList<int[]>>();
	
	public MazeBuilderKruskal() {
		super();
		System.out.println("MazeBuilderKruskal uses Kruskal's algorithm to generate maze.");
	}
	public MazeBuilderKruskal(boolean det) {
		super(det);
		System.out.println("MazeBuilderKruskal uses Kruskal's algorithm to generate maze.");
	}
	
	/**
	 * This method overwrites the superclass's generatePathways method in order to create a maze using Kruskal's algorithm.
	 * All of the walls in the maze are thrown into an ArrayList.
	 * Then a wall is randomly selected from the ArrayList to be considered.
	 * If the wall connects two cells that DO NOT have the same marker, 
	 * then the wall is torn down and the two cells are given the same marker and become part of the same set.
	 * If the wall connects two cells that DO have the same marker,
	 * nothing happens and the wall gets tossed out and another wall gets randomly picked for consideration.
	 * This continues on until the ArrayList is empty,
	 * meaning there are no more walls to consider and every cell has the same marker.
	 */
	@Override // Override generatePathways method from MazeBuilder
	public void generatePathways() {
		
		// add all of the walls to the list
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (cells.canGo(x, y, 0, 1))
				{
					listOfWalls.add(new Wall(x, y, 0, 1));
				}
				if (cells.canGo(x, y, 0, -1))
				{
					listOfWalls.add(new Wall(x, y, 0, -1));
				}
				if (cells.canGo(x, y, 1, 0))
				{
					listOfWalls.add(new Wall(x, y, 1, 0));
				}
				if (cells.canGo(x, y, -1, 0))
				{
					listOfWalls.add(new Wall(x, y, -1, 0));
				}
				// iterate through every cell in the maze and assign it to an integer array.
				int[] intCell = {x,y};
				ArrayList<int[]> listOfInts = new ArrayList<int[]>();
				listOfInts.add(intCell);
				listOfLists.add(listOfInts);
			}
		}
		
		Wall randomPick;
		int x, y, dx, dy;
		// we need to consider each wall in the list only once
		// so while the list is NOT empty (if it's empty, then we're out of walls to work with)
		while (!listOfWalls.isEmpty()) {

			// randomly pick one wall and pull it out
			randomPick = extractWallFromListRandomly(listOfWalls);
			x = randomPick.x;
			y = randomPick.y;
			dx = randomPick.dx;
			dy = randomPick.dy;
			
			//create a temporary arraylist to hold the coordinates of the current cell
			int[] randomInt1 = {randomPick.x, randomPick.y};
			//find the index of the list of lists that contains randomInt1
			int index1 = findCell(randomInt1);
			
			//create another temporary arraylist to hold the coordinates of the neighboring cell
			int[] randomInt2 = {x+dx, y+dy};
			//find the index of the list of lists that contains randomInt2
			int index2 = findCell(randomInt2);
			
			
			// check and see if this wall connects the 2 different arraylists
			//if (!tempTree1.equals(tempTree2)) {
			if (index1 != index2 && index1 != -1 && index2 != -1) {
				//if so, then delete the wall
				cells.deleteWall(x,y,dx,dy);
				//and combine the cells into one group
				listOfLists.get(index1).addAll(listOfLists.get(index2));
				// remove the other arraylist
				listOfLists.remove(index2);
				
				//set the cells as visited
				cells.setCellAsVisited(x,y);
				cells.setCellAsVisited(x+dx, y+dy);	
			}
		}
	}
	
	/**
	 * This method is a specific contains function that takes in an arraylist and target
	 * and returns true or false depending on whether the target is in the arraylist or not
	 * @param mySet
	 * @param target
	 * @return boolean
	 */
	public boolean contains(ArrayList<int[]> tempList, int[] target){
		for (int i = 0; i < tempList.size(); i++){
			if (tempList != null && (Arrays.equals(tempList.get(i), target))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method attempts to find and return the index in listOfLists in which the int[] is located.
	 * Iterating through all of the elements in listOfLists, it adds those elements to a temporary arraylist
	 * Then it checks to see if any of those elements contain the target value
	 * If so, then the index of that element is returned
	 * If not, then -1 is returned to indicate that the target could not be found in the arraylist
	 * @param target
	 * @return the index or -1
	 */
	public int findCell(int[] target) {
		// Look through all of the elements in listOfLists
		for (int i = 0; i < listOfLists.size(); i++) {
			// add them to a temporary arraylist
			ArrayList<int[]> mySet = listOfLists.get(i);
			// if an element contains the target value
			if (mySet != null && contains(mySet, target)) {
				// return the index of that element
				return i;
			}
		}
		// If the index cannot be found, return -1
		return -1;
	}
	
	/**
	 * Randomly selects a wall from the list of all possible walls 
	 * @param listOfWalls
	 * @return a randomly selected wall
	 */
	private Wall extractWallFromListRandomly(ArrayList<Wall> listOfWalls) {
		// randomly picks and removes a wall from the list of walls and returns that wall
		return listOfWalls.remove(random.nextIntWithinInterval(0, listOfWalls.size()-1));
	}
}
