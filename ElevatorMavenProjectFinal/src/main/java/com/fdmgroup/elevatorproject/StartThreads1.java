package com.fdmgroup.elevatorproject;

//package com.fdm.threads;

public class StartThreads1 {

	public static void main(String[] args) {
		String title;
		/*
		 * ArrayList<Elevator> elevators=new ArrayList<Elevator>(); for(int
		 * i=0;i<Const.numOfElevators;i++) { elevators.add(new Elevator(1)); }
		 */
		ElevatorController ec = new ElevatorController();

		title = "Simulation for " + Const.numOfFloors + " Floors and " + Const.numOfElevators + " Elevators";

		try {
			ec.readConfig();
			GUI view = new GUI(ec.getElevatorList());
			Thread graphics = new Thread(view);
			graphics.start();
			// Thread.sleep(3000); // sleep to ensure all elevators start at bottom

			// ec.commandAdd("4:1:2");
			// ec.commandAdd("3:8:6");
			// ec.commandAdd("1:6:7");
			// ec.commandAdd("10:1:6");
			// System.out.println(ec.getCommandList().size());
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

	/**
	 * Print all the contents of an Events
	 * 
	 * @param output
	 *
	 *               private static void printOutput(Events output) {
	 * 
	 *               int length = output.length() + 1;
	 * 
	 *               System.out.println("\nOutput Messages\n");
	 * 
	 *               for (int i = 1; i < length; i++) { for (String message :
	 *               output.getAllEventMessages(i)) System.out.println("Tread " + i
	 *               + " " + message);
	 * 
	 *               System.out.println();
	 * 
	 *               }
	 * 
	 *               }
	 */

}
