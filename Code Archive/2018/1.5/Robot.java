
package org.usfirst.frc.team3673.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;


public class Robot extends IterativeRobot {
	
	// Define joysticks
	public Joystick red = new Joystick(1);
	public Joystick black = new Joystick(0);
	
	//Int for time around in auto
	int autoStep = 1;
	
	// Assign a port to the navX gyro
	public AHRS ahrs = new AHRS(SPI.Port.kMXP); 
	
	// Define a new Compressor
	public Compressor compress = new Compressor(0);
	
	// Define Solenoid
	public DoubleSolenoid solenoid = new DoubleSolenoid(0, 1);
	
	// Assign a port to the encoder
	public Encoder encode = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
	
	// Assign PWM ports to the spark motor controllers
	public Spark rightFront = new Spark(3);
	public Spark rightBack = new Spark(1); 
	public Spark leftFront = new Spark(4);
	public Spark leftBack = new Spark(2); 
	public Spark highGuy = new Spark(5);
	
	// Add front and rear motors to the right side group
	public SpeedControllerGroup right = new SpeedControllerGroup(rightFront, rightBack);
	
	// Add front and rear motors to the left side group
	public SpeedControllerGroup left = new SpeedControllerGroup(leftFront, leftBack);
	
	// Define groups to be used for driving
	public DifferentialDrive drive = new DifferentialDrive(left, right);
	
	//Double leftSpeed to prevent junk values
	double leftSpeed;
	//Double rightSpeed to prevent junk values
	double rightSpeed;
	
