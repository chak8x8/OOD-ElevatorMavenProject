package com.fdmgroup.elevatorproject;
import java.util.ArrayList;

public class ElevatorView {
	public void display(ArrayList<Elevator> elevators) {
		for (Elevator e : elevators)
			System.out.println(e.currentInfo());

	}
}
