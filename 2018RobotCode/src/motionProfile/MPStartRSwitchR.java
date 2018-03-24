package motionProfile;

import java.io.File;

import org.usfirst.frc.team930.robot.Drive;
import org.usfirst.frc.team930.robot.Utilities;

import com.ctre.phoenix.motorcontrol.ControlMode;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

// Start R Switch R Scale L 1
public class MPStartRSwitchR implements Runnable {
	
	private static EncoderFollower rightFollower;
	private static EncoderFollower leftFollower;

	public MPStartRSwitchR() {
		
		//Drive.gyro.reset();
		
		Waypoint[] rightLeftScale = new Waypoint[] {
				new Waypoint(0.7, -3.1, Pathfinder.d2r(0)),
				new Waypoint(3.2, -3.1, Pathfinder.d2r(0)),
				new Waypoint(4.0, -2.3, Pathfinder.d2r(90)),
		}; // Vel: 3.0
		
		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.02, 4.0, 2.3, 50.0);
		
		Trajectory tra = this.generate(rightLeftScale, config);
		
		TankModifier modifier = new TankModifier(tra).modify(0.628);

	    Trajectory left = modifier.getLeftTrajectory();
	    
	    Trajectory right = modifier.getRightTrajectory();
	    
		rightFollower = new EncoderFollower(right);
		leftFollower = new EncoderFollower(left);
		rightFollower.configurePIDVA(0.9, 0, 0, 0.289, 0.06);
		leftFollower.configurePIDVA(0.9, 0, 0, 0.289, 0.06);
		
	}
	
	public Trajectory generate(Waypoint[] waypoints, Trajectory.Config config) {
		String hash = Utilities.hash(waypoints, config);
		File pathFile = new File("/home/lvuser/" + hash + ".traj");
		Trajectory traj;
		
		if(pathFile.exists()) {
			traj = Pathfinder.readFromFile(pathFile);
		} else {
			traj = Pathfinder.generate(waypoints, config);
			Pathfinder.writeToFile(pathFile, traj);
		}
		
		return traj;
	}
	
	public void run() {
		
		double heading = Math.toDegrees(rightFollower.getHeading());
			
		if(heading >180)
			heading = heading%180-180;
		double yaw = Drive.gyro.getYaw();
		
		double error = heading + yaw;
		if(error>180)
			error = error-360;
		else if(error < -180)
			error = error+360;
			
		double kG = -0.038;//0.8 * (-1.0/80.0);

		double turn = kG * error;
			
		System.out.printf("Heading: %.2f  Gyro: %.2f  Turn:  %.2f \n", heading,-yaw,turn); 
		double calc = (rightFollower.calculate(Drive.rightMain.getSelectedSensorPosition(0)));
		double calc2 = (leftFollower.calculate(Drive.leftMain.getSelectedSensorPosition(0)));
		// Driving forward
		if(!isLastPoint()){
			Drive.rightMain.set(ControlMode.PercentOutput, (calc - turn));
			Drive.leftMain.set(ControlMode.PercentOutput, (calc2 + turn));
		} else {
			Drive.runAt(0, 0);
		}
			
	}
	
	public boolean isLastPoint(){
		
		return (rightFollower.isFinished()&&leftFollower.isFinished());
		
	}

	public void startPath() {

		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~ START AUTO~~~~~~~~~~~~~");
		rightFollower.configureEncoder(Drive.rightMain.getSelectedSensorPosition(0), 1024, .102);
		leftFollower.configureEncoder(Drive.leftMain.getSelectedSensorPosition(0), 1024, .102);
		
	}

}
