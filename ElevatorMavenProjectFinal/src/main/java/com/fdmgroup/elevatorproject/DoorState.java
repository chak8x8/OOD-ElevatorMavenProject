package com.fdmgroup.elevatorproject;

/**
 * States of an elevator's door
 * 
 * @author TeamGreat
 * @version 3/8/22 initial Version
 * 
 */

public enum DoorState {
	
	/**
	 * An elevator's door is opening
	 */
	doorOpening, 
	
	/**
	 * An elevator's door is closing
	 */
	doorClosing, 
	
	/**
	 * An elevator's door is closed
	 */
	doorClosed;

}