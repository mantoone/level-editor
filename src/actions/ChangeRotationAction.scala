package actions

import gui.AttributePanel
import logic.Actor

/**
 * Class ChangeRotationAction represents an action that changes the rotation of the selected actors.
 */
class ChangeRotationAction(private val selectedActors: Vector[Actor], newRotation: Double) extends Action {
  private val oldValues = selectedActors.map { actor => actor -> actor.rotation }.toMap
  selectedActors.foreach { _.rotation = newRotation }
  private val newValues = selectedActors.map { actor => actor -> actor.rotation }.toMap

  ActionController.add(this)

  override def redo() = {
    selectedActors.foreach { actor => actor.rotation = newValues(actor) }
    AttributePanel.updateFields()
  }

  def execute() = {}

  def undo() = {
    selectedActors.foreach { actor => actor.rotation = oldValues(actor) }
    AttributePanel.updateFields()
  }
}