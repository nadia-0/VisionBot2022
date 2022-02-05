// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotContainer;
import frc.robot.commands.TeleopDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.Constants;

public class Drivetrain extends SubsystemBase {
  /* Creates a new Drivetrain. 
   * @param rightMtrID
   * @param leftMtrID
   */

  CANSparkMax leftMtr;
  CANSparkMax rightMtr;

  DifferentialDrive drive;

  NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
  NetworkTableEntry tx = table.getEntry("tx");
  NetworkTableEntry ty = table.getEntry("ty");
  NetworkTableEntry ta = table.getEntry("ta");

  public Drivetrain() {

    leftMtr = new CANSparkMax(Constants.leftMtrID, MotorType.kBrushless);
    rightMtr = new CANSparkMax(Constants.rightMtrID, MotorType.kBrushless);
    
    leftMtr.setInverted(true);
    rightMtr.setInverted(true);

    drive = new DifferentialDrive(leftMtr, rightMtr);
    //UNUSED = inst.getTable("SmartDashboard");

    // read values periodically
  }

  public void ManualDrive(double turnSpeed, double forwardSpeed) {
    drive.arcadeDrive(turnSpeed, -forwardSpeed);
  }

  public void MotorOverride(double leftSpeed, double rightSpeed) {
    leftMtr.set(leftSpeed);
    rightMtr.set(-rightSpeed);
  }

  private double areaFinder(double area) {
    return (80*area)+(20*(area*area)); //THIS NUMBER IS COMPLETELY MADE UP IT DOESN'T MEAN ANYTHING (yet)
  }

  private double angleFinder(double y) {
    double nativeAngle = 20; // Mounted angle of the limelight
    double correction = -3; // Constant error
    double h1 = 9; // Limelight height
    double h2 = 61.5; // Target height
    return (h2-h1) / Math.tan(Math.toRadians(nativeAngle+correction+y));
  }

  public void VisionPointer() {
    double x = tx.getDouble(0.0);
    double y = ty.getDouble(0.0);
    double area = ta.getDouble(0.0);

    SmartDashboard.putNumber("LimelightX", x);
    SmartDashboard.putNumber("LimelightY", y);
    SmartDashboard.putNumber("LimelightArea", area);

    double angleDist = angleFinder(y);
    double areaDist = areaFinder(area);
    double dist = angleDist;
    //double dist = (angleDist+areaDist)/2; //FIND AVERAGE BETWEEN TWO METHODS, MAY NEED LATER ADJUSTMENT
    SmartDashboard.putNumber("Distance to Target (ft)", dist/12);
    double maxAngle = 30;
    double desRotation = ((x - (maxAngle)) / (-maxAngle - (maxAngle))) * (1 - -1) + (-1);

    drive.arcadeDrive(desRotation*.8, 0);
  }
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}