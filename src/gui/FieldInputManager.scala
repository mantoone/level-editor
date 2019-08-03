package gui

import scala.swing.Reactor
import scala.swing.TextField
import scala.swing.event.EditDone

import AttributePanel.hField
import AttributePanel.rField
import AttributePanel.wField
import AttributePanel.xField
import AttributePanel.yField
import LevelEditor.project
import actions.ChangePositionAction
import actions.ChangeRotationAction
import actions.ChangeSizeAction

/**
 * FieldInputManager handles the events of textfields in AttributePanel.
 */
object FieldInputManager extends Reactor {
  this.reactions += {
    case EditDone(source) => {
      this.fieldUpdated(source.asInstanceOf[TextField])
    }
  }

  /**
   * Creates an action that updates the attributes of selected actors based on what textfield was edited.
   */
  private def fieldUpdated(field: TextField) = {
    val scene = project.currentScene.get
    val selectedActors = project.getSelectedActors
    var update = true
    try {
      field.name match {

        case "x" => {

          val newValue = xField.text.toInt
          if (selectedActors.exists { _.position.x != newValue })
            new ChangePositionAction(selectedActors, newValue, ChangePositionAction.X)
        }
        case "y" => {
          val newValue = LevelEditor.convertY(yField.text.toInt)
          if (selectedActors.exists { _.position.y != newValue })
            new ChangePositionAction(selectedActors, newValue, ChangePositionAction.Y)
        }
        case "w" => {
          val newValue = wField.text.toInt
          if (selectedActors.exists { _.size.width != newValue })
            new ChangeSizeAction(selectedActors, newValue, ChangeSizeAction.Width)
        }
        case "h" => {
          val newValue = hField.text.toInt
          if (selectedActors.exists { _.size.height != newValue })
            new ChangeSizeAction(selectedActors, newValue, ChangeSizeAction.Height)
        }
        case "r" => {
          val newValue = rField.text.toDouble
          if (selectedActors.exists { _.rotation != newValue })
            new ChangeRotationAction(selectedActors, newValue)
        }
        case _ => update = false
      }
    } catch {
      case e: NumberFormatException => println("Not a number!")
    }
    if (update) LevelEditor.reDraw()
  }
}