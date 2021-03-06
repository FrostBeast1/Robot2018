package org.usfirst.frc.team930.robot;


import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



public class Robot extends TimedRobot {
	//-- Joystick Object Declarations and Instantiations --\\
	Joystick controller = new Joystick(0);
	Joystick controller2 = new Joystick(1);
	
	
	//-- Driving Object Declarations and Instantiations --\\
	WPI_TalonSRX rightMain = new WPI_TalonSRX(0);  //Declarations for talons
	WPI_TalonSRX leftMain = new WPI_TalonSRX(1);  //These will be the main motor controllers
	VictorSPX rightFollow = new VictorSPX(2);     //Declarations for victors that are
	VictorSPX leftFollow = new VictorSPX(3);   //followers to the talons
	VictorSPX rightFollow2 = new VictorSPX(4);     //Declarations for victors that are
	VictorSPX leftFollow2 = new VictorSPX(5);   //followers to the talons
	DifferentialDrive robot = new DifferentialDrive(leftMain, rightMain);  //Declares the driving method control for robot
	
	
	//-- Elevator Object Declarations and Instantiations --\\
	WPI_TalonSRX lift1 = new WPI_TalonSRX(6);
	//VictorSPX lift2 = new VictorSPX(1);
	
	
	//-- Intake Object Declarations and Instantiations--\\
	VictorSPX rightIntakeWheel = new VictorSPX(7);
	VictorSPX leftIntakeWheel = new VictorSPX(8);
	//Solenoid rightSolenoid = new Solenoid(9);
	//Solenoid leftSolenoid = new Solenoid(10);  
	PowerDistributionPanel PDP = new PowerDistributionPanel();
	
	Compressor comp = new Compressor(0);
	
	//-- Intake Variable Declarations --\\
	double currentThreshhold,	//Threshhold for the current of channel 11 (in AMPs).
	intakeMotorSpeed;			//Speed of intake motors.
	boolean holdingCube;		//Check for cube.
	int PDPcounter,				//Counter for PDP checks.
	PDPcounterLimit;			//The amount of times we need to check before we begin in take.	
	
	
	boolean aPressed, onOffA, bPressed, onOffB, test1;
	double targetPosition, fValue, pValue, iValue, dValue, returnPos;
	int velocity, acceleration;
	boolean stopBool = false;
	
	int count = 0;
	boolean pressed6 = false, pressed5 = false, switchBool = false;
	// a-button 6, b-button 5
	
	@Override
	public void robotInit() {		
		// Motion Magic Initialization
		/* first choose the sensor */
		lift1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, Constants1.kPIDLoopIdx, Constants1.kTimeoutMs);
		lift1.setSensorPhase(false);
		lift1.setInverted(false);

