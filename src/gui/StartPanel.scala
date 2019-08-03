package gui

import scala.swing.Button
import scala.swing.Label
import scala.swing.Panel
import javax.swing.ImageIcon
import scala.swing.BoxPanel
import scala.swing.Orientation
import scala.swing.event.ButtonClicked
import java.net.URI
import java.awt.Desktop
import javax.imageio.ImageIO

/**
 * ScenePanel is the panel that shows the scene.
 */
class StartPanel() extends BoxPanel(Orientation.Vertical) {
  val res = getClass.getResourceAsStream("/start.png")

  val icon = new ImageIcon(ImageIO.read(res))
  val thumb = new Label()
  thumb.icon = icon

  val websiteButton = new Button("Open website")
  val button = new Button("Open project")

  this.listenTo(websiteButton, button)
  this.reactions += {
    case ButtonClicked(`button`) => {
      LevelEditor.loadProject()
    }
    case ButtonClicked(`websiteButton`) => {
      Desktop.getDesktop().browse(new URI("http://mantuapps.com/gscse/full/"))
    }
  }

  this.contents += thumb
  this.contents += websiteButton
  this.contents += button
}
