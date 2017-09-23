package org.usfirst.frc.team4186.di.modules

import com.ctre.MotorControl.CANTalon
import com.ctre.MotorControl.SmartMotorController
import com.github.salomonbrys.kodein.*
import com.kauailabs.navx.frc.AHRS
import edu.wpi.cscore.UsbCamera
import edu.wpi.first.wpilibj.*


object Hardware {
  object Motor {
    const val LEFT = "motor-left"
    const val RIGHT = "motor-right"

    const val CLIMBER = "motor-climber"

    object Slave {
      const val LEFT = "motor-slave-left"
      const val RIGHT = "motor-slave-right"
      const val CLIMBER = "motor-slave-climber"
    }
  }

  object Sonar {
    const val FRONT_LONG = "sonar-front-long"
    const val FRONT = "sonar-front"
    const val LEFT = "sonar-left"
    const val RIGHT = "sonar-right"
  }

  const val TIMESTAMP = "timestamp"
}

object CANMap {
  object Motor {
    const val LEFT = 1
    const val RIGHT = 9
    const val CLIMBER = 6

    object Slave {
      const val LEFT = 2
      const val RIGHT = 8
      const val CLIMBER = 7
    }
  }
}

object PWMMap {
  object Sonar {
    const val FRONT_PORT_A = 0
    const val FRONT_PORT_B = 1

    const val LEFT_PORT_A = 2
    const val LEFT_PORT_B = 3

    const val RIGHT_PORT_A = 4
    const val RIGHT_PORT_B = 5
  }
}

object AIOMap {
  const val FRONT_SONAR = 0
}

object UsbMap {
  object Camera {
    const val Front = 0
    const val Back = 1
  }
}


val hardware_module = Kodein.Module {
  bind<UsbCamera>() with factory { device: Int -> CameraServer.getInstance().startAutomaticCapture(device) }

  bind<AHRS>() with instance(AHRS(SPI.Port.kMXP))

  bind<PIDSource>(Hardware.Sonar.FRONT_LONG) with singleton { AnalogInput(AIOMap.FRONT_SONAR) }

  bind<PIDSource>(Hardware.Sonar.FRONT) with singleton { Ultrasonic(PWMMap.Sonar.FRONT_PORT_A, PWMMap.Sonar.FRONT_PORT_B) }
  bind<PIDSource>(Hardware.Sonar.LEFT) with singleton { Ultrasonic(PWMMap.Sonar.LEFT_PORT_A, PWMMap.Sonar.LEFT_PORT_B) }
  bind<PIDSource>(Hardware.Sonar.RIGHT) with singleton { Ultrasonic(PWMMap.Sonar.RIGHT_PORT_A, PWMMap.Sonar.RIGHT_PORT_B) }

  bind<SpeedController>(tag = Hardware.Motor.CLIMBER) with singleton {
    instance<SpeedController>(Hardware.Motor.Slave.CLIMBER) // NOTE initialize slave motor
    CANTalon(CANMap.Motor.CLIMBER)
  }
  bind<SpeedController>(tag = Hardware.Motor.LEFT) with singleton {
    instance<SpeedController>(Hardware.Motor.Slave.LEFT) // NOTE initialize slave motor
    CANTalon(CANMap.Motor.LEFT)
  }
  bind<SpeedController>(tag = Hardware.Motor.RIGHT) with singleton {
    instance<SpeedController>(Hardware.Motor.Slave.RIGHT) // NOTE initialize slave motor
    CANTalon(CANMap.Motor.RIGHT)
  }

  // NOTE slave instance holders!!
  bind<SpeedController>(tag = Hardware.Motor.Slave.CLIMBER) with singleton { slaveCANTalon(CANMap.Motor.CLIMBER, CANMap.Motor.Slave.CLIMBER) }
  bind<SpeedController>(tag = Hardware.Motor.Slave.LEFT) with instance(slaveCANTalon(CANMap.Motor.LEFT, CANMap.Motor.Slave.LEFT))
  bind<SpeedController>(tag = Hardware.Motor.Slave.RIGHT) with instance(slaveCANTalon(CANMap.Motor.RIGHT, CANMap.Motor.Slave.RIGHT))
}

private fun slaveCANTalon(masterId: Int, slaveId: Int) = CANTalon(slaveId).apply {
  changeControlMode(SmartMotorController.TalonControlMode.Follower)
  set(masterId.toDouble())
}

