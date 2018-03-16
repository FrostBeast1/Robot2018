package org.usfirst.frc.team930.robot;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;

public class StartRScaleLSwitchL extends Routine {
	
	private Timer time = new Timer();
	private TimeDelay delayElev = new TimeDelay();
	private TimeDelay delayOuttake = new TimeDelay();
	private TimeDelay delayStopIntake = new TimeDelay();
	public static Notifier n;
	
	public StartRScaleLSwitchL(String v, double d) {
		
		super(v, d);
		delayElev.set(0);
		delayOuttake.set(3.5);
		delayStopIntake.set(1);

		n = new Notifier (AutoHandler.myMP7A);
		AutoHandler.myMP7A.startPath();
		
		time.start();
		
		
	}
	
	public void variation() {
		
		switch (this.autoStep) {
			case 1:
				n.startPeriodic(0.02);
				this.autoStep = 3;
				System.out.println("DONE");
				break;
			/*case 1:
				System.out.println("Running case 1");
				actList.wristUp();
				n.startPeriodic(0.02);
				this.autoStep = 2;
				break;*/
			case 2:
				System.out.println("Running case 2");
				if(delayElev.execute(time.get()))	{
					this.autoStep = 3;
					actList.scaleMPosition();
					System.out.println("*****Transition to Case 2");
				}
				break;
			case 3:
				System.out.println("Running case 3");
				if(segList.seg7A()) {
					this.autoStep = 6;
					n.stop();
					//n = new Notifier(AutoHandler.myAutoGT);
					//AutoHandler.myAutoGT.startPath();
					//actList.slowOuttake();
					System.out.println("*****Transition to Case 4");
				}
				break;
			case 4:
				n.startPeriodic(0.02);
				this.autoStep = 5;
				System.out.println("DONE");
				break;
			case 5:
				if(segList.segGyroTurn()) {
					this.autoStep = 6;
					n.stop();
				}
				break;
			case 6:
				if(delayStopIntake.execute(time.get()))
					actList.stopIntake();
				Drive.runAt(0, 0);
				break;
		}
		
	}
	

}
