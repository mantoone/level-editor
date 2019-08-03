package gui

import java.awt.Dimension

import scala.swing.BoxPanel
import scala.swing.Component
import scala.swing.FlowPanel
import scala.swing.Label
import scala.swing.Orientation
import scala.swing.TextField

/**
 * AttributePanel is a panel that contains the textfields that are used to change the attributes
 * of selected actors.
 */
object AttributePanel extends BoxPanel(Orientation.Vertical) {
  private def createField(n: String) = {
    val field = new TextField(8) {
      name = n
    }
    FieldInputManager.listenTo(field)
    field
  }

  val xField = createField("x")
  val yField = createField("y")
  val wField = createField("w")
  val hField = createField("h")
  val rField = createField("r")

  /**
   * Creates a FlowPanel containing a Label if the parameter label is not an empty string and the Component given as parameter.
   */
  private def createPanel(label: String, component: Component, h: Int = 35) = {
    new FlowPanel {
      if (!label.isEmpty()) {
        contents += new Label(label)
      }
      contents += component
      minimumSize = new Dimension(150, h)
      preferredSize = new Dimension(150, h)
      maximumSize = new Dimension(150, h)
    }
  }

  contents += createPanel("", new Label("Position"), 20)
  contents += createPanel("X: ", xField)
  contents += createPanel("Y: ", yField)
  contents += createPanel("", new Label("Size"), 20)
  contents += createPanel("W: ", wField)
  contents += createPanel("H: ", hField)
  contents += createPanel("Rotation: ", rField, 55)

  /**
   * Updates all textfields to have the values of the first selected actor.
   */
  def updateFields() = {
    val selectedActors = LevelEditor.project.getSelectedActors
    if (selectedActors.size > 0) {
      val actor = selectedActors(0)
      val x = actor.position.x
      val y = actor.position.y
      val w = actor.size.width
      val h = actor.size.height
      val r = actor.rotation

      xField.text = x.toString
      yField.text = LevelEditor.convertY(y).toString
      wField.text = w.toString
      hField.text = h.toString
      rField.text = r.toString.take(10)
    }
  }
}