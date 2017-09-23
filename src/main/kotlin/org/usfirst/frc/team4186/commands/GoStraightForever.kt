package org.usfirst.frc.team4186.commands

import edu.wpi.first.wpilibj.command.Command
import org.usfirst.frc.team4186.drive.TankDrive

class GoStraightForever(
    private val drive: TankDrive,
    private val output: Double = 0.0
) : Command("GoStraightForever $output") {

  override fun execute() {
    drive.drive(output, output)
  }

  override fun end() {
    drive.stopMotor()
  }

  override fun isFinished(): Boolean = false
}