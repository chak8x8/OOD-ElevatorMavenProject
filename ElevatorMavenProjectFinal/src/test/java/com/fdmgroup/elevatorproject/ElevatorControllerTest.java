package com.fdmgroup.elevatorproject;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ElevatorControllerTest {
	
	ElevatorController controller = new ElevatorController();
	
	@Mock
	Const cons;

	@Test
	void test1_Input1_5_isValid() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String[] input = new String[]{"1","5"};
		//assertFalse(controller.validateInput(input)); //TODO 
	}
	
	@Test
	void test2_Input_Minus1_20_InValid() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String[] input = new String[]{"-1","20"};
		//assertTrue(controller.validateInput(input)); //TODO 
	}
	
	@Test
	void test3_Input_Minus5_15_InValid() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String[] input = new String[]{"-5","15"};
		//assertTrue(controller.validateInput(input)); //TODO 
	}
	
	@Test
	void test4_Input_0_25_InValid() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String[] input = new String[]{"0","25"};
		//assertTrue(controller.validateInput(input)); //TODO 
	}
	
	@Test
	void test5_Input_5_5_InValid() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String[] input = new String[]{"5","5"};
		//assertTrue(controller.validateInput(input)); //TODO 
	}

}
