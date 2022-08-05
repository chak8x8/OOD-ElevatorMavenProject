package com.fdmgroup.elevatorproject;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ElevatorControllerTest {
	ElevatorController controller; 
		
	@Mock
	Const cons;
	
	//Test invalidCommand method

	@BeforeEach
	void init() {
		controller = new ElevatorController();
	}
	
	@Test
	void test1_Input1_5_gives_false_for_same_floor_command() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String input = "1:1:1";
		assertFalse(controller.validateInput(input));
	}
	
	@Test
	void test2_validateInput_Gives_False_for_instruction_length_less_than_three() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String input = "2:4";
		assertFalse(controller.validateInput(input));
	}
	
	@Test
	void test3_validateInput_gives_false_if_source_floor_out_of_bounds() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String input = "16:2:1";
		assertFalse(controller.validateInput(input));
	}
	
	@Test
	void test4_validateInput_gives_false_if_destination_floor_out_of_bounds() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String input = "1:16:1";
		assertFalse(controller.validateInput(input));
	}
	
	@Test
	void test5_validateInput_returns_false_if_source_equals_destination() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String input = "2:2:1";
		assertFalse(controller.validateInput(input));
	}
	
	
	@Test
	void test6_validateInput_returns_false_for_single_number() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String input = "55";
		assertFalse(controller.validateInput(input));
	}
	
	@Test
	void test7_validateInput_returns_false_if_number_of_people_greater_than_max_cap_per_command() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		Const.maxCapPerCommand = 9;
		String input = "5:6:10";
		assertFalse(controller.validateInput(input));
	}
	
	@Test
	void test8_validateInput_returns_false_if_number_of_people_is_zero() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String input = "5:6:0";
		assertFalse(controller.validateInput(input));
	}
	
	@Test
	void test8_validateInput_gives_false_for_missing_colon() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String input = "510";
		assertFalse(controller.validateInput(input));
	}
	
	@Test
	void test9_validateInput_gives_false_for_special_character() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String input = "5!10:1";
		assertFalse(controller.validateInput(input));
	}

	@Test
	void test10_validateInput_gives_false_for_num_of_people_less_than_zero() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String input = "5:10:-1";
		assertFalse(controller.validateInput(input));
	}
	
	@Test
	void test11_validateInput_gives_false_for_invalidCommand_Inside_array() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String input = "5:10:1,5:5:5";
		assertFalse(controller.validateInput(input));
	}
	
	@Test
	void test12_validateInput_gives_false_for_special_character_Inside_array() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String input = "5:10:1,5!11:1";
		assertFalse(controller.validateInput(input));
	}
	
	@Test
	void test13_validateInput_gives_false_for_missing_colon_Inside_array() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String input = "5:10:1,511:1";
		assertFalse(controller.validateInput(input));
	}
	
	@Test
	void test14_validateInput_gives_True_for_two_valid_commands() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String input = "5:10:1,1:2:1";
		assertTrue(controller.validateInput(input));
	}
	
	@Test
	void test15_validateInput_gives_true_for_three_valid_commands_in_string() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		cons.maxCapPerCommand = 3;
		String input = "5:10:2,5:11:3,6:15:1";
		assertTrue(controller.validateInput(input));
	}
	
	@Test
	void test16_validateInput_gives_true_for_four_valid_commands_in_string() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		cons.maxCapPerCommand = 4;
		String input = "5:10:1,5:11:2,6:15:2,1:12:3";
		assertTrue(controller.validateInput(input));
	}
	
	@Test
	void test17_validateInput_gives_true_for_five_valid_commands_in_string() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		cons.maxCapPerCommand = 4;
		String input = "5:10:1,5:11:2,6:15:3,1:12:4,12:2:2";
		assertTrue(controller.validateInput(input));
	}
	
	@Test
	void test18_validateInput_ignores_whitespace_for_valid_command_string() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String input = "5:10:1,5 : 11:1,6:15:1,1  :12:1,12:2:1";
		assertTrue(controller.validateInput(input));
	}
	
	@Test
	void test19_validateInput_ignores_whitespace_for_valid_single_command() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String input = "5 :   1  :1";
		assertTrue(controller.validateInput(input));
	}
	
	@Test
	void test21_validateInput_gives_false_if_negative_floor_is_found() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String input = "-5:6:1";
		assertFalse(controller.validateInput(input));
	}
	
	@Test
	void test22_validateInput_gives_false_if_negative_destination_is_found() {
		cons.minFloor = 0;
		cons.maxFloor = 15;
		String input = "5:-16:1";
		assertFalse(controller.validateInput(input));
	}
	
	//Testing commandAdd() Method
	
	@Test
	void test23_commandList_is_empty_if_invalid_instruction_is_given_to_commandAdd_method() {
		cons.minFloor = 0;
		cons.maxFloor = 20;
		String input = "5:21";
		controller.commandAdd(input);
		assertEquals(0, controller.getCommandList().size());
	}
	
	@Test
	void test24_commandList_is_size_one_if_valid_instruction_given_to_commandAdd_method() {
		cons.minFloor = 0;
		cons.maxFloor = 20;
		String input = "5:21";
		controller.commandAdd(input);
		assertEquals(0, controller.getCommandList().size());
	}
	
	@Test
	void test25_commandList_is_size_one_if_valid_instruction_given_to_commandAdd_method() {
		cons.minFloor = 0;
		cons.maxFloor = 20;
		String input = "5:20:1";
		controller.commandAdd(input);
		assertEquals(1, controller.getCommandList().size());
	}
	
	@Test
	void test26_commandList_is_size_zero_if_invalid_instructions_given_to_commandAdd_method() {
		cons.minFloor = 0;
		cons.maxFloor = 20;
		String input = "5:21:1,12:12:1";
		controller.commandAdd(input);
		assertEquals(0, controller.getCommandList().size());
	}
	
	@Test
	void test27_commandList_is_size_two_if_two_valid_instructions_given_to_commandAdd_method() {
		cons.minFloor = 0;
		cons.maxFloor = 20;
		String input = "5:20:1,12:13:1";
		controller.commandAdd(input);
		assertEquals(2, controller.getCommandList().size());
	}
	
	@Test
	void test28_commandList_is_size_three_if_three_valid_commands_given_to_commandAdd_method() {
		cons.minFloor = 0;
		cons.maxFloor = 20;
		cons.maxCapPerCommand = 4;
		String input = "5:20:4,12:13:1,17:18:3";
		controller.commandAdd(input);
		assertEquals(3, controller.getCommandList().size());
	}
	
	@Test
	void test29_commandList_is_size_ten_if_ten_valid_commands_given_to_commandAdd_method() {
		cons.minFloor = 0;
		cons.maxFloor = 20;
		cons.maxCapPerCommand = 5;
		String input = "5:20:5,12:13:1,17:18:2,1:10:3,11:14:4,20:2:1,6:11:4,9:3:5,7:15:2,17:1:3";
		controller.commandAdd(input);
		assertEquals(10, controller.getCommandList().size());
	}
	
	//Testing readConfig() method
	
	@Test
	void test30_readConfig_method_ReadsInNumberOfElevators() {
		controller.readConfig();
		assertEquals(4, Const.numOfElevators);
	}
	
	@Test
	void test30_readConfig_method_ReadsIn_numOfFloors() {
		controller.readConfig();
		assertEquals(15, Const.numOfFloors);
	}
	
	@Test
	void test31_readConfig_method_ReadsIn_maxCapPerElevator() {
		controller.readConfig();
		assertEquals(8, Const.maxCapPerElevator);
	}
	
	@Test
	void test32_readConfig_method_ReadsIn_maxCapPerCommand() {
		controller.readConfig();
		assertEquals(16, Const.maxCapPerCommand);
	}
	
	@Test
	void test33_readConfig_method_ReadsIn_minFloor_default() {
		controller.readConfig();
		assertEquals(0, Const.minFloor);
	}
	
	@Test
	void test34_readConfig_sets_elevatorList_size() {
		controller.readConfig();
		assertEquals(4, controller.getElevatorList().size());
	}
	
	@Test
	void controller_isExit_is_true_if_exit_entered_to_commandAdd() {
		String input = "exit";
		controller.commandAdd("exit");
		
		assertTrue(controller.getIsExit());
	}
	
	@Test
	void controller_will_join_all_config_elevators() {
		controller.readConfig();
		controller.join();
		
		assertEquals(4, controller.getElevatorList().size());
		
	}
	
	@Mock
	ElevatorController controller2;
	
	@Test
	void run_command_will_start_elevators_and_exit_if_exit_entered() {
		controller2.run();
	
	}
}