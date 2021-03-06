package org.usfirst.frc.team930.robot;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;

/*
 * Moves to the left side of the scale when you start on the right
 */
public class StartRScaleLSwitchL extends Routine {
	
	private Timer time = new Timer();
	private TimeDelay delayElev = new TimeDelay();
	private TimeDelay delayOuttake = new TimeDelay();
	private TimeDelay delayStopIntake = new TimeDelay();
	public static Notifier n;
	
	/*
	 * Initializes MP path, Notifier, variation, and individual time delays for robot functions
	 */
	public StartRScaleLSwitchL(String v, double d) {
		
		super(v, d);
		delayElev.set(Constants.ETime3);
		delayOuttake.set(Constants.ITime3);
		delayStopIntake.set(Constants.OTime1);

		n = new Notifier (AutoHandler.mpStartRScaleL);
		AutoHandler.mpStartRScaleL.startPath();
		
		time.start();
		
		
	}
	
	/*
	 * Strings together MP path with actions
	 */
	public void variation() {
		
		switch (this.autoStep) {
			case 1: // Lifts wrist, starts intaking and starts MP path
				System.out.println("Running case 1");
				actList.wristUp();
				actList.intake();
				n.startPeriodic(0.02);
				this.autoStep = 2;
				break;
			case 2: // Lift elevator after time delay
				System.out.println("Running case 2");
				if(delayElev.execute(time.get()))	{
					this.autoStep = 3;
					actList.scaleMPosition();
					System.out.println("*****Transition to Case 2");
				}
				break;
			case 3: // Slow outtake of cube after time delay and checking if MP path is done
				System.out.println("Running case 3");
				if(segList.segStartRScaleL()) {
					this.autoStep = 6;
					n.stop();
					actList.slowOuttake();
					System.out.println("*****Transition to Case 4");
				}
				break;
			case 4: // Starts the gyro turn path
				n.startPeriodic(0.02);
				this.autoStep = 5;
				System.out.println("DONE");
				break;
			case 5: // Checks if gyro turn is done
				if(segList.segGyroTurn()) {
					this.autoStep = 6;
					n.stop();
				}
				break;
			case 6: // Stops intake and drivetrain
				if(delayStopIntake.execute(time.get()))
					actList.stopIntake();
				Drive.runAt(0, 0);
				break;
		}
		
	}
	

}
