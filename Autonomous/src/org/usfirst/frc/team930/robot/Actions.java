package org.usfirst.frc.team930.robot;

public class Actions {
	
	private int path = 1;
	private int count = 1;
	
	public boolean act1() {
		boolean isFinished = false;
		switch (this.path) {
		case 1:
			/*for(int i = 1; i <= 5; i++) {
				System.out.println("Running Segment 1 Case 1");
				if(i == 5)
					this.path = 2;
			}*/
			System.out.println("Running Action 1 Case 1");

			Robot.leftMain.set(0);
			Robot.rightMain.set(0);
			
			if(this.count == 100) {
				this.count = 1;
				this.path = 2;
			}
			
			this.count++;
			
			break;
		case 2:
			/*System.out.println("\n");
			for(int i = 6; i <= 10; i++) {
				System.out.println("Running Segment 1 Case 2");
				if(i == 10) {
					this.path = 1;
					isFinished = true;
				}
			}*/
			System.out.println("Running Action 1 Case 2");

			Robot.leftMain.set(0);
			Robot.rightMain.set(0);
			
			if(this.count == 100) {
				this.count = 1;
				this.path = 1;
				isFinished = true;
			}
			
			this.count++;
			
			break;
		}
		return isFinished;
	}

}