	@Override
	public void robotInit() {
		
		compress.setClosedLoopControl(true);
	
	}

	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	
	@Override
	public void autonomousInit() {
		//Reset rotation count
		encode.reset();
		// Disable motor safety
		drive.setSafetyEnabled(true);
		//Set wheel size
		double wheelDiameter = 5.98;
		// Set distance per pulse based on wheel circumference (in inches)
		encode.setDistancePerPulse((wheelDiameter*3.14159265358979323846264)/8192);
		//Double leftSpeed to prevent junk values
		leftSpeed = 0.0;
		//Double rightSpeed to prevent junk values
		rightSpeed = 0.0;
	}

	
	@Override
	public void autonomousPeriodic() {
		
		// Timer bit comment later
		Timer timer = new Timer();
		// Get distance since last pulse
		double latestDistance = encode.getDistance();
		// Get number of rotations since last reset
		int count = encode.get();
		// Get distance per pulse
		double distancePerRotation = encode.getDistancePerPulse();
		// Use this formula to get a predicted distance for the next pulse
		double distance = count*distancePerRotation+latestDistance;
		// Get game information in the format of R/L (your switch) R/L (the scale) R/L (their switch)
		String gameData = DriverStation.getInstance().getGameSpecificMessage();
		
		// If the robot is at Station 1
		if(DriverStation.getInstance().getLocation() == 1){
			
			// If enemy alliance's switch is on our side
			if(gameData.charAt(2) == 'L'){
				//1, **L
				System.out.println("1, **L");
				

					
				if(distance < 386.6 && autoStep == 1){
					
					// Print distance
					System.out.println(distance);
					
					// Move forward
					leftSpeed = -0.5;
					rightSpeed = -0.5;
					
				}
				
				else{
					
					// Keep robot from moving forward
					rightSpeed = 0.0;
					leftSpeed = 0.0;
					
					// If robot has not yet turned 90 degrees 
					if(ahrs.getAngleAdjustment() < 90){
						
						// Turn right
						leftSpeed = -0.5;
						rightSpeed = 0.5;
						
						// Set auto step to 2
						autoStep = 2;
						
						// Reset encoder count and therefore distance 
						encode.reset();
						
					}
					else {
						
						// Keep robot from turning right 
						rightSpeed = 0.0;
						leftSpeed = 0.0;
						
						if (distance < 53 && autoStep == 2) {
							
							// Move forward
							leftSpeed = -0.5;
							rightSpeed = -0.5;
							
						}
						else{
							
							// Keep robot from moving forward
							rightSpeed = 0.0;
							leftSpeed = 0.0;
							
							// Reset displacement of the NavX
							ahrs.resetDisplacement();
							
							if (ahrs.getAngleAdjustment() < 90){
								
								// Turn left
								leftSpeed = 0.5;
								rightSpeed = -0.5;
								
							}
							else {
								
								// Keep robot from turning left
								rightSpeed = 0.0;
								leftSpeed = 0.0;
								
								// Reset encoder count and therefore distance
								encode.reset();
								
								if (distance < 32.8){
									
									// Move forward
									leftSpeed = -0.5;
									rightSpeed = -0.5;
									
									//reset time
									timer.reset();
								}
								else{
									
									// Keep robot from moving forward
									rightSpeed = 0.0;
									leftSpeed = 0.0;
									
									
									// DO THE GRABBY THING
									if(timer.get() < 10){
										solenoid.set(DoubleSolenoid.Value.kReverse);
									}
									else{
										solenoid.set(DoubleSolenoid.Value.kForward);
									}
								}
							}
						}	
					}	
				}
			} 
			
			else{
				//1, **R
				// Else, their switch is on the right
				System.out.println("1, **R");
				
				// If our switch is on the left
				if(gameData.charAt(0) == 'L'){
					
					//1, L*R
					System.out.println("1, L*R");
					
					if (distance < 168){
						
						// Move forward
						leftSpeed = -0.5;
						rightSpeed = -0.5;
						
						// Reset displacement of the NavX
						ahrs.resetDisplacement();
						
					}
					else{
						
						// Keep robot from moving forward
						rightSpeed = 0.0;
						leftSpeed = 0.0;
						
						if(ahrs.getAngleAdjustment() < 90){
							
							// Turn right
							leftSpeed = -0.5;
							rightSpeed = 0.5;
							
							// Reset encoder count and therefore distance
							encode.reset();
							
						}
						else{
							
							// Keep robot from turning right 
							rightSpeed = 0.0;
							leftSpeed = 0.0;
							
							if(distance < 38.3){
								
								// Move forward
								leftSpeed = -0.5;
								rightSpeed = -0.5;
								
							}
							else{
								
								// Keep robot from moving forward
								rightSpeed = 0.0;
								leftSpeed = 0.0;
								
								// DO THE GRABBY THING
								
							}
							
						}
						
					}
					
					
					
				} 
				
				else 
				{
					System.out.println("1, R*R");
					//1, R*R
					
					//If distance < var
					if (distance < 2) {
						
						// Move forward
						leftSpeed = -0.5;
						rightSpeed = -0.5;
					
					}
					else {
						
						// Keep robot from moving forward
						leftSpeed = 0.0;
						rightSpeed = 0.0;
						
						if(ahrs.getAngleAdjustment() < 90){
							
							// Turn right
							leftSpeed = -0.5;
							rightSpeed = 0.5;
							
							
							
						}
						else {
							
							// Keep robot from turning right 
							rightSpeed = 0.0;
							leftSpeed = 0.0;
							
							// Reset encoder count and therefore distance
							encode.reset();
							
						}
					}
				}
			}
		}				
		
		// If the robot is at Station 2
		else if(DriverStation.getInstance().getLocation() == 2){
			if(gameData.charAt(2) == 'L')
			{
				//2, **L
				System.out.println("2, **L");
			} 
			else 
			{
				//2, **R
				System.out.println("2, **R");

			}			
		}
		
		// If the robot is at Station 3
		else{
			if(gameData.charAt(2) == 'R')
			{
				System.out.println("3, **R");

				//3, **R
				
				if(distance < 386.6){
					
					System.out.println(distance);
					
					leftSpeed = -0.5;
					rightSpeed = -0.5;
				}
				
				else{
					rightSpeed = 0.0;
					leftSpeed = 0.0;
					
				}
				
			} 
			else 
			{
				System.out.println("3, **L");

				//3, **L
				if(gameData.charAt(0) == 'R')
				{
					//3, R*R
					System.out.println("3, R*R");

				} 
				else 
				{
					//3, R*L
					System.out.println("3, R*L");

				}	
			}			
		}
		
		// Set drive parameters
		drive.tankDrive(leftSpeed, rightSpeed);
	}
	

	@Override
	public void teleopInit() {
	
	 
	}
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		
	
			if(red.getRawButton(5)){
				highGuy.set(1.0);
			}
			else if (red.getRawButton(3)){
				highGuy.set(-1.0);
			}
			else{
				highGuy.set(0.0);
			}
		
		
		// If red trigger is pressed
		if(red.getTrigger()){
			
			 // Turn on pneumatics
			solenoid.set(DoubleSolenoid.Value.kForward);
			
		}
		else if(black.getTrigger()){
			
			// Reverse pneumatics
			solenoid.set(DoubleSolenoid.Value.kReverse);
		}
		else{
			
			// Turn off pneumatics
			solenoid.set(DoubleSolenoid.Value.kOff);
			
		}

		// Define drive type as tank drive (two joysticks)
		 drive.tankDrive(red.getY(), black.getY());
		
    }		

	
	@Override
	public void testPeriodic() {
	}
}
