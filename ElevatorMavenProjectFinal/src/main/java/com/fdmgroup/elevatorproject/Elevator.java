package com.fdmgroup.elevatorproject;
import java.util.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Class Elevator 
 * A class to represent basic functions of an elevator.
 *
 * @author TeamGreat
 * @version 3/8/22 initial version
 * 
 */
public class Elevator implements Runnable, Comparable<Elevator>, FrameGUI {
	
	private static int elevatorCounter = 0; 	// Constant value that is incremented every time the constructor of the class is called
	private int elevatorID; 					// Unieque ID of each elevator
	private int currentFloor; 					// Elevator's current floor
	private RunningState runningState; 			// Elevator's state
	private DoorState doorState; 				// Elevator's door state
	private ArrayList<ArrayList<Integer>> upList; 			// List of commands to tell an elevator to move upwards
	private ArrayList<ArrayList<Integer>> downList; 		// List of commands to tell an elevator to move downwards
	private Thread thread; 						// Elevator thread
	private int currentPersonNum;				// Current number of people in an elevator
	private boolean isExit; 					// For checking if an input command is "exit"
	private boolean dealExit; 					// Input command is "exit"
	private static final Logger LOGGER = LogManager.getLogger(Elevator.class); 

	/**
	 * Constructor Elevator 
	 * Required constructor has 1 parameter which defines the lowest floor that has elevator(s) for the class
	 *
	 * @param startFloor - the lowest floor of a building containing elevator(s)
	 *
	 */
	public Elevator(int startFloor) {
		
		this();
		currentFloor = startFloor;
	}

	/**
	 * Constructor Elevator 
	 * Required constructor has no parameters
	 *
	 */
	public Elevator() {
		
		elevatorCounter++;
		elevatorID = elevatorCounter;
		currentFloor = Const.minFloor;
		runningState = RunningState.idle;
		doorState = DoorState.doorClosed;
		upList = new ArrayList<ArrayList<Integer>>();
		downList = new ArrayList<ArrayList<Integer>>();
		currentPersonNum = 0;
		isExit = false;
		dealExit = false;

		LOGGER.info("Elevator created: " + elevatorID); 

	}

	/**
	 * Method dealWithCommand 
	 * Calls a method that deals with a specific command depending on an elevator's state, returning false otherwise
	 * 
	 * @param command for each elevator from elevator controller
	 * @return boolean - returning false if an input command is "exit" or has not been allocated to an elevator
	 * 
	 */
	public boolean dealWithCommand(ArrayList<Integer> command) {
		
		if (checkIsExit(command)) {
			isExit = true;
			dealExit = false;
			return false;
			
			// Elevator in IDLE state
		} else if (runningState == RunningState.idle) {
			return dealWithCommandInIdle(command);
			
			// Elevator is moving up
		} else if (runningState == RunningState.movingUp) {
			return dealWithCommandInMovingUp(command);
			
			// Elevator is moving down
		} else if (runningState == RunningState.movingDown) {
			return dealWithCommandInMovingDown(command);
		}
		return false; // Elevator has been assigned with no command
	}

	/**
	 * Method checkIsExit
	 * Checks if a command is "exit"
	 * 
	 * @param command from an elevator controller
	 * @return boolean - returning true if a command is {-1,-1,-1}, false otherwise
	 * 
	 */
	private boolean checkIsExit(ArrayList<Integer> command) {
		
		return command.get(0) == -1 && command.get(1) == -1 && command.get(2) == -1;
	}

