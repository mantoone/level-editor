package gui

import scala.swing.Panel
import scala.swing.BoxPanel
import scala.swing.Orientation
import scala.swing.Label
import scala.swing.Button
import logic.Layer
import java.awt.Dimension
import scala.swing.FlowPanel
import scala.swing.Component
import scala.swing.event.ButtonClicked

object LayerButtonListener extends Component {
  def getLayer(layerId: Int): Layer = {
    LevelEditor.project.currentScene.get.layers(layerId)
  }

  this.reactions += {
    case e: ButtonClicked =>
      val button = e.source.asInstanceOf[LayerButton]
      if (button.char == 'H') {
        val layer = this.getLayer(button.layerId)
        layer.deselectSelected()

        layer.locked = !layer.locked
        layer.visible = !layer.visible

        LevelEditor.reDraw()
      } else if (button.char == 'L') {
        val layer = this.getLayer(button.layerId)
        if (layer.visible) {
          layer.deselectSelected()
          layer.locked = !layer.locked
        }

        LevelEditor.reDraw()
      }
  }
}

class LayerButton(val char: Char, val layerId: Int) extends Button(char.toString) {
  this.preferredSize = new Dimension(15, 15)
  LayerButtonListener.listenTo(this)
}

class LayerPanel() extends BoxPanel(Orientation.Vertical) {

  private def createButton(char: Char, layerId: Int) = new LayerButton(char, layerId)

  def createLayer(label: String, layerId: Int) = {
    new FlowPanel {
      this.xLayoutAlignment = 0.0
      contents += createButton('H', layerId)
      contents += createButton('L', layerId)
      contents += new BoxPanel(Orientation.Vertical) {
        contents += new Label(label)
      }
      this.maximumSize = new Dimension(this.preferredSize.width, 30)
    }
  }

  def updateLayers(layers: Vector[Layer]): Unit = {
    this.contents.clear()

    for (layerId <- layers.indices) {
      this.contents += createLayer(layers(layerId).name, layerId)
    }
  }
}