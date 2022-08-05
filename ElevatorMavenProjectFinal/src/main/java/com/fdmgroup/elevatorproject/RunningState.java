package com.fdmgroup.elevatorproject;

/**
 * States of an elevator
 * 
 * @author TeamGreat
 * @version 3/8/22 initial Version
 * 
 */

public enum RunningState {
	
	/**
	 * An elevator is moving up
	 */
	movingUp, 
	
	/**
	 * An elevator is moving down
	 */
	movingDown, 
	
	/**
	 * An elevator is idle
	 */
	idle;
}