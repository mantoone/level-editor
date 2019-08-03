package gui.dialog

import scala.swing.BoxPanel
import scala.swing.Button
import scala.swing.FlowPanel
import scala.swing.Frame
import scala.swing.Label
import scala.swing.Orientation
import scala.swing.Reactor
import scala.swing.TextField
import scala.swing.event.ButtonClicked

import gui.LevelEditor
import logic.Grid

/**
 * The dialog that is used to choose the settings for the grid.
 */
object GridDialog extends Reactor {

  private val widthField = new TextField(8)
  private val heightField = new TextField(8)
  private val save = new Button("Save")
  private val cancel = new Button("Cancel")

  private val frame = new Frame {
    title = "Grid"
    contents = new BoxPanel(Orientation.Vertical) {
      contents += new FlowPanel {
        contents += new Label("Width: ")
        contents += widthField
      }
      contents += new FlowPanel {
        contents += new Label("Height: ")
        contents += heightField
      }

      contents += new FlowPanel(cancel, save)
    }
    resizable = false
  }

  this.listenTo(save, cancel)

  def show() = {
    widthField.text = Grid.size.width.toString
    heightField.text = Grid.size.height.toString
    this.frame.centerOnScreen()
    this.frame.visible = true
  }

  def hide() = {
    this.frame.visible = false
  }

  this.reactions += {
    case ButtonClicked(source) => {
      source.text match {
        case "Save" => {
          Grid.size.width = widthField.text.toInt
          Grid.size.height = heightField.text.toInt
          hide()
          LevelEditor.reDraw()
        }
        case _ => hide()
      }
    }
  }
}