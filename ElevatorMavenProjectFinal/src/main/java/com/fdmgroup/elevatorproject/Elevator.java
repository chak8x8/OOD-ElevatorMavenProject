package com.fdmgroup.elevatorproject;
import java.util.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Elevator implements Runnable, Comparable<Elevator>, FrameGUI {
	
	private static int elevatorCounter = 0;
	private int elevatorID;
	private int currentFloor;
	private RunningState runningState;
	private DoorState doorState;
	private ArrayList<ArrayList<Integer>> upList;
	private ArrayList<ArrayList<Integer>> downList;
	private Thread thread;
	private int currentPersonNum;
	private boolean isExit;
	private boolean dealExit;

	public Elevator(int startFloor) {
		this();
		currentFloor = startFloor;
	}

	/**
	 * Constructor
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

		System.out.println("created Elevator :" + elevatorID);
	}

	/**
	 * A main method to deal with users' commands. Based on different elevators'
	 * state, it allows elevators if they can take the job.
	 * 
	 * @param command
	 * @return
	 */
	public boolean dealWithCommand(ArrayList<Integer> command) {
		if (checkIsExit(command)) {
			isExit = true;
			dealExit = false;
			return false;
		} else if (runningState == RunningState.idle) {
			return dealWithCommandInIdle(command);
		} else if (runningState == RunningState.movingUp) {
			return dealWithCommandInMovingUp(command);
		} else if (runningState == RunningState.movingDown) {
			return dealWithCommandInMovingDown(command);
		}
		return false;
	}

	/**
	 * To check if the command is "exit".
	 * 
	 * @param command
	 * @return
	 */
	private boolean checkIsExit(ArrayList<Integer> command) {
		return command.get(0) == -1 && command.get(1) == -1 && command.get(2) == -1;
	}

	/**
	 * The moving downward elevator would decide if it would take the command.
	 * 
	 * @param command
	 * @return
	 */
	private boolean dealWithCommandInMovingDown(ArrayList<Integer> command) {
		int s = command.get(0);// s is source
		int d = command.get(1);// d is destination
		int num = command.get(2);// num is number of people that need to take elevators

		if (d < s) {
			if (s < currentFloor) {
				// To decide how many people that can be picked and decide if any furthur
				// command can be taken.
				int rest = calRestCapDown(s, d);

				if (num > rest) {
					downList.add(valueList(s, rest, 0));
					downList.add(valueList(d, 0, rest));
					command.set(2, num - rest);
					return false;
				} else {
					downList.add(valueList(s, num, 0));
					downList.add(valueList(d, 0, num));
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * To calculate how many space left to pick people who want to go lower levels.
	 * 
	 * @param s
	 * @param d
	 * @return
	 */
	private int calRestCapDown(int s, int d) {
		int allow = 0; // The amount of empty space to pick people.
		int in = 0; // The amount of people to get into the elevator.
		int out = 0; // The amount of people to get out the elevator.

		// To sort the source from high levels to low levels.
		downList.sort(new Comparator<ArrayList<Integer>>() {
			@Override
			public int compare(ArrayList<Integer> v1, ArrayList<Integer> v2) {
				return v2.get(0) - v1.get(0);
			}
		});

		// To calculate how many will get in and get out on each level.
		for (ArrayList<Integer> v : downList) {
			if (v.get(0) >= s) {
				in += v.get(1);
				out += v.get(2);
			}
		}
		int tempS = currentPersonNum + in - out;

		// To calculate how many people in the elevator.
		int maxTemps = tempS;
		for (ArrayList<Integer> v : downList) {
			if (v.get(0) < s && v.get(0) >= d) {
				tempS += v.get(1);
				tempS -= v.get(2);
				if (maxTemps < tempS)
					maxTemps = tempS;
			}
		}
		// To get to know how much space left for more people.
		allow = Const.maxCapPerElevator - maxTemps;
		return allow;
	}

	/**
	 * The moving upward elevator would decide if it would take the command.
	 * 
	 * @param command
	 * @return
	 */
	private boolean dealWithCommandInMovingUp(ArrayList<Integer> command) {
		int s = command.get(0);
		int d = command.get(1);
		int num = command.get(2);
		if (d > s) {
			if (s > currentFloor) {
				// To decide how many people that can be picked and decide if any furthur
				// command can be taken.
				int rest = calRestCapUp(s, d);

				if (num > rest) {
					upList.add(valueList(s, rest, 0));
					upList.add(valueList(d, 0, rest));
					command.set(2, num - rest);
					return false;
				} else {
					upList.add(valueList(s, num, 0));
					upList.add(valueList(d, 0, num));
					return true;
				}
			}
		}
		return false;
	}

	private int calRestCapUp(int s, int d) {
		int allow = 0;
		int in = 0;
		int out = 0;
		// To sort the source from low levels to high levels.
		upList.sort(new Comparator<ArrayList<Integer>>() {
			@Override
			public int compare(ArrayList<Integer> v1, ArrayList<Integer> v2) {
				return v1.get(0) - v2.get(0);
			}
		});
		// To calculate how many people will get in and get out on each level.
		for (ArrayList<Integer> v : upList) {
			if (v.get(0) <= s) {
				in += v.get(1);
				out += v.get(2);
			}
		}
		int tempS = currentPersonNum + in - out;
		// To calculate how many people in the elevator.
		int maxTemps = tempS;
		for (ArrayList<Integer> v : upList) {
			if (v.get(0) > s && v.get(0) <= d) {
				tempS += v.get(1);
				tempS -= v.get(2);
				if (maxTemps < tempS)
					maxTemps = tempS;
			}
		}
		// To get to know how much space left for more people.
		allow = Const.maxCapPerElevator - maxTemps;
		return allow;
	}

	/**
	 * 
	 * @param f   is the floor
	 * @param in  is the number of people to get in
	 * @param out is the number of people to get out
	 * @return
	 */
	private ArrayList<Integer> valueList(int f, int in, int out) {
		ArrayList<Integer> value = new ArrayList<Integer>();
		value.add(f);
		if (in <= Const.maxCapPerElevator)
			value.add(in);
		else
			value.add(Const.maxCapPerElevator);
		value.add(out);
		return value;
	}

	/**
	 * To decide which direction the idle elevator will go when commands come in and
	 * decide if they would take the command.
	 * 
	 * @param command
	 * @return
	 */
	private boolean dealWithCommandInIdle(ArrayList<Integer> command) {
		boolean isFinish = true;
		ArrayList<Integer> upInOutValue = new ArrayList<Integer>();
		ArrayList<Integer> downInOutValue = new ArrayList<Integer>();
		int s = command.get(0);
		int d = command.get(1);
		int num = command.get(2);
		int actual = num > Const.maxCapPerElevator ? Const.maxCapPerElevator : num;
		// To adjust the number of people waiting, and the command is not yet completed.
		if (num > Const.maxCapPerElevator) {
			command.set(2, num - Const.maxCapPerElevator);
			isFinish = false;
		}

		if (currentFloor < s) {
			upInOutValue = valueList(s, actual, 0);
			runningState = RunningState.movingUp;
		} else if (currentFloor > s) {
			downInOutValue = valueList(s, actual, 0);
			runningState = RunningState.movingDown;
		} else {
			if (d < s) {
				runningState = RunningState.movingDown;
				downInOutValue = valueList(s, actual, 0);

			} else {
				runningState = RunningState.movingUp;
				upInOutValue = valueList(s, actual, 0);
			}
		}
		if (!upInOutValue.isEmpty())
			upList.add(upInOutValue);
		if (!downInOutValue.isEmpty())
			downList.add(downInOutValue);

		// To get to know how many people will get out at destination.
		if (d < s) {
			downList.add(valueList(d, 0, actual));

		} else {
			upList.add(valueList(d, 0, actual));
		}

		return isFinish;

	}

	private void subRun(RunningState setSate, ArrayList<ArrayList<Integer>> list1,
			ArrayList<ArrayList<Integer>> list2) {

		if (doorState == DoorState.doorClosed) {
			if (list1.isEmpty() && !list2.isEmpty()) {
				runningState = setSate;
			} else if (!list1.isEmpty()) {
				boolean isStop = false;
				for (int i = list1.size() - 1; i >= 0; i--) {
					if (currentFloor == list1.get(i).get(0)) {
						int in = list1.get(i).get(1);
						int out = list1.get(i).get(2);
						list1.remove(i);
						currentPersonNum += in;
						currentPersonNum -= out;
						doorState = DoorState.doorOpening;
						isStop = true;
					}
				}
				if (!isStop) {
					if (runningState == RunningState.movingUp)
						currentFloor++;
					else if (runningState == RunningState.movingDown)
						currentFloor--;
				}

			}

		} else if (doorState == DoorState.doorOpening) {
			doorState = DoorState.doorClosing;
		} else if (doorState == DoorState.doorClosing) {
			doorState = DoorState.doorClosed;
			if (list1.isEmpty() && list2.isEmpty()) {
				runningState = RunningState.idle;
			}
		}
	}

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
						dealExit = true;
					} else if (isExit && dealExit && currentFloor == Const.minFloor) {
						break;
					}
				}
				thread.sleep(1000);
				System.out.println("help : " + currentInfo());
			}

		} catch (InterruptedException e) {

		}

	}

	public void start() {
		if (thread == null) {
			isExit = false;
			dealExit = false;
			thread = new Thread(this, "Thread - " + elevatorID);
			thread.start();
		}
	}

	public void join() {
		if (thread != null) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public String currentInfo() {
		String info = elevatorID + " :  currentFloor = " + currentFloor;
		info += " state : " + runningState.toString();
		if (runningState != RunningState.idle) {
			if (doorState != DoorState.doorClosed)
				info += " doorState : " + doorState.toString();
		}
		info += " person : " + currentPersonNum;
		return info;
	}

	public int getElevatorID() {
		return elevatorID;
	}

	// To pioritize the command to those moving elevators.
	@Override
	public int compareTo(Elevator other) {
		if (this.runningState != RunningState.idle)
			return -1;
		else if (this.runningState == RunningState.idle && other.runningState != RunningState.idle)
			return 1;
		return 0;
	}

	//getters and setters: 
	@Override
	public int getCurrentFloor() {
		return currentFloor;
	}
	
	@Override
	public int getPeople() {
		return currentPersonNum;
	}
	
	public RunningState getRunningState() {
		return runningState;
	}
	
	@Override
	public DoorState getDoorState() {
		return doorState;
	}

	public static int getElevatorCounter() {
		return elevatorCounter;
	}

	public static void setElevatorCounter(int elevatorCounter) {
		Elevator.elevatorCounter = elevatorCounter;
	}

	public void setRunningState(RunningState runningState) {
		this.runningState = runningState;
	}

	public void setDoorState(DoorState doorState) {
		this.doorState = doorState;
	}

	public ArrayList<ArrayList<Integer>> getUpList() {
		return upList;
	}

	public void setUpList(ArrayList<ArrayList<Integer>> upList) {
		this.upList = upList;
	}

	public ArrayList<ArrayList<Integer>> getDownList() {
		return downList;
	}

	public void setDownList(ArrayList<ArrayList<Integer>> downList) {
		this.downList = downList;
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public void setCurrentPersonNum(int currentPersonNum) {
		this.currentPersonNum = currentPersonNum;
	}

	public boolean isExit() {
		return isExit;
	}

	public void setExit(boolean isExit) {
		this.isExit = isExit;
	}

	public boolean isDealExit() {
		return dealExit;
	}

	public void setDealExit(boolean dealExit) {
		this.dealExit = dealExit;
	}

	public void setElevatorID(int elevatorID) {
		this.elevatorID = elevatorID;
	}

	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}
}
