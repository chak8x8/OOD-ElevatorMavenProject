package com.fdmgroup.elevatorproject;

public interface FrameGUI{

	public int getCurrentFloor();		// Get the current floor number

//	public String getCommand(); 		// Get the current command being used
//	
	public int getPeople(); 			// Get the number of people in the elevator
//	
//	public RunningState getRunningState(); 			// Get the current elevator state for colour change
//	
	public DoorState getDoorState(); 
}