	/**
	 * Method dealWithCommandInMovingDown
	 * Tells an elevator moving downwards whether it should pick up passenger(s) along the way
	 * 
	 * @param command from an elevator controller
	 * @return boolean - returning true if a moving-down command is successful, false otherwise
	 */
	private boolean dealWithCommandInMovingDown(ArrayList<Integer> command) {
		
		int s = command.get(0);		// Source floor
		int d = command.get(1);		// Destination floor
		int num = command.get(2);	// Number of people to enter an elevator

		if (d < s) {
			
			if (s < currentFloor) {
				int rest = calRestCapDown(s, d);	// Number of people allowed to enter an elevator
				
				// Not enough space in an elevator for every passenger waiting to enter an elevator
				if (num > rest) {
					downList.add(valueList(s, rest, 0)); 	// Moving-down command for an elevator taking people who are allowed to enter an elevator
					downList.add(valueList(d, 0, rest)); 	// Moving-down command for an elevator from a destination floor with no passenger getting inside
					command.set(2, num - rest);				// Setting a number of people could not enter an elevator to be a number of people who are waiting to use an elevator
					return false;
					
				// Enough space in an elevator for all passenger(s) waiting to enter
				} else {
					downList.add(valueList(s, num, 0));		// Moving-down command for an elevator taking all passengers with it
					downList.add(valueList(d, 0, num));		// Moving-down command for an elevator from a destination floor with no passenger getting inside
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Method calRestCapDown
	 * Calculates how many passengers a moving-downward elevator can pick up
	 * 
	 * @param s source floor
	 * @param d destination floor
	 * @return int - number of people an elevator can pick up
	 * 
	 */
	private int calRestCapDown(int s, int d) {
		
		int allow = 0; 	// Amount of people allowed to enter an elevator
		int in = 0; 	// Amount of people getting into an elevator
		int out = 0; 	// Amount of people getting out of an elevator

		// Sorting the source floor from high levels to low levels
		downList.sort(new Comparator<ArrayList<Integer>>() {
			
			@Override
			public int compare(ArrayList<Integer> v1, ArrayList<Integer> v2) {
				return v2.get(0) - v1.get(0); // Descending order
			}
		});

		for (ArrayList<Integer> v : downList) {
			
			// Calculating how many passengers a moving-downward elevator can pick up
			if (v.get(0) >= s) {
				in += v.get(1);			// Adding a number of people who enter an elevator
				out += v.get(2);		// Subtracting a number of people who exit an elevator
			}
		}
		int tempS = currentPersonNum + in - out;

		int maxTemps = tempS; // Number of passengers currently in an elevator
		
		for (ArrayList<Integer> v : downList) {
			if (v.get(0) < s && v.get(0) >= d) {
				tempS += v.get(1);
				tempS -= v.get(2);
				if (maxTemps < tempS)
					maxTemps = tempS;
			}
		}
		allow = Const.maxCapPerElevator - maxTemps; // Amount of people allowed to enter an elevator
		return allow;
	}

	/**
	 * Method dealWithCommandInMovingUp
	 * Tells an elevator moving up whether it should pick up passenger(s) along the way
	 * 
	 * @param command from an elevator controller
	 * @return boolean - returning true if a moving-up command is successful, false otherwise
	 */
	private boolean dealWithCommandInMovingUp(ArrayList<Integer> command) {

		int s = command.get(0);		// Source floor
		int d = command.get(1);		// Destination floor
		int num = command.get(2);	// Number of passengers to enter an elevator
		
		if (d > s) {
			if (s > currentFloor) {

				
				// Deciding on how many people that can be picked and if a further command can be taken
				int rest = calRestCapUp(s, d);	// Amount of people allowed to enter an elevator

				// Not enough space in an elevator for every passenger waiting to enter an elevator
				if (num > rest) {
					upList.add(valueList(s, rest, 0));	// Moving-up command for an elevator taking people who are allowed to enter an elevator
					upList.add(valueList(d, 0, rest));	// Moving-up command for an elevator from a destination floor with no passenger getting inside
					command.set(2, num - rest);			// Setting a number of people could not enter an elevator to be a number of people who are waiting to use an elevator
					return false;
					
				// Enough space in an elevator for all passenger(s) waiting to enter
				} else {
					upList.add(valueList(s, num, 0));	// Moving-up command for an elevator taking all passengers with it
					upList.add(valueList(d, 0, num));	// Moving-up command for an elevator from a destination floor with no passenger getting inside
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Method calRestCapUp
	 * Calculates how many passengers a moving-up elevator can pick up
	 * 
	 * @param s source floor
	 * @param d destination floor
	 * @return int - number of people an elevator can pick up
	 * 
	 */
	private int calRestCapUp(int s, int d) {

		int allow = 0; 	// Amount of people allowed to enter an elevator
		int in = 0; 	// Amount of people getting into an elevator
		int out = 0; 	// Amount of people getting out of an elevator
		
		// Sorting source floor from low levels to high levels
		upList.sort(new Comparator<ArrayList<Integer>>() {
			@Override
			public int compare(ArrayList<Integer> v1, ArrayList<Integer> v2) {
				return v1.get(0) - v2.get(0);	//Ascending order
			}
		});
		
		// Calculating how many people will get in and out on each level
		for (ArrayList<Integer> v : upList) {
			if (v.get(0) <= s) {
				in += v.get(1);		// Adding a number of people getting into an elevator
				out += v.get(2);	// Adding a number of people getting out of an elevator
			}
		}
		
		int tempS = currentPersonNum + in - out;
		int maxTemps = tempS;	// Current number of people in an elevator
		
		for (ArrayList<Integer> v : upList) {
			if (v.get(0) > s && v.get(0) <= d) {
				tempS += v.get(1);		// Adding a number of people getting into an elevator
				tempS -= v.get(2);		// Subtracting a number of people getting out of an elevator
				if (maxTemps < tempS)
					maxTemps = tempS;
			}
		}
	
		allow = Const.maxCapPerElevator - maxTemps; // Amount of people allowed to enter an elevator
		return allow;
	}

	/**
	 * Method valueList
	 * Compares a number of people who can enter an elevator with a capacity of an elevator
	 * 
	 * @param f - source floor
	 * @param in - number of people getting into an elevator
	 * @param out - number of people getting out of an elevator
	 * @return ArrayList<Integer> a list represented by {source floor, number of people in an elevator, number of people exiting an elevator}
	 * 
	 */
	private ArrayList<Integer> valueList(int f, int in, int out) {
		
		ArrayList<Integer> value = new ArrayList<Integer>();
		value.add(f);
		
		// An elevator has enough space for all waiting passenger(s)
		if (in <= Const.maxCapPerElevator)
			value.add(in);
		
		else
			value.add(Const.maxCapPerElevator);	// An elevator is full
		value.add(out);
		
		return value;
	}

	/**
	 * Method dealWithCommandInIdle
	 * Decides which direction whether idle elevator should take a command
	 * and whether it should go upwards or downwards
	 * 
	 * @param command from elevator controller
	 * @return boolean - returning false if not all passengers can enter an elevator, false otherwise
	 * 
	 */
	private boolean dealWithCommandInIdle(ArrayList<Integer> command) {
		boolean isFinish = true;
		ArrayList<Integer> upInOutValue = new ArrayList<Integer>();
		ArrayList<Integer> downInOutValue = new ArrayList<Integer>();
		int s = command.get(0);		// Source floor
		int d = command.get(1);		// Destination floor
		int num = command.get(2);	// Number of people to enter an elevator
		int actual = num > Const.maxCapPerElevator ? Const.maxCapPerElevator : num;	// Actual number of people who can enter an elevator

		// Adjusting the number of people waiting, and the command is not yet completed
		
		// Not all passengers can enter an elevator
		if (num > Const.maxCapPerElevator) {
			command.set(2, num - Const.maxCapPerElevator);
			isFinish = false;
		}

		if (currentFloor < s) {
			// Moving upwards to pick up passenger(s)
			upInOutValue = valueList(s, actual, 0);
			runningState = RunningState.movingUp;
		} else if (currentFloor > s) {
			// Moving down to pick up passenger(s)
			downInOutValue = valueList(s, actual, 0);
			runningState = RunningState.movingDown;
		} else {
			if (d < s) {
				// Taking passenger(s) downwards
				runningState = RunningState.movingDown;
				downInOutValue = valueList(s, actual, 0);

			} else {
				// Taking passenger(s) upwards
				runningState = RunningState.movingUp;
				upInOutValue = valueList(s, actual, 0);
			}
		}
		if (!upInOutValue.isEmpty())
			upList.add(upInOutValue);
		if (!downInOutValue.isEmpty())
			downList.add(downInOutValue);

		// Finding how many people will get out at destination floor
		if (d < s) {
			//For moving-down elevator
			downList.add(valueList(d, 0, actual));

		} else {
			//For moving-up elevator
			upList.add(valueList(d, 0, actual));
		}

		return isFinish;
	}

	/**
	 * Method subRun
	 * Decides a state of an elevator's door and whether an idle elevator should take a command
	 * and whether it should go upwards or downwards
	 * 
	 * @param setState - running state of elevator
	 * @param list1 - a list of command lists
	 * @param list2 - a list of command lists
	 * 
	 */
	public void subRun(RunningState setSate, ArrayList<ArrayList<Integer>> list1,
			ArrayList<ArrayList<Integer>> list2) {

		// If door closed
		if (doorState == DoorState.doorClosed) {
			
			if (list1.isEmpty() && !list2.isEmpty()) {
				//An elevator either go up or down
				runningState = setSate;
				
			} else if (!list1.isEmpty()) {
				boolean isStop = false;
				
				// An elevator keeps moving either up or down
				for (int i = list1.size() - 1; i >= 0; i--) {
					
					// An elevator has reached the source floor to pick up passenger(s)
					if (currentFloor == list1.get(i).get(0)) {
						int in = list1.get(i).get(1);
						int out = list1.get(i).get(2);
						list1.remove(i);				// Removing a command
						currentPersonNum += in;		// Adding a number of passengers entering an elevator
						currentPersonNum -= out;		// Subtracting a number of passengers entering an elevator
						doorState = DoorState.doorOpening;
						isStop = true;
					}
				}
				
				if (!isStop) {
					// Updating current floor
					if (runningState == RunningState.movingUp)
						currentFloor++;
					else if (runningState == RunningState.movingDown)
						currentFloor--;
				}

			}

			// Closing elevator's door
		} else if (doorState == DoorState.doorOpening) {
			doorState = DoorState.doorClosing;
			
			// Closed door
		} else if (doorState == DoorState.doorClosing) {
			doorState = DoorState.doorClosed;
			
			// No command
			if (list1.isEmpty() && list2.isEmpty()) {
				runningState = RunningState.idle;
			}
		}
	}

	/**
	 * Method run
	 * Decides whether an elevator should go upwards or downwards
	 * (This decision is based first-come-first-serve algorithm but we also consider going up and down
	 * to pick up passengers that are not far from the elevator)
	 * 
	 */
	public void run() {
		
		try {
			while (true) {
				if (runningState == RunningState.movingUp) {
					subRun(RunningState.movingDown, upList, downList);
					
				} else if (runningState == RunningState.movingDown) {
					subRun(RunningState.movingUp, downList, upList);
					
				} else if (runningState == RunningState.idle) {
					
					if (isExit && !dealExit) {
						dealWithCommand(valueList(currentFloor, Const.minFloor, 0));
						dealExit = true;	// Command is "exit"
						
						// Input command is "exit" and is "exit" and current floor and the lowest floor with elevator(s) are the same
					} else if (isExit && dealExit && currentFloor == Const.minFloor) {
						break;
					}
				}
				thread.sleep(1000);
				System.out.println("elevator : " + currentInfo());
			}

		} catch (InterruptedException e) {

		}

	}

	/**
	 * Method start
	 * Starts an elevator thread and initiates "isExit" and "dealExit" boolean variables
	 * 
	 */
	public void start() {
		if (thread == null) {
			isExit = false;
			dealExit = false;
			thread = new Thread(this, "Thread - " + elevatorID);
			thread.start();
			LOGGER.info("Created " + thread.getName()); 
		}
	}

	/**
	 * Method joint
	 * Joins an elevator thread so no threads are executed at the same time
	 * 
	 */
	public void join() {
		if (thread != null) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method currentInfo
	 * Displays elevator ID, current floor, an elevator's state, its door's state and a number of passengers to enter the elevator
	 * 
	 * @return String current information about an elevator
	 * 
	 */
	public String currentInfo() {
		String info = elevatorID + " :  currentFloor = " + currentFloor;
		info += " state : " + runningState.toString();
		if (runningState != RunningState.idle) {
			if (doorState != DoorState.doorClosed)
				info += " doorState : " + doorState.toString();
		}
		info += ", person(s) : " + currentPersonNum;
		return info;
	}

	/**
	 * Method getElevatorID
	 * Returns a unique ID of an elevator
	 * 
	 * @return int elevator ID
	 * 
	 */
	public int getElevatorID() {
		return elevatorID;
	}

	/**
	 * Method compareTo
	 * Pioritises a command to those moving elevators
	 * 
	 * @param other - comparing to the other elevator's state
	 * @return int - elevator ID
	 * 
	 */
	@Override
	public int compareTo(Elevator other) {
		if (this.runningState != RunningState.idle)
			return -1;
		else if (this.runningState == RunningState.idle && other.runningState != RunningState.idle)
			return 1;
		return 0;
	}

	//getters and setters: 
	/**
	 * Method  getCurrentFloor
	 * Returns a current floor an elevator is on
	 * 
	 * @return int - current floor
	 * 
	 */
	@Override
	public int getCurrentFloor() {
		return currentFloor;
	}

	/**
	 * Method  getPeople
	 * 
	 * @return int - current number of people in an elevator
	 * 
	 */
	@Override
	public int getPeople() {
		return currentPersonNum;
	}
	
	/**
	 * Method  getRunningState
	 * 
	 * @return RunningState - elevator's state
	 * 
	 */
	public RunningState getRunningState() {
		return runningState;
	}

	/**
	 * Method  getDoorState
	 * 
	 * @return DoorState - door's state
	 * 
	 */
	@Override
	public DoorState getDoorState() {
		return doorState;
	}

	/**
	 * Method  getElevatorCounter
	 * 
	 * @return int - constant value that is incremented every time the constructor of the class is called
	 * 
	 */
	public static int getElevatorCounter() {
		return elevatorCounter;
	}

	/**
	 * Method setElevatorCounter
	 * 
	 * @param elevatorCounter (constant value that is incremented every time the constructor of the class is called)
	 * 
	 */
	public static void setElevatorCounter(int elevatorCounter) {
		Elevator.elevatorCounter = elevatorCounter;
	}

	/**
	 * Method setRunningState
	 * 
	 * @param runningState - elevator's state
	 * 
	 */
	public void setRunningState(RunningState runningState) {
		this.runningState = runningState;
	}

	/**
	 * Method setDoorState
	 * 
	 * @param doorState - elevator door's state
	 * 
	 */
	public void setDoorState(DoorState doorState) {
		this.doorState = doorState;
	}

	/**
	 * Method getUpList
	 * 
	 * @return ArrayList<ArrayList<Integer>> -  list of command lists to tell an elevator to move upwards
	 * 
	 */
	public ArrayList<ArrayList<Integer>> getUpList() {
		return upList;
	}
	
	/**
	 * Method setUpList
	 * 
	 * @param upList - list of command lists to tell an elevator to move upwards
	 * 
	 */
	public void setUpList(ArrayList<ArrayList<Integer>> upList) {
		this.upList = upList;
	}

	/**
	 * Method getDownList
	 * 
	 * @return ArrayList<ArrayList<Integer>> -  list of command lists to tell an elevator to move downwards
	 * 
	 */
	public ArrayList<ArrayList<Integer>> getDownList() {
		return downList;
	}

	/**
	 * Method setDownList
	 * 
	 * @param downList -  list of command lists to tell an elevator to move downwards
	 * 
	 */
	public void setDownList(ArrayList<ArrayList<Integer>> downList) {
		this.downList = downList;
	}

	/**
	 * Method getThread
	 * 
	 * @return Thread - elevator thread
	 * 
	 */
	public Thread getThread() {
		return thread;
	}

	/**
	 * Method setThread
	 * 
	 * @param thread of each elevator
	 * 
	 */
	public void setThread(Thread thread) {
		this.thread = thread;
	}

	/**
	 * Method setCurrentPersonNum
	 * 
	 * @param currentPersonNum - current number of people in an elevator
	 * 
	 */
	public void setCurrentPersonNum(int currentPersonNum) {
		this.currentPersonNum = currentPersonNum;
	}

	/**
	 * Method isExit
	 * 
	 * @return boolean - return true if an input command is "exit", false otherwise
	 * 
	 */
	public boolean isExit() {
		return isExit;
	}

	/**
	 * Method setExit
	 * Stops elevator system from running
	 * 
	 * @param isExit - set true if an input command is "exit", false otherwise
	 * 
	 */
	public void setExit(boolean isExit) {
		this.isExit = isExit;
	}

	/**
	 * Method isDealExit
	 * 
	 * @return boolean - for dealing with an input command being "exit"
	 * 
	 */
	public boolean isDealExit() {
		return dealExit;
	}

	/**
	 * Method dealExit
	 * 
	 * @param dealExit - an input command is "exit"
	 * 
	 */
	public void setDealExit(boolean dealExit) {
		this.dealExit = dealExit;
	}

	/**
	 * Method setElevatorID
	 * 
	 * @param elevatorID - unique elevator ID
	 * 
	 */
	public void setElevatorID(int elevatorID) {
		this.elevatorID = elevatorID;
	}

	/**
	 * Method setCurrentFloor
	 * 
	 * @param currentFloor - current floor of an elevator
	 * 
	 */
	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}
}
