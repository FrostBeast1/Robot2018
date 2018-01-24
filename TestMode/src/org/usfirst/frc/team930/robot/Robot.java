/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team930.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	
	TalonSRX motor1 = new TalonSRX(0);

	@Override
	public void robotInit() {
		SmartDashboard.putNumber("Talon Ouput", motor1.getMotorOutputPercent());
		SmartDashboard.putBoolean("Update Values", false);
		
	}

	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		// m_autoSelected = SmartDashboard.getString("Auto Selector",
		// 		kDefaultAuto);
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
		//update values using SmartDashboard
		if(SmartDashboard.getBoolean("Update Values", false))
		{
			motor1.set(ControlMode.PercentOutput, SmartDashboard.getNumber("Talon Output", 0));
			SmartDashboard.putBoolean("Update Values", false);
			
			System.out.println("Updated Values");
		}
		SmartDashboard.putNumber("Talon Output", motor1.getMotorOutputPercent());
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		//update values using SmartDashboard
		if(SmartDashboard.getBoolean("Update Values", false))
		{
			motor1.set(ControlMode.PercentOutput, SmartDashboard.getNumber("Talon Output", 0));
			SmartDashboard.putBoolean("Update Values", false);
			
			System.out.println("Updated Values");
		}
		SmartDashboard.putNumber("Talon Output", motor1.getMotorOutputPercent());
	}
}