package com.fdmgroup.elevatorproject;
import java.io.*;
import java.util.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Class        ElevatorController
 * A class that verifies command(s) from passenger(s) and assign a valid command to each elevator.
 *
 * @author      TeamGreat
 * @version		3/8/22 initial version
 * 
 */

public class ElevatorController {
	
	private ArrayList<ArrayList<Integer>> commandList;
	private ArrayList<Elevator> elevatorList;
	private boolean isExit;
	private int joinCount = 0;
	// private static final Logger LOGGER =
	// LogManager.getLogger(ElevatorController.class); //TODO 

	/**
	 * Constructor ElevatorController 
	 * Required constructor has no parameters
	 *
	 */
	
	public ElevatorController() {
		
		commandList = new ArrayList<ArrayList<Integer>>();	// List containing source and destination floor numbers
		elevatorList = new ArrayList<Elevator>();			// List contaning existing elevators in a building
		isExit = false;										// Input command is "exit" - elevator system stops
	}

	/**
	 * Method commandAdd
	 * Adds instruction for elevators to the command list initialised in the constructor
	 * 
	 * @param instruction - a list of commands for elevator(s)
	 * 
	 */
	
	public void commandAdd(String instruction) {
		
		// If a command is "exit"
		if (instruction.equals("exit")) {
			ArrayList<Integer> command = new ArrayList<Integer>();
			// Assigning a command to be {-1,-1,-1}, meaning a command is "exit"
			command.add(-1);
			command.add(-1);
			command.add(-1);
			commandList.clear();
			commandList.add(command);
			isExit = true;

			// Checking if instruction is valid using the check method
		} else if (validateInput(instruction) == true) {
			String[] instructionArray = instruction.split(",");			// Splitting commands
			for (String inst : instructionArray) {
				String[] floorRequest = inst.split(":");				// Splitting source floor number and destination floor number
				int s = Integer.parseInt(floorRequest[0]); 				// Source floor number
				int d = Integer.parseInt(floorRequest[1]); 				// Destination floor number
				int num = Integer.parseInt(floorRequest[2]);			// Number of people to enter an elevator
				ArrayList<Integer> command = new ArrayList<Integer>();	// List of commands
				command.add(s);
				command.add(d);
				command.add(num);
				commandList.add(command);
			}
		}
	}

	/**
	 * Method validateInput
	 * Checks if an instruction (a list of commands) for elevator(s) is valid
	 * 
	 * @param instruction - a list of commands for elevator(s)
	 * @return boolean - true if an instruction if valid, false otherwise
	 */
	
	public boolean validateInput(String instruction) {
		instruction = instruction.replaceAll("\\s+","");
		String[] instructionArray = instruction.split(",");
		
		for (String inst : instructionArray) {

			for (int i = 0; i < inst.length(); i++) {
				if (!Character.isDigit(inst.charAt(i))) {
					if (inst.charAt(i) != ':') {
						// Returning false as a character is not a digit or a colon
						return false;
					}
				}
			}

			// Checking if floors exist in a building
			String[] floorRequest = inst.split(":");

//			if(invalidCommand(floorRequest)) {
//				//LOGGER.error("Invalid floor command");
//				return false;
//			}
//
//	public boolean invalidCommand(String[] floorRequest) {
//		if (floorRequest.length != 3) {
//			// LOGGER.error("More than 3 floor in the command (!=3)");
//			return true;
//
//		} else if (Integer.parseInt(floorRequest[0]) < Const.minFloor
//				|| Integer.parseInt(floorRequest[0]) > Const.maxFloor) {
//			// LOGGER.error("Source Floor outOfBound (floor[0])");
//			return true;
//
//		} else if (Integer.parseInt(floorRequest[1]) < Const.minFloor
//				|| Integer.parseInt(floorRequest[1]) > Const.maxFloor) {
//			// LOGGER.error("Destination Floor outOfBound (floor[1])");
//			return true;
//
//		} else if (Integer.parseInt(floorRequest[0]) == Integer.parseInt(floorRequest[1])) {
//			// LOGGER.error("Same Floor Movement (floor[0] = floor[1])");
//			return true;
//
//		} else if (Integer.parseInt(floorRequest[2]) <= 0
//				|| Integer.parseInt(floorRequest[2]) > Const.maxCapPerCommand) {
//			// LOGGER.info("No people or the number of people is more than the elevator's
//			// maxmium capacity");
//			return true;
//		}
//
//		// LOGGER.info("The floor command is valid");
//		return false;
//
//	}

			if (floorRequest.length != 3) {
				return false;
			}

			if (Integer.parseInt(floorRequest[0]) < Const.minFloor
					|| Integer.parseInt(floorRequest[0]) > Const.maxFloor) {
				return false;
			}

			if (Integer.parseInt(floorRequest[1]) < Const.minFloor
					|| Integer.parseInt(floorRequest[1]) > Const.maxFloor) {
				return false;
			}

			if (Integer.parseInt(floorRequest[0]) == Integer.parseInt(floorRequest[1])) {
				return false;
			}

			if (Integer.parseInt(floorRequest[2]) <= 0 || Integer.parseInt(floorRequest[2]) > Const.maxCapPerCommand) {
				return false;
			}

		}

		return true;
	}

