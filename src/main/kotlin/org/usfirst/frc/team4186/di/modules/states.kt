package org.usfirst.frc.team4186.di.modules

import com.github.salomonbrys.kodein.*
import org.usfirst.frc.team4186.extensions.DSState
import org.usfirst.frc.team4186.routines.Autonomous
import org.usfirst.frc.team4186.robot.State
import org.usfirst.frc.team4186.routines.Teleop


val states_module = Kodein.Module {
  bind<State>(DSState.kStateAutonomous) with provider {
    Autonomous(
        scheduler = instance(),
        turn = factory(Commands.TURN),
        move = factory(Commands.MOVE)
    )
  }

  bind<State>(DSState.kStateTeleop) with provider {
    Teleop(
        drive = instance(),
        climber = instance(Hardware.Motor.CLIMBER),
        joystickAdapter = instance(),
        sonar = instance(Hardware.Sonar.FRONT),
        ahrs = instance(),
        startCamera = factory()
    )
  }

  bind<State>(DSState.kStateTest) with provider { instance<State>() }
  bind<State>(DSState.kStateDisabled) with provider { instance<State>() }
  bind<State>(DSState.kStateEnabled) with provider { instance<State>() }

  bind<State>() with singleton {
    object : State {
      override fun init() {}
      override fun tick() {}
      override fun cleanUp() {}
    }
  }
}