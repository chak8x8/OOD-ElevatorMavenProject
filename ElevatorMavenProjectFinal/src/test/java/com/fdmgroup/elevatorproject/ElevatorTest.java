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
	
	@Test
	void dealWithCommandInMovingDown_method_returns_true_if_source_lower_than_current_and_num_lower_than_rest() {
		elevator.setRunningState(RunningState.movingDown);
		elevator.setCurrentFloor(4);
		command = new ArrayList<> (Arrays.asList(3, 2, 1));
		
		elevator.dealWithCommand(command);
		assertTrue(elevator.dealWithCommand(command));
	}
	
	@Test
	void dealWithCommandInMovingDown_method_returns_true_if_source_lower_than_current_and_num_greater_than_rest() {
		elevator.setRunningState(RunningState.movingDown);
		elevator.setCurrentFloor(4);
		command = new ArrayList<> (Arrays.asList(3, 2, 8));
		
		assertTrue(elevator.dealWithCommand(command));
	}
	
	
	@Test
	void dealWithCommandInMovingUp_if_des_greater_than_source_and_source_greater_than_current() {
		elevator.setRunningState(RunningState.movingUp);
		elevator.setCurrentFloor(4);
		command = new ArrayList<> (Arrays.asList(5, 8, 1));
		
		assertTrue(elevator.dealWithCommand(command));
	}
	
	@Test
	void dealWithCommandInMovingUp_if_des_greater_than_source_and_source_greater_than_current_and_num_greater_than_rest() {
		elevator.setRunningState(RunningState.movingUp);
		elevator.setCurrentFloor(4);
		command = new ArrayList<> (Arrays.asList(5, 8, 9));
		
		assertFalse(elevator.dealWithCommand(command));
	}
	
	@Test
	void subRun_will_set_state_if_it_has_only_upList() {
		ArrayList<Integer> valueList1 = new ArrayList<> ();
		ArrayList<Integer> valueList2 = new ArrayList<> (Arrays.asList(1,2,3));
		
		ArrayList<ArrayList<Integer>> list1 = new ArrayList<ArrayList<Integer>> ();
		ArrayList<ArrayList<Integer>> list2 = new ArrayList<ArrayList<Integer>> ();
	
		list2.add(valueList2);
		
		elevator.setRunningState(RunningState.movingUp);
		
		elevator.subRun(RunningState.movingUp, list1, list2);
		
		assertEquals(RunningState.movingUp, elevator.getRunningState());
	}
	
	@Test
	void subRun_will_stop_elevator_and_open_doors_if_currentFloor_equals_a_destination() {
		ArrayList<Integer> valueList1 = new ArrayList<> ();
		ArrayList<Integer> valueList2 = new ArrayList<> (Arrays.asList(1,2,3));
		
		ArrayList<ArrayList<Integer>> list1 = new ArrayList<ArrayList<Integer>> ();
		ArrayList<ArrayList<Integer>> list2 = new ArrayList<ArrayList<Integer>> ();
		
		list1.add(valueList2);
		
		elevator.setRunningState(RunningState.movingUp);
		elevator.setCurrentFloor(1);
	
		elevator.subRun(RunningState.movingUp, list1, list2);
	
		assertEquals(DoorState.doorOpening, elevator.getDoorState());
	}
	
	@Test
	void subRun_will_move_up_one_floor_if_movingUp_and_not_at_destination() {
		ArrayList<Integer> valueList1 = new ArrayList<> ();
		ArrayList<Integer> valueList2 = new ArrayList<> (Arrays.asList(2,3,4));
		
		ArrayList<ArrayList<Integer>> list1 = new ArrayList<ArrayList<Integer>> ();
		ArrayList<ArrayList<Integer>> list2 = new ArrayList<ArrayList<Integer>> ();
		//list1.add();
		list1.add(valueList2);
		
		elevator.setRunningState(RunningState.movingUp);
		elevator.setCurrentFloor(1);
		
		elevator.subRun(RunningState.movingUp, list1, list2);
	
		assertEquals(RunningState.movingUp, elevator.getRunningState());
		assertEquals(2, elevator.getCurrentFloor());
	}
	
	@Test
	void subRun_will_move_down_one_floor_if_movingDown_and_not_at_destination() {
		ArrayList<Integer> valueList = new ArrayList<> (Arrays.asList(2,3,4));
		ArrayList<ArrayList<Integer>> list1 = new ArrayList<ArrayList<Integer>> ();
		ArrayList<ArrayList<Integer>> list2 = new ArrayList<ArrayList<Integer>> ();
		list1.add(valueList);
		
		elevator.setRunningState(RunningState.movingDown);
		elevator.setCurrentFloor(7);
		
		elevator.subRun(RunningState.movingDown, list1, list2);
		
		assertEquals(6, elevator.getCurrentFloor());
		assertEquals(RunningState.movingDown, elevator.getRunningState());
	}
	
	@Test 
	void subRun_will_set_door_closing_if_door_is_open() {
		elevator.setDoorState(DoorState.doorOpening);
		ArrayList<ArrayList<Integer>> list1 = new ArrayList<ArrayList<Integer>> ();
		ArrayList<ArrayList<Integer>> list2 = new ArrayList<ArrayList<Integer>> ();
		elevator.subRun(RunningState.movingUp, list1, list2);
		
		assertEquals(DoorState.doorClosing, elevator.getDoorState());
	}
	
	@Test 
	void subRun_will_set_door_closed_if_doorState_is_door_closing() {
		ArrayList<Integer> valueList = new ArrayList<> (Arrays.asList(2,3,4));
		elevator.setDoorState(DoorState.doorClosing);
		ArrayList<ArrayList<Integer>> list1 = new ArrayList<ArrayList<Integer>> ();
		ArrayList<ArrayList<Integer>> list2 = new ArrayList<ArrayList<Integer>> ();
		list1.add(valueList);
		elevator.subRun(RunningState.movingUp, list1, list2);
		
		assertEquals(DoorState.doorClosed, elevator.getDoorState());
	}

	@Test 
	void subRun_will_set_doorState_idle_if_door_is_close_and_updownList_is_empty() {
		elevator.setDoorState(DoorState.doorClosing);
		elevator.setRunningState(RunningState.movingUp);
		
		ArrayList<ArrayList<Integer>> list1 = new ArrayList<ArrayList<Integer>> ();
		ArrayList<ArrayList<Integer>> list2 = new ArrayList<ArrayList<Integer>> ();
	
		elevator.subRun(RunningState.movingUp, list1, list2);
		
		assertEquals(RunningState.idle, elevator.getRunningState());
	}
	
	@Test
	void currentInfo_returns_formatted_info() {
		elevator.setElevatorID(24);
		String StringCurrentInfo = "24 :  currentFloor = 0 state : idle , person(s) : 0";
		assertEquals(StringCurrentInfo, elevator.currentInfo());
	}
	
	
	@Test
	void currentInfo_returns_formatted_info_for_moving_elevator() {
		elevator.setElevatorID(26);
		elevator.setRunningState(RunningState.movingDown);
		String StringCurrentInfo = "26 :  currentFloor = 0 state : movingDown , person(s) : 0";
		assertEquals(StringCurrentInfo, elevator.currentInfo());
	}
	
	@Test
	void run_command_start_threads() {
		elevator.setExit(true);
		elevator.setDealExit(true);
		elevator.setCurrentFloor(Const.minFloor);
		elevator.run();

	}
	
	@Test
	void start_command_starts_threads() {
		elevator.setExit(true);
		elevator.setDealExit(true);
		elevator.setCurrentFloor(Const.minFloor);
		elevator.start();
	}
	
	@Test
	void compareTo_returns_negativeOne_if_runningState_is_not_idle() {
		Elevator elevator2 = new Elevator();
		elevator.setRunningState(RunningState.movingDown);
		int isEqual = elevator.compareTo(elevator2);
		assertEquals(-1, isEqual);
	}
	
	@Test
	void compareTo_returns_one_if_running_state_is_idle_and_other_is_not() {
		Elevator elevator2 = new Elevator();
		
		elevator.setRunningState(RunningState.idle);
		elevator2.setRunningState(RunningState.movingDown);
		
		int isEqual = elevator.compareTo(elevator2);
		assertEquals(1, isEqual);
	}
	
	@Test
	void compareTo_returns_zero_if_running_state_is_idle_and_other_is_idle() {
		Elevator elevator2 = new Elevator();
		
		elevator.setRunningState(RunningState.idle);
		elevator2.setRunningState(RunningState.idle);
		
		int isEqual = elevator.compareTo(elevator2);
		assertEquals(0, isEqual);
	}
}
