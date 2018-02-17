package org.usfirst.frc.team930.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TeleopHandler {
	
	private static final Joystick stick1 = new Joystick(0);
	private static final Joystick stick2 = new Joystick(1);
	//private static final Joystick stick3 = new Joystick(2);
	private static final Timer elevatorTimer = new Timer();
	private static boolean buttonCheckRB = false;
	private static boolean buttonCheckLB = false;
	private static int elevatorCounter = 0;
	private static boolean switchBool = false;
	private static double timeAmount = 0;
	private static boolean toggledTwice = false;
	
	public static void init() {
		Utilities.startCapture();
	}
	
	public static void disabled() {
		Drive.updateDashboard();
		Elevator.updateDashboard();
		LEDHandler.run(RobotStates.DISABLED);
	}
	
	enum RobotStates {
		ENABLED,
		DISABLED
	}
	
	enum IntakeStates{
		INTAKING,
		INTAKE_DONE,
		OUTTAKING,
		LIFTER_UP,
		LIFTER_DOWN
	}
	enum ElevatorStates{
		INTAKE_POSITION,
		SWITCH_POSITION,
		SCALE_POSITION_L,
		SCALE_POSITION_M,
		SCALE_POSITION_H
	}
	enum RampStates{
		RIGHT_RAMP_DOWN,
		LEFT_RAMP_DOWN,
		RIGHT_RAMP_UP,
		LEFT_RAMP_UP
	}
	
	public static void run() {
		
		LEDHandler.run(RobotStates.ENABLED);
		
		Drive.run(stick1.getRawAxis(Constants.rightXaxis), stick1.getRawAxis(Constants.leftYaxis));
		
		if(stick2.getRawAxis(Constants.rightTriggerAxis) > 0.7)																
			Intake.run(IntakeStates.INTAKING);
		else if(stick2.getRawAxis(Constants.leftTriggerAxis) > 0.7)																
			Intake.run(IntakeStates.OUTTAKING);
		else
			Intake.run(IntakeStates.INTAKE_DONE);
		
		
		//Elevator
		Elevator.run(stick2.getRawAxis(Constants.rightYaxis));
		
		if (stick2.getRawButton(Constants.RB) && (!buttonCheckRB)) {
			buttonCheckRB = true;
			switchBool = true;
			if(elevatorCounter < 4 && elevatorCounter >= 0){
				elevatorCounter++;
				timeAmount = 0.1 + (0.05 * elevatorCounter);
				elevatorTimer.start();
			}
		} else if ((!stick2.getRawButton(Constants.RB)) && buttonCheckRB) {
			buttonCheckRB = false;
		}

		if (stick2.getRawButton(Constants.LB) && (!buttonCheckLB)) {
			buttonCheckLB = true;
			switchBool = true;
			if(elevatorCounter <= 4 && elevatorCounter > 0){
				elevatorCounter--;
				timeAmount = 0.1 + (0.05 * elevatorCounter);
				elevatorTimer.start();
			}
		} else if ((!stick2.getRawButton(Constants.LB)) && buttonCheckLB) {
			buttonCheckLB = false;
		}
		
		if (switchBool){
			switch(elevatorCounter){
			case 0:
				Elevator.run(ElevatorStates.INTAKE_POSITION);
				switchBool = false;
				break;
			case 1:
				Elevator.run(ElevatorStates.SWITCH_POSITION);
				switchBool = false;
				break;
			case 2:
				Elevator.run(ElevatorStates.SCALE_POSITION_L);
				switchBool = false;
				break;
			case 3:
				Elevator.run(ElevatorStates.SCALE_POSITION_M);
				switchBool = false;
				break;
			case 4:
				Elevator.run(ElevatorStates.SCALE_POSITION_H);
				switchBool = false;
				break;
			}
		}
		
		if(elevatorTimer.get() > 0.001 && elevatorTimer.get() < timeAmount){
			setRumble(2, 1.0);
		} else if(elevatorTimer.get() > timeAmount){
			elevatorTimer.stop();
			elevatorTimer.reset();
		} else {
			setRumble(2, 0);
		}
		
		//stop elevator if encoder is not returning information
		if(Elevator.checkSensor()) {
			Elevator.stop();
		}
		
		if(SmartDashboard.getBoolean("Toggle Camera", false) && !toggledTwice) {
			CameraServer.getInstance().removeCamera("Camera");
			toggledTwice = true;
			SmartDashboard.putBoolean("Toggle Camera", false);
		} else if(SmartDashboard.getBoolean("Toggle Camera", false) && toggledTwice) {
			Utilities.startCapture();
			toggledTwice = false;
			SmartDashboard.putBoolean("Toggle Camera", false);
		}
		
		if(stick2.getRawButton(Constants.A))
			Ramp.run(RampStates.RIGHT_RAMP_DOWN);
		if(stick2.getRawButton(Constants.B))
			Ramp.run(RampStates.LEFT_RAMP_DOWN);
		if(stick2.getRawButton(Constants.X))
			Ramp.run(RampStates.RIGHT_RAMP_UP);
		if(stick2.getRawButton(Constants.Y))
			Ramp.run(RampStates.LEFT_RAMP_UP);
		
		/*if(stick3.getRawButton(7))
			Ramp.run(RampStates.RIGHT_RAMP_DOWN);
		if(stick2.getRawButton(2))
			Ramp.run(RampStates.LEFT_RAMP_DOWN);
		if(stick3.getRawButton(12))
			Ramp.run(RampStates.RIGHT_RAMP_UP);
		if(stick3.getRawButton(1))
			Ramp.run(RampStates.LEFT_RAMP_UP);*/
		
	}
	public static void setRumble(int controller, double intensity){
		if(controller == 1){
			stick1.setRumble(GenericHID.RumbleType.kLeftRumble, intensity);
			stick1.setRumble(GenericHID.RumbleType.kRightRumble, intensity);
		}
		else if(controller == 2){
			stick2.setRumble(GenericHID.RumbleType.kLeftRumble, intensity);
			stick2.setRumble(GenericHID.RumbleType.kRightRumble, intensity);
		}
	}

}