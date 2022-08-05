package com.fdmgroup.elevatorproject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Class        StartThreads1
 * A class that contains a main method to run an elevator controller.
 *
 * @author      TeamGreat
 * @version		3/8/22 initial version
 * 
 */

public class StartThreads {

	private static final Logger LOGGER = LogManager.getLogger(StartThreads.class); 
	
	/**
	 * Method main
	 * Instantiates instances of ElevatorController class and GUI class
	 * to run the elevator controller and display graphical demonstration of elevator system
	 * 
	 * @param args arguments of the main method
	 * 
	 */
	
	public static void main(String[] args) {
		
		ElevatorController ec = new ElevatorController();

		try {
			/*
			 * The program can be configured through config.txt where lines represent:
			 * 1.Number of elevators
			 * 2.Number of floors
			 * 3.Maximum capacity (persons) for an elevator
			 * 4.Maximum limit (persons) for an individual command
			 * 5.Minimum/Lowest floor
			 * 6. onwards: commands to be executed (format - source:destination:people)
			 */
			
			ec.readConfig();
			
			String title;
			title = "Simulation for " + Const.numOfFloors + " Floors and " + Const.numOfElevators + " Elevators";
			LOGGER.info(title);
			
			GUI view = new GUI(ec.getElevatorList());
			Thread graphics = new Thread(view);
			
			graphics.start();
			
			Thread.sleep(2000); // sleep to ensure all elevators start at bottom

			/*
			 * Section to pass in commands at start of program.
			 * To start, commands can also be passed in through line 6 and onwards of config.txt.
			 * Commands can manually be passed in through typing into the running console as well.
			 */
			
			//(Un)Comment and add commands as necessary: 
			ec.commandAdd("4:1:2,3:8:6"); 
			ec.commandAdd("2:3:1");
			
			/*
			 * To terminate the program: Close GUI or terminate console.
			 * To exit and return all elevators to level 0: Enter 'exit' in console.
			 */
			
			Thread.sleep(1000);
			
			ec.run();

			ec.join();

			Thread.sleep(2000);
			
			view.close();

			System.out.println("Exit");

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