	/**
	 * Method run
	 * Reads a configuration file, activates elevator(s), assigns a command to an elevator and calls ElevatorView class to display a state of an elevator
	 *
	 */
	
	public void run() {
		
		// readConfig();
		isExit = false;
		Scanner sc = new Scanner(System.in);
		System.out.println("please input command:");
		
		// Activating elevator(s)
		for (int i = 0; i < elevatorList.size(); i++) {
			elevatorList.get(i).start();
		}
		while (true) {

			// Assigning commands to elevator(s)
			Collections.sort(elevatorList);

			for (int i = 0; i < elevatorList.size(); i++) {
				for (int j = commandList.size() - 1; j >= 0; j--) {

					if (elevatorList.get(i).dealWithCommand(commandList.get(j)) == true) {
						commandList.remove(j);	 // Removing a command that has been assigned
					}
				}
			}
			
			if (isExit)
				break;
			
			if (sc.hasNextLine()) {
				String line = sc.nextLine();
				
				if (line.equals("stop")) {
					break;
				}
				commandAdd(line);
			}
		}
	}

	/**
	 * Method join
	 * Joins elevator thread
	 * 
	 */
	
	public void join() {
		
		for (int i = 0; i < elevatorList.size(); i++) {
			elevatorList.get(i).join();
			joinCount++;
		}
	}
	
	/**
	 * Method readConfig
	 * Reads a configuration file to obtain information about a building of elevator(s) and the elevator(s) and commands for elevator(s)
	 * 
	 */
	
	public void readConfig() {
		
		String filePath = "src\\main\\resources\\config.txt";
		try {
			Scanner sc = new Scanner(new File(filePath));

			if (sc.hasNextInt()) {
				// The file contains a number of elevator(s)
				Const.numOfElevators = sc.nextInt();
				if (Const.numOfElevators <= 0) {
					Const.numOfElevators = 1;	// Default number of elevator if input is invalid
				}
			}

			if (sc.hasNextInt()) {
				// The file contains a number of floors in the building
				Const.numOfFloors = sc.nextInt();
				if (Const.numOfFloors <= 0) {
					Const.numOfFloors = 10;	// Default number of floors if input is invalid
				}
			}

			if (sc.hasNextInt()) {
				// The file contains a capacity of each elevator
				Const.maxCapPerElevator = sc.nextInt();
				if (Const.maxCapPerElevator <= 0) {
					Const.maxCapPerElevator = 8;	// Default capacity of each elevator if input is invalid
				}
			}

			if (sc.hasNextInt()) {
				// The file contains a maximum capacity for each command 
				Const.maxCapPerCommand = sc.nextInt();
				if (Const.maxCapPerCommand <= 1) {
					Const.maxCapPerCommand = 1;	// Default maximum capacity for each command  if input is invalid
				}
			}

			if (sc.hasNextInt()) {
				// The file contains the lowest floor with elevator(s) (source)
				Const.minFloor = sc.nextInt();
				if (Const.minFloor > Const.numOfFloors) {
					Const.minFloor = 0; // Default ground floor if input is invalid
				}
			}

			Const.maxFloor = Const.numOfFloors + Const.minFloor - 1;

			for (int i = 0; i < Const.numOfElevators; i++) {
				// Adding elevator(s) to a list
				elevatorList.add(new Elevator());
			}

			while (sc.hasNextLine()) {
				// Obtaining all commands of elevator(s) line by line
				String line = sc.nextLine();
				commandAdd(line);
			}

		} catch (FileNotFoundException e) {
			System.out.println("The file cannot be read.");
		}
	}

	/**
	 * Method getCommandList
	 * 
	 * @return ArrayList a list of lists of elevator commands
	 * 
	 */
	
	public ArrayList<ArrayList<Integer>> getCommandList() {
		
		return commandList;
	}

	/**
	 * Method getElevatorList
	 * 
	 * @return ArrayList a list of elevator objects
	 * 
	 */
	
	public ArrayList<Elevator> getElevatorList() {
		
		return elevatorList;
	}

	/**
	 * Method getIsExit
	 * 
	 * @return boolean
	 * 
	 */
	
	public boolean getIsExit() {
		return isExit;
	}

	/**
	 * Method getJoinCount
	 * 
	 * @return int
	 * 
	 */
	
	public int getJoinCount() {
		return this.joinCount;
	}

	/**
	 * Method setIsExitTrue
	 * 
	 */
	
	public void setIsExitTrue() {
		this.isExit = true;
	}

}
