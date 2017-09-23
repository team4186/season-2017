package org.usfirst.frc.team4186.routines

import com.kauailabs.navx.frc.AHRS
import edu.wpi.cscore.UsbCamera
import edu.wpi.first.wpilibj.PIDSource
import edu.wpi.first.wpilibj.SpeedController
import edu.wpi.first.wpilibj.command.Command
import edu.wpi.first.wpilibj.command.Scheduler
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.usfirst.frc.team4186.di.modules.UsbMap
import org.usfirst.frc.team4186.drive.TwinMotorArcadeDrive
import org.usfirst.frc.team4186.hardware.*
import org.usfirst.frc.team4186.robot.State

class Teleop(
    private val drive: TwinMotorArcadeDrive,
    private val climber: SpeedController,
    private val joystickAdapter: SaitekX52Adapter,
    private val sonar: PIDSource,
    private val ahrs: AHRS,
    private val startCamera: (cameraId: Int) -> UsbCamera
) : State {

  private val joystickState by lazy {
    val mode = joystickAdapter.mode
    val axis = DoubleArray(SaitekX52Axis.AXIS_COUNT) { joystickAdapter.axis(it) }
    val pov = IntArray(SaitekX52Pov.POV_COUNT) { joystickAdapter.pov(it) }
    val buttons = ButtonData(joystickAdapter.buttons.state, joystickAdapter.buttons.count)
    JoystickState(mode, axis, pov, buttons)
  }

  private lateinit var frontCamera: UsbCamera
  private lateinit var backCamera: UsbCamera

  override fun init() {
    println("Teleop Init")
    drive.report()
    joystickAdapter.report()

    frontCamera = startCamera(UsbMap.Camera.Front)
    backCamera = startCamera(UsbMap.Camera.Back)
  }

  override fun tick() {
    with(joystickState.updated) {
      climber.set(climbing)
      drive.drive(throttle, yaw)
    }

    SmartDashboard.putNumber("sonar", sonar.pidGet())
    SmartDashboard.putNumber("heading", ahrs.pidGet())
  }

  override fun cleanUp() {
    println("Teleop clean up")
    drive.stopMotor()
  }

  inline private val JoystickState.updated get() = apply { joystickAdapter.update(this) }
  inline private val JoystickState.yaw get() = axis[SaitekX52Axis.X]
  inline private val JoystickState.throttle get() = axis[SaitekX52Axis.THROTTLE]
  inline private val JoystickState.climbing
    get() = when {
      buttons[SaitekX52Buttons.THROTTLE_HAT_UP] -> 1.0
      buttons[SaitekX52Buttons.THROTTLE_HAT_DOWN] -> -1.0
      else -> 0.0
    }
}
