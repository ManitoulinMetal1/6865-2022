// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
/** 
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

   public Robot() {
     LiveWindow.disableAllTelemetry();
   }
   private final Joystick bigJ = new Joystick(1);
   private final XboxController xBox = new XboxController(0);
   private final double deadZone = 0.05;

   private final Timer mTimer = new Timer();

   WPI_TalonSRX rightFront = new WPI_TalonSRX(1);
   WPI_TalonSRX rightFollower = new WPI_TalonSRX(2);
   WPI_TalonSRX intakeSRX = new WPI_TalonSRX(3);
   WPI_TalonSRX leftFront = new WPI_TalonSRX(4);
   WPI_TalonSRX leftFollower = new WPI_TalonSRX(5);
   
   WPI_TalonFX armFx = new WPI_TalonFX(0);
   
   double drivePower = 0.6;
   double intakePower = -1;
   double outtakePower = 1;
   //double armSpeed = 0.2;
   // double armTarget = 0;
   //double armPos = 0;
   //Constants for controlling the arm. consider tuning these for your particular robot
   
  //Constants in power for arm 
  final double armHoldUp = 0.02;
  final double armHoldDown = 0.02;
  final double armTravel = 0.3;

  //Constants for time for raising and lowering arm 
  final double armTimeUp = 0.2;
  final double armTimeDown = 0.2;

  //Varibles needed for the code
  boolean armUp = true; //Arm initialized to up because that's how it would start a match
  boolean burstMode = false;
  double lastBurstTime = 0;

  double autoStart = 0;
  boolean goForAuto = false;

  

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */

  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    CameraServer.startAutomaticCapture(0);
    CameraServer.startAutomaticCapture(1);

  /*  rightFront.configFactoryDefault();
    rightFollower.configFactoryDefault();
    leftFront.configFactoryDefault();
    leftFollower.configFactoryDefault();*/
    rightFollower.follow(rightFront);
    leftFollower.follow(leftFront);

    rightFront.setInverted(true); 
    leftFront.setInverted(false);

    rightFollower.setInverted(true);
    leftFollower.setInverted(false);

    //Everybot sample code
    armFx.setInverted(false);

//  armFx.setSelectedSensorPosition(0);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  
  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    //m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

    mTimer.reset();
    mTimer.start();

    
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
      if (mTimer.get() < 0.5)
      {
        rightFront.set(-0.2);
        leftFront.set(-0.2); 
      }
      else if (mTimer.get() < 2)
      {
        rightFront.set(0);
        leftFront.set(0);
        intakeSRX.set(1);
      }
      else if (mTimer.get() < 5)
      {
        rightFront.set(0.2);
        leftFront.set(0.2);
        intakeSRX.set(0);
      }
      else if (mTimer.get() < 12)
      {
        rightFront.set(0);
        rightFront.set(0);
      }
      else if (mTimer.get() < 12.4)
      {
        rightFront.set(0);
        rightFront.set(0);
        armFx.set(-0.1);
      }
      else
      {
        rightFront.set(0);
        leftFront.set(0);
        armFx.set(0);
      }
        break;
    }
  }
  //@Override
  //public void robotPeriodic() {}

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {

    armFx.setNeutralMode(NeutralMode.Brake);
  }
  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() 
  {
    if (Math.abs(bigJ.getY()) > deadZone || Math.abs(bigJ.getX()) > deadZone) 
    { 
      if (Math.abs(bigJ.getX()) > deadZone)
      {
        rightFront.set(drivePower*(bigJ.getY()-bigJ.getX()));
        leftFront.set(drivePower*(bigJ.getY()+bigJ.getX()));
      }
      else
      {
        rightFront.set(drivePower*bigJ.getY());
        leftFront.set(drivePower*bigJ.getY());
      }
    }
    else
    {
      rightFront.set(0);
      leftFront.set(0);
    }

    //Intake and scoring
    if (xBox.getYButton() == true) 
    {
      intakeSRX.set(intakePower);
//     armFx.set(-0.15);
    }
    else if(xBox.getBButton() == true) 
    {
      intakeSRX.set(outtakePower);
    }
    else
    {
      intakeSRX.set(0);
    }

    //Arm Controls
    if(armUp){
      if(Timer.getFPGATimestamp() - lastBurstTime < armTimeUp){
        armFx.set(armTravel);
      }
      else{
        armFx.set(armHoldUp);
      }
    }
    else{
      if(Timer.getFPGATimestamp() - lastBurstTime < armTimeDown){
        armFx.set(-armTravel);
      }
      else{
        armFx.set(-armHoldDown);
      }
    }
  
    if(xBox.getXButton() && !armUp){
      lastBurstTime = Timer.getFPGATimestamp();
      armUp = true;
    }
    else if(xBox.getAButton() && armUp){
      lastBurstTime = Timer.getFPGATimestamp();
      armUp = false;
    }  


  /*  if (xBox.getXButton() == true)
    {
      //60 degrees up?
      armTarget = 0;
    }
    else if(xBox.getAButton() == true)
    {
      //return down?
      armTarget = -34500;
    }
    else if(xBox.getStartButton() == true)
    {
      armSpeed = 0.1;
      armTarget = 100000;
    } 

    armPos = armFx.getSelectedSensorPosition();
    if(armPos < armTarget-2500)
    {
      armFx.set(armSpeed);
    }
    else if(armPos > armTarget+2500)
    {
      armFx.set(-armSpeed);
    }
    else if(armPos < armTarget-300)
    {
      armFx.set(0.1);
    }
    else if(armPos > armTarget+300)
    {
      armFx.set(-0.1);
    } 
    else
    {
      armFx.set(0);
    }
*/


//    System.out.println(armFx.getSelectedSensorPosition());

  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
    rightFront.set(0);
    leftFront.set(0);
    intakeSRX.set(0);
    armFx.set(0);
  }

  /** This function is called periodically when disabled. */
  @Override 
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}