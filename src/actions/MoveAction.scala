package actions

import java.awt.Point
import logic.Actor
import logic.Scene
import gui.AttributePanel

/**
 * Class MoveAction represents an action where selected actors are moved.
 */
class MoveAction(private val selectedActors: Vector[Actor], private val movement: Point) extends Action {
  ActionController.add(this)

  def execute() = {
    selectedActors.foreach { actor =>
      actor.moveBy(movement)
    }
    AttributePanel.updateFields()
  }

  def undo() = {
    selectedActors.foreach { actor =>
      actor.moveBy(new Point(-movement.x, -movement.y))
    }
    AttributePanel.updateFields()
  }
  
  override def toString = "Move Action: " + movement.getX + " " + movement.getY
}