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
import frc.robot.commands.ManualDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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

  double x;
  double y;

  public Drivetrain(int leftMtrID, int rightMtrID) {

    leftMtr = new CANSparkMax(leftMtrID, MotorType.kBrushless);
    rightMtr = new CANSparkMax(rightMtrID, MotorType.kBrushless);
    
    leftMtr.setInverted(true);
    rightMtr.setInverted(true);

    drive = new DifferentialDrive(leftMtr, rightMtr);
    //UNUSED = inst.getTable("SmartDashboard");

    // read values periodically
  }

  public void ManualDrive() {
    drive.arcadeDrive(RobotContainer.leftJoy.getY(), -RobotContainer.rightJoy.getX());
  }

  public void VisionPointer() {
    double x = tx.getDouble(0.0);
    double y = ty.getDouble(0.0);
    double area = ta.getDouble(0.0);

    System.out.println(x);

    SmartDashboard.putNumber("LimelightX", x);
    SmartDashboard.putNumber("LimelightY", y);
    SmartDashboard.putNumber("LimelightArea", area);

    if(x == -1 || y == -1) {
      System.out.print("not found");
      return;
    } else if (Math.abs(x-120) <= 8) {
      // drive.arcadeDrive(0, 0);
      System.out.print("Ball centered");
      return;
    }

    // System.out.print(x);
    // System.out.print(y);

    double desRotation = ((x - (30)) / (-30 - (30))) * (1 - -1) + (-1);
    // System.out.println("Desired roation: "+Double.toString(desRotation));
    drive.arcadeDrive(desRotation*.8, 0);
  }
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
