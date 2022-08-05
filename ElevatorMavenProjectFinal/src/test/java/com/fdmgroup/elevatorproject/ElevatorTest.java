package com.fdmgroup.elevatorproject;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ElevatorTest {
	Elevator elevator;
	ArrayList<Integer> command;

	@BeforeEach
	void init() {
		elevator = new Elevator();
		cons.maxCapPerElevator = 8;
	}
	//Testing dealWithCommand method. Don't need to check if input is valid as this is dealt with in controller.
	@Mock
	Const cons;
	
	@Test
	void test1_dealWithCommandinIdle_method_returns_false_if_num_people_greater_than_maxCap() {
		elevator.setRunningState(RunningState.idle);
		command = new ArrayList<> (Arrays.asList(2, 4, 9));
		
		boolean commandBoolean = elevator.dealWithCommand(command);
		
		assertEquals(false, commandBoolean);
	}
	
	@Test
	void dealWithCommandinIdle_method_returns_true_for_a_valid_command() {
		
		elevator.setRunningState(RunningState.idle);
		cons.maxCapPerElevator = 8;
		command = new ArrayList<> (Arrays.asList(2, 4, 1));
		boolean commandTrue = elevator.dealWithCommand(command);
		
		assertEquals(true, commandTrue);
	}
	
	@Test
	void dealWithCommandinIdle_method_returns_true_for_num_equals_maxCap() {
		
		elevator.setRunningState(RunningState.idle);
		cons.maxCapPerElevator = 8;
		command = new ArrayList<> (Arrays.asList(2, 4, 8));
		boolean commandTrue = elevator.dealWithCommand(command);
		
		assertEquals(true, commandTrue);
	}
	
	@Test
	void dealWithCommandinIdle_method_set_runningState_to_down_if_source_lower_than_current() {
		elevator.setRunningState(RunningState.idle);
		cons.maxCapPerElevator = 8;
		elevator.setCurrentFloor(10);
		command = new ArrayList<> (Arrays.asList(2, 4, 8));
		elevator.dealWithCommand(command);
		
		
		assertEquals(RunningState.movingDown, elevator.getRunningState());
	}
	
	@Test
	void dealWithCommandinIdle_method_sets_runningState_to_up_if_source_higher_than_current() {
		elevator.setRunningState(RunningState.idle); 
		elevator.setCurrentFloor(1);
		command = new ArrayList<> (Arrays.asList(2, 4, 8));
		
		elevator.dealWithCommand(command);
		
		assertEquals(RunningState.movingUp, elevator.getRunningState());
	}
	
	@Test
	void dealWithCommandinIdle_method_sets_runningState_to_up_if_source_equals_current_and_dest_higher_than_src() {
		elevator.setRunningState(RunningState.idle);
		elevator.setCurrentFloor(2);
		command = new ArrayList<> (Arrays.asList(2, 4, 8));
		
		elevator.dealWithCommand(command);
		
		assertEquals(RunningState.movingUp, elevator.getRunningState());
	}
	
	@Test
	void dealWithCommandinIdle_method_sets_runningState_to_down_if_source_equals_current_and_dest_lower_than_src() {
		elevator.setRunningState(RunningState.idle);
		elevator.setCurrentFloor(4);
		command = new ArrayList<> (Arrays.asList(4, 2, 8));
		
		elevator.dealWithCommand(command);
		
		assertEquals(RunningState.movingDown, elevator.getRunningState());
	}
	
	@Test
	void dealWithCommandInMovingDown_method_returns_false_if_source_is_lower_than_current_and_d_higher_than_s() {
		elevator.setRunningState(RunningState.movingDown);
		elevator.setCurrentFloor(4);
		command = new ArrayList<> (Arrays.asList(1, 2, 8));
		
		elevator.dealWithCommand(command);
		
		assertEquals(RunningState.movingDown, elevator.getRunningState());
	}
	
	@Test
	void dealWithCommandInMovingUp_method_returns_false_if_command_is_false() {
		elevator.setRunningState(RunningState.idle);
		elevator.setCurrentFloor(4);
		command = new ArrayList<> (Arrays.asList(4, 2, 8));
		
		elevator.dealWithCommand(command);
		
		assertEquals(RunningState.movingDown, elevator.getRunningState());
	}
	
	@Test
	void dealWithCommandInMovingDown_method_returns_false_if_source_is_lower_than_current_and_s_higher_than_s() {
		elevator.setRunningState(RunningState.movingDown);
		elevator.setCurrentFloor(4);
		command = new ArrayList<> (Arrays.asList(5, 2, 8));
		
		elevator.dealWithCommand(command);
		assertFalse(elevator.dealWithCommand(command));


	}
	
	
}