		/* Set relevant frame periods to be at least as fast as periodic rate */
		lift1.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, Constants1.kTimeoutMs);
		lift1.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, Constants1.kTimeoutMs);

		/* set the peak and nominal outputs */
		lift1.configNominalOutputForward(0, Constants1.kTimeoutMs);
		lift1.configNominalOutputReverse(0, Constants1.kTimeoutMs);
		lift1.configPeakOutputForward(1, Constants1.kTimeoutMs);
		lift1.configPeakOutputReverse(-1, Constants1.kTimeoutMs);
		
		lift1.configForwardSoftLimitThreshold(7000, Constants1.kTimeoutMs);
		lift1.configReverseSoftLimitThreshold(50, Constants1.kTimeoutMs);

		/* set closed loop gains in slot0 - see documentation */
		lift1.selectProfileSlot(Constants1.kSlotIdx, Constants1.kPIDLoopIdx);
		lift1.config_kF(0, 1.89, Constants1.kTimeoutMs);
		lift1.config_kP(0, 0.5, Constants1.kTimeoutMs);
		lift1.config_kI(0, 0, Constants1.kTimeoutMs);
		lift1.config_kD(0, 10, Constants1.kTimeoutMs);
		/* set acceleration and vcruise velocity - see documentation */
		lift1.configMotionCruiseVelocity(800, Constants1.kTimeoutMs);
		lift1.configMotionAcceleration(800, Constants1.kTimeoutMs);
		/* zero the sensor */
		lift1.setSelectedSensorPosition(0, Constants1.kPIDLoopIdx, Constants1.kTimeoutMs);
		
		//-------------------------------
		
		aPressed = false;
		onOffA = false;
		bPressed = false;
		onOffB = false;
		
		//-- Used for driving --\\
		rightFollow.follow(rightMain);   //Sets the victors to follow their 
		leftFollow.follow(leftMain);   //respective talons
		rightFollow2.follow(rightMain);   //Sets the victors to follow their 
		leftFollow2.follow(leftMain);   //respective talons
		robot.setQuickStopThreshold(0.1);
		
		comp.setClosedLoopControl(true);
		
		//-- In take Variable Initializations --\\
		holdingCube = false;
		currentThreshhold = 35.0;
		intakeMotorSpeed = 0.75;
		PDPcounter = 0;
		PDPcounterLimit = 15;
		
		targetPosition = 0;
	}

	
	@Override
	public void autonomousInit() {
	}

	
	@Override
	public void autonomousPeriodic() {
	}

	
	@Override
	public void teleopPeriodic() {
		//boolean check;  //Value to do the quick turn or not
		
		double rightXStick = controller.getRawAxis(4); //Right joystick X axis
		double leftYStick = controller.getRawAxis(1); //Left joystick Y axis
		double targetPos = controller2.getRawAxis(1) * -3500 + 3500;
		
		robot.setDeadband(0.1);  //Sets the deadband for the controller values
		
				
		/*		
		if(controller.getRawAxis(1) < 0.02)
			check = true;
		else                      //Tells the robot when to do a quick turn
			check = false;*/
				
		//robot.curvatureDrive(leftYStick, rightXStick, false);  //sends the values to the drivetrain
		robot.arcadeDrive(leftYStick, -0.75 * rightXStick);
		
		
		//-- Basic Manual In take Code for Testing --\\
		
		if (controller.getRawAxis(3) > 0.7 || controller2.getRawAxis(3) > 0.7) {									//If the RT button is down																		
			rightIntakeWheel.set(ControlMode.PercentOutput, -intakeMotorSpeed); //Turn on motors
			leftIntakeWheel.set(ControlMode.PercentOutput, intakeMotorSpeed); 		
		} else if (controller.getRawAxis(2) > 0.7 || controller2.getRawAxis(2) > 0.7) {																//If the RT button is up
			rightIntakeWheel.set(ControlMode.PercentOutput, intakeMotorSpeed); 	//Turn right motor forwards.
			leftIntakeWheel.set(ControlMode.PercentOutput, -intakeMotorSpeed);
		} else {
			rightIntakeWheel.set(ControlMode.PercentOutput, 0);					//Stop motors						
			leftIntakeWheel.set(ControlMode.PercentOutput, 0);
		}
		
		if(controller.getRawButton(4)) {
			controller.setRumble(GenericHID.RumbleType.kLeftRumble , 0.5);
			controller.setRumble(GenericHID.RumbleType.kRightRumble , 0.5);
		} else if(controller2.getRawButton(4)) {
			controller2.setRumble(GenericHID.RumbleType.kLeftRumble , 0.5);
			controller2.setRumble(GenericHID.RumbleType.kRightRumble , 0.5);
		} else {
			controller.setRumble(GenericHID.RumbleType.kLeftRumble , 0);
			controller.setRumble(GenericHID.RumbleType.kRightRumble , 0);
			controller2.setRumble(GenericHID.RumbleType.kLeftRumble , 0);
			controller2.setRumble(GenericHID.RumbleType.kRightRumble , 0);
		}
		
		
		/* a pressed -- elevator up
		if(controller.getRawButton(1) && (!aPressed))
		{
			aPressed = true;
			onOffA = !onOffA;
		} else if((!controller.getRawButton(1)) && aPressed) {
			aPressed = false;
		}
		
		if(onOffA) {
			lift1.set(ControlMode.PercentOutput, 1);
		} else {
			lift1.set(ControlMode.PercentOutput, 0);
		}
		
		
		// b pressed -- elevator down
		if(controller.getRawButton(2) && (!bPressed))
		{
			bPressed = true;
			onOffB = !onOffB;
		} else if((!controller.getRawButton(2)) && bPressed) {
			bPressed = false;
		}
				
		if(onOffB) {
			lift1.set(ControlMode.PercentOutput, -1);
		} else {
			lift1.set(ControlMode.PercentOutput, 0);
		}*/
		
		//Left joystick -- elevator control
		/*if(controller2.getRawAxis(1) < -0.2) {
			lift1.set(ControlMode.MotionMagic, 6500);
		} else if(controller2.getRawAxis(1) > 0.2 && lift1.getSelectedSensorPosition(0) > 0) {
			lift1.set(ControlMode.MotionMagic, 0);
		} else {
			lift1.set(ControlMode.PercentOutput, 0);
		}*/
		
		if((targetPosition < 6500 && targetPosition >=0) && controller2.getRawAxis(5) < -0.2){
			targetPosition += (controller2.getRawAxis(5) * -200);
		} else if((targetPosition <= 6500 && targetPosition > 0) && controller2.getRawAxis(5) > 0.2){
			targetPosition -= (controller2.getRawAxis(5) * 200);
		}
		
		if(targetPosition > 6500) {
			targetPosition = 6500;
		} else if (targetPosition < 0) {
			targetPosition = 0;
		}
		
		if(controller2.getRawAxis(5) < -0.2 || controller2.getRawAxis(5) > 0.2){
			if(controller2.getRawAxis(5) < -0.2 || controller2.getRawAxis(5) > 0.2) {
				lift1.set(ControlMode.MotionMagic, targetPosition);
				stopBool = true;
			} else {
				if(stopBool) {
					returnPos = lift1.getSelectedSensorPosition(0);
					if(lift1.getSelectedSensorVelocity(0) > 0) {
						returnPos += (lift1.getSelectedSensorVelocity(0) * 2.3);
					} else if(lift1.getSelectedSensorVelocity(0) < 0) {
						returnPos += (lift1.getSelectedSensorVelocity(0) * 2.3);
					}
					stopBool = false;
					targetPosition = returnPos;
				}
				lift1.set(ControlMode.MotionMagic, targetPosition);
				//lift1.set(ControlMode.PercentOutput, 0);
			}
		}else {
			if (controller2.getRawButton(6) && (!pressed6)) {
				pressed6 = true;
				switchBool = true;
				if(count < 4 && count >= 0){
					count++;
					controller2.setRumble(GenericHID.RumbleType.kLeftRumble , 0.5);
					controller2.setRumble(GenericHID.RumbleType.kRightRumble , 0.5);
					Timer.delay(0.05 + (0.05 * count));
					controller2.setRumble(GenericHID.RumbleType.kLeftRumble , 0.0);
					controller2.setRumble(GenericHID.RumbleType.kRightRumble , 0.0);
				}
			} else if ((!controller2.getRawButton(6)) && pressed6) {
				pressed6 = false;
			}

			if (controller2.getRawButton(5) && (!pressed5)) {
				pressed5 = true;
				switchBool = true;
				if(count <= 4 && count > 0){
					count--;
					controller2.setRumble(GenericHID.RumbleType.kLeftRumble , 0.5);
					controller2.setRumble(GenericHID.RumbleType.kRightRumble , 0.5);
					Timer.delay(0.05 + (0.05 * count));
					controller2.setRumble(GenericHID.RumbleType.kLeftRumble , 0.0);
					controller2.setRumble(GenericHID.RumbleType.kRightRumble , 0.0);
				}
			} else if ((!controller2.getRawButton(5)) && pressed5) {
				pressed5 = false;
			}
			
			if (switchBool){
				switch(count){
				case 0:
					targetPosition = 0;
					lift1.set(ControlMode.MotionMagic, targetPosition);
					switchBool = false;
					break;
				case 1:
					targetPosition = 1000;
					lift1.set(ControlMode.MotionMagic, targetPosition);
					switchBool = false;
					break;
				case 2:
					targetPosition = 2000;
					lift1.set(ControlMode.MotionMagic, targetPosition);
					switchBool = false;
					break;
				case 3:
					targetPosition = 4000;
					lift1.set(ControlMode.MotionMagic, targetPosition);
					switchBool = false;
					break;
				case 4:
					targetPosition = 6500;
					lift1.set(ControlMode.MotionMagic, targetPosition);
					switchBool = false;
					break;
				}
			}
		}
		SmartDashboard.putNumber("Position", lift1.getSelectedSensorPosition(0));
		SmartDashboard.putNumber("Target", targetPosition);
		
		/*
		if (controller2.getRawButton(6)) {
			// Motion Magic - 4096 ticks/rev * 10 Rotations in either direction 
			lift1.set(ControlMode.MotionMagic, 7000);
		} else if (controller2.getRawButton(5)) {
			lift1.set(ControlMode.MotionMagic, 10);
		} else {
			if(controller2.getRawAxis(5) > 0.2 || controller2.getRawAxis(5) < -0.2) {
				lift1.set(ControlMode.PercentOutput, -0.3 * (controller2.getRawAxis(5)));
			}
			else {
				lift1.set(ControlMode.PercentOutput, 0);
			}
		}*/
	}

	public class Constants1 {
		public static final int kSlotIdx = 0;
		public static final int kPIDLoopIdx = 0;
		public static final int kTimeoutMs = 10;
		public static final double intakePosition = 0;
		public static final double switchPosition = 2000;
		public static final double scalePosition = 7000;
	}
	
	@Override
	public void testPeriodic() {
	}
}
