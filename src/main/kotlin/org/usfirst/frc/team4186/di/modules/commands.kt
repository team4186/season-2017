package org.usfirst.frc.team4186.di.modules

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.factory
import com.github.salomonbrys.kodein.instance
import com.kauailabs.navx.frc.AHRS
import edu.wpi.first.wpilibj.SpeedController
import edu.wpi.first.wpilibj.command.Command
import org.usfirst.frc.team4186.commands.GoStraightForever
import org.usfirst.frc.team4186.commands.Move
import org.usfirst.frc.team4186.commands.PowerMotor
import org.usfirst.frc.team4186.commands.Turn

object Commands {
  const val POWER_MOTOR = "command-power-motor"

  const val RUN = "command-run"
  const val MOVE = "command-move"
  const val TURN = "command-turn"

}

fun power(motor: SpeedController, power: Double) = motor to power

val commands_module = Kodein.Module {

  bind<Command>(tag = Commands.POWER_MOTOR) with factory { args: Pair<SpeedController, Double> ->
    val (motor, power) = args
    PowerMotor(
        motor = motor,
        output = power
    )
  }

  bind<Command>(tag = Commands.RUN) with factory { power: Double ->
    GoStraightForever(
        drive = instance(),
        output = power
    )
  }

  bind<Command>(tag = Commands.MOVE) with factory { distance: Double ->
    Move(
        distanceEstimator = instance(Hardware.Sonar.FRONT),
        drive = instance(),
        distance = distance
    )
  }

  bind<Command>(tag = Commands.TURN) with factory { angle: Double ->
    Turn(
        ahrs = instance<AHRS>(),
        drive = instance(),
        angle = angle
    )
  }
}