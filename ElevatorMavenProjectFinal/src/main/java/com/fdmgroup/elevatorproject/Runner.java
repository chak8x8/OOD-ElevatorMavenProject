package com.fdmgroup.elevatorproject;
import java.util.logging.Logger;

public class Runner {
	public static Logger logger = Logger.getLogger("");

	public static void main(String[] args) {
		// logger.warning("-----------");
		/// Logger.getGlobal().info("log test");
		ElevatorController ec = new ElevatorController();
		ec.run();
	}

}