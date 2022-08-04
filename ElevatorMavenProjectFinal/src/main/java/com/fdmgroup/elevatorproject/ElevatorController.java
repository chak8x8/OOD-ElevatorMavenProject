package com.fdmgroup.elevatorproject;
import java.io.*;
import java.util.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ElevatorController {
	private ArrayList<ArrayList<Integer>> commandList;
	private ArrayList<Elevator> elevatorList;
	private boolean isExit;
	// private static final Logger LOGGER =
	// LogManager.getLogger(ElevatorController.class); //TODO 

	public ElevatorController() {
		commandList = new ArrayList<ArrayList<Integer>>();
		elevatorList = new ArrayList<Elevator>();
		isExit = false;
	}

	public void commandAdd(String instruction) {
		if (instruction.equals("exit")) {
			ArrayList<Integer> command = new ArrayList<Integer>();
			command.add(-1);
			command.add(-1);
			command.add(-1);
			commandList.clear();
			commandList.add(command);
			isExit = true;

		} else if (validateInput(instruction) == true) {
			String[] instructionArray = instruction.split(",");
			for (String inst : instructionArray) {
				String[] floorRequest = inst.split(":");
				int s = Integer.parseInt(floorRequest[0]);
				int d = Integer.parseInt(floorRequest[1]);
				int num = Integer.parseInt(floorRequest[2]);
				ArrayList<Integer> command = new ArrayList<Integer>();
				command.add(s);
				command.add(d);
				command.add(num);
				commandList.add(command);
			}
		}
	}

	public boolean validateInput(String instruction) {
		String[] instructionArray = instruction.split(",");

		for (String inst : instructionArray) {

			for (int i = 0; i < inst.length(); i++) {
				if (!Character.isDigit(inst.charAt(i))) {
					if (inst.charAt(i) != ':') {
						return false;
					}
				}
			}

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

	public void run() {
		// readConfig();
		isExit = false;
		Scanner sc = new Scanner(System.in);
		System.out.println("please input command:");
		// activate the elevators
		for (int i = 0; i < elevatorList.size(); i++) {
			elevatorList.get(i).start();
		}
		while (true) {

			Collections.sort(elevatorList);

			for (int i = 0; i < elevatorList.size(); i++) {
				for (int j = commandList.size() - 1; j >= 0; j--) {

					if (elevatorList.get(i).dealWithCommand(commandList.get(j)) == true) {
						commandList.remove(j);
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

	public void join() {
		for (int i = 0; i < elevatorList.size(); i++) {
			elevatorList.get(i).join();
		}
	}

	public void readConfig() {
		String filePath = "src\\main\\resources\\config.txt";
		try {
			Scanner sc = new Scanner(new File(filePath));

			if (sc.hasNextInt()) {
				Const.numOfElevators = sc.nextInt();
				if (Const.numOfElevators <= 0) {
					Const.numOfElevators = 1;
				}
			}

			if (sc.hasNextInt()) {
				Const.numOfFloors = sc.nextInt();
				if (Const.numOfFloors <= 0) {
					Const.numOfFloors = 10;
				}
			}

			if (sc.hasNextInt()) {
				Const.maxCapPerElevator = sc.nextInt();
				if (Const.maxCapPerElevator <= 0) {
					Const.maxCapPerElevator = 8;
				}
			}

			if (sc.hasNextInt()) {
				Const.maxCapPerCommand = sc.nextInt();
				if (Const.maxCapPerCommand <= 1) {
					Const.maxCapPerCommand = 1;
				}
			}

			if (sc.hasNextInt()) {
				Const.minFloor = sc.nextInt();
				if (Const.minFloor > Const.numOfFloors) {
					Const.minFloor = 0;
				}
			}

			Const.maxFloor = Const.numOfFloors + Const.minFloor - 1;

			for (int i = 0; i < Const.numOfElevators; i++) {
				elevatorList.add(new Elevator());
			}

			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				commandAdd(line);
			}

		} catch (FileNotFoundException e) {
			System.out.println("The file cannot be read.");
		}
	}

	public ArrayList<ArrayList<Integer>> getCommandList() {
		return commandList;
	}

	public ArrayList<Elevator> getElevatorList() {
		return elevatorList;
	}

}
