package actions

import java.awt.Dimension

import gui.AttributePanel
import logic.Actor

/**
 * The object ChangeSizeAction holds the two different types of ChangeSizeAction width and height.
 */
object ChangeSizeAction extends Enumeration {
  type Dimension = Value
  val Width, Height = Value
}

/**
 * Class ChangeSizeAction represents an action that changes width or height of the selected actors.
 */
class ChangeSizeAction(private val selectedActors: Vector[Actor], newValue: Int,
                       dimension: ChangeSizeAction.Dimension) extends Action {

  private val oldValues = selectedActors.map { actor => actor -> actor.size }.toMap
  if (dimension == ChangeSizeAction.Width) {
    selectedActors.foreach { _.setWidth(newValue) }
  } else {
    selectedActors.foreach { _.setHeight(newValue) }
  }

  private val newValues = selectedActors.map { actor => actor -> actor.size }.toMap

  ActionController.add(this)

  private def setValues(values: Map[Actor, Dimension]) {
    selectedActors.foreach { actor => actor.size = values(actor) }
  }

  override def redo() = {
    setValues(newValues)
    AttributePanel.updateFields()
  }

  def execute() = {}

  def undo() = {
    setValues(oldValues)
    AttributePanel.updateFields()
  }
}