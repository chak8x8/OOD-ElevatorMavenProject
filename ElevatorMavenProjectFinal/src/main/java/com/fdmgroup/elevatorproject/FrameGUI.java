package com.fdmgroup.elevatorproject;

/**
 * Interface	FrameGUI
 * Used for displaying a graphical demonstration of elevator system
 * 
 * @author TeamGreat
 * @version 3/8/22 initial Version
 * 
 */

public interface FrameGUI{

	/**
	 * Method	getCurrentFloor
	 * Gets current floor of an elevator
	 * 
	 * @return int - current floor of elevator
	 * 
	 */
	
	public int getCurrentFloor();

//	public String getCommand(); 		// Get the current command being used

	/**
	 * Method	getPeople
	 * Gets the number of people in the elevator
	 * 
	 * @return int - number of people in the elevator
	 * 
	 */
	
	public int getPeople(); 

//	public RunningState getRunningState(); 			// Get the current elevator state for colour change

	/**
	 * Method	getDoorState
	 * Gets a state of an elevator door
	 * 
	 * @return DoorState - elevator door's state
	 * 
	 */
	public DoorState getDoorState(); 
}