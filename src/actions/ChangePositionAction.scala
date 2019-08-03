package actions

import java.awt.Point

import gui.AttributePanel
import logic.Actor

/**
 * The object ChangePositionAction holds the two different types of ChangePositionAction X and Y.
 */
object ChangePositionAction extends Enumeration {
  type Coordinate = Value
  val X, Y = Value
}

/**
 * Class ChangePositionAction represents an action that changes x or y values of the selected actors.
 */
class ChangePositionAction(private val selectedActors: Vector[Actor], newValue: Int,
                           coordinate: ChangePositionAction.Coordinate) extends Action {

  private val oldValues = selectedActors.map { actor => actor -> actor.position }.toMap
  if (coordinate == ChangePositionAction.X) {
    selectedActors.foreach { _.setX(newValue) }
  } else {
    selectedActors.foreach { _.setY(newValue) }
  }
  private val newValues = selectedActors.map { actor => actor -> actor.position }.toMap

  ActionController.add(this)

  private def setValues(values: Map[Actor, Point]) = {
    selectedActors.foreach { actor => actor.setPosition(values(actor)) }
  }

  def execute() = {}

  def undo() = {
    setValues(oldValues)
    AttributePanel.updateFields()
  }

  override def redo() = {
    setValues(newValues)
    AttributePanel.updateFields()
  }
}