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


	/**
	 * Method main
	 * Instantiates instances of ElevatorController class and GUI class
	 * to run the elevator controller and display graphical demonstration of elevator system
	 * 
	 * @param args arguments of the main method
	 * 
	 */
	
	public static void main(String[] args) {
		String title;

		/* TODO 
		 * ArrayList<Elevator> elevators=new ArrayList<Elevator>(); for(int
		 * i=0;i<Const.numOfElevators;i++) { elevators.add(new Elevator(1)); }
		 */
		ElevatorController ec = new ElevatorController();

		//title = "Simulation for " + Const.numOfFloors + " Floors and " + Const.numOfElevators + " Elevators";

		try {
			ec.readConfig();
			GUI view = new GUI(ec.getElevatorList());
			Thread graphics = new Thread(view);
			graphics.start();
			// Thread.sleep(3000); // sleep to ensure all elevators start at bottom

			// ec.commandAdd("4:1:2"); //TODO 
			// ec.commandAdd("3:8:6");
			// ec.commandAdd("1:6:7");
			// ec.commandAdd("10:1:6");
			// System.out.println(ec.getCommandList().size()); //4:1:2,3:8:6,1:6:7,10:1:6
			Thread.sleep(100);
			ec.run();
			// ec.commandAdd("exit");

			ec.join();

			Thread.sleep(2000);
			view.close();

			System.out.println("Exit");

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
