/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team930.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;

public class Robot extends TimedRobot {
	
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	
	Joystick controller = new Joystick(0);
	
	boolean aPressed, bPressed;
	int bCounter;
	
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		
		aPressed = false;
		bPressed = false;
		bCounter = 0;
		
	}

	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		// m_autoSelected = SmartDashboard.getString("Auto Selector",
		// kDefaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
		
	}

	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {
		case kCustomAuto:
			// Put custom auto code here
			break;
		case kDefaultAuto:
		default:
			// Put default auto code here
			break;
		}
	}

	@Override
	public void teleopPeriodic() {
		if (controller.getRawButton(1)) {
			aPressed = true;
			SmartDashboard.putBoolean("Am I Pressing The A Button?", aPressed);
		}
		else {
			aPressed = false;
			SmartDashboard.putBoolean("Am I Pressing The A Button?", aPressed);
		}
		
		if (controller.getRawButton(2) && !bPressed) {
			bPressed = true;
		}
		else if (!controller.getRawButton(2) && bPressed) {
			bPressed = false;
			bCounter ++;
			SmartDashboard.putNumber("# of Spam Clicks on the B Button", bCounter);
		}
	}

	@Override
	public void testPeriodic() {
		
	}
}