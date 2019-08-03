package gui

import scala.swing.BoxPanel
import scala.swing.Button
import scala.swing.FlowPanel
import scala.swing.Label
import scala.swing.Orientation
import scala.swing.ScrollPane
import scala.swing.event.ButtonClicked
import LevelEditor.project
import scala.swing.GridPanel

/**
 * SceneSelectPanel shows all scenes in the project and can be used to open a scene.
 */
class SceneSelectPanel(scrollSpeed: Int) extends ScrollPane {
  this.verticalScrollBar.unitIncrement = scrollSpeed
  this.horizontalScrollBar.unitIncrement = scrollSpeed

  this.border = null
  
  private val gridPanel = new GridPanel(1, 1)
  private var rowPanel = new BoxPanel(Orientation.Horizontal)
  private val rowSize = 5

  def update() {
    gridPanel.contents.clear()
    gridPanel.columns = rowSize
    gridPanel.rows = 1
    
    var scenesAdded = 0
    if (project != null) {
      for (i <- project.scenes.indices) {
        val data = project.scenes(i)
        val icon = data._1
        val sceneName = data._2

        val b = new Button()
        b.name = (i + 1).toString()
        b.icon = icon
        this.listenTo(b)
        val panel = new BoxPanel(Orientation.Vertical) {
          contents += new FlowPanel(b)
          contents += new FlowPanel(new Label(sceneName))
        }

        gridPanel.contents += new FlowPanel(panel)
        
        if(scenesAdded >= rowSize && scenesAdded % rowSize == 0){
           gridPanel.rows += 1
        }
        scenesAdded += 1
        
      }
    }
  }

  this.contents = gridPanel

  this.reactions += {
    case e: ButtonClicked =>
      val button = e.source
      val scene = button.name
      LevelEditor.selectScene(scene)
  }

}