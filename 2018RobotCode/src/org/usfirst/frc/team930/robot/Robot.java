/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team930.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

public class Robot extends IterativeRobot {

	@Override
	public void robotInit() {
		
		Drive.init();
		Intake.init();
		Elevator.init();
		Ramp.init();
		AutoHandler.robotInit();
		TeleopHandler.init();
		//LEDHandler.init();
		//Utilities.startCapture();
		
		LiveWindow.disableAllTelemetry();
		
	}

	@Override
	public void autonomousInit() {
		
		System.out.println("Init Start");
		System.out.println("Gyro: " + Drive.gyro.isConnected());
		AutoHandler.autoInit();
		System.out.println("Init End");
		
	}

	@Override
	public void autonomousPeriodic() {
		//LEDHandler.autoRun();
		AutoHandler.run();
		
	}
	
	public void teleopInit() {
		
		TeleopHandler.init();
		Drive.changeSensorPhase(false, true);
		Drive.invertMotorsForwards();
		
	}

	@Override
	public void teleopPeriodic() {
		
		TeleopHandler.run();
		
	}
	
	public void disabledInit() {
		AutoHandler.mpStartRScaleR.disabled();
	}
	
	@Override
	public void disabledPeriodic() {
		
		TeleopHandler.disabled();
		AutoHandler.disabled();
		//System.out.println(Elevator.lift1.getSelectedSensorPosition(0));
		
	}

	@Override
	public void testPeriodic() {
		
	}
}
