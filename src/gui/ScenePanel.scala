package gui

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Point
import java.awt.RenderingHints
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener

import scala.swing.Component
import scala.swing.Panel

import LevelEditor.project
import logic.Grid

/**
 * ScenePanel is the panel that shows the scene.
 */
class ScenePanel() extends Panel {
  private var zoom = 1.0d
  private var width = this.size.width
  private var height = this.size.height

  this.background = new Color(0.87f, 0.87f, 0.87f)

  private val cameraPosition = new Point(0, 0)

  InputManager.listenTo(this.mouse.clicks, this.mouse.moves, this.keys)

  focusable = true
  requestFocus()

  def getCameraPosition(): Point = cameraPosition

  def moveCamera(delta: Point): Unit = {
    this.cameraPosition.translate(delta.x, delta.y)
  }

  def getZoom(): Double = zoom

  def updateSceneSize(sceneSize: Dimension) = {
    this.width = sceneSize.width
    this.height = sceneSize.height
    this.cameraPosition.x = 0
    this.cameraPosition.y = 0
    this.zoom = 1.0d
  }

  // This method draws the selection rectangle that is used to select actors.
  def drawSelectionRectangle(g: Graphics2D) = {
    g.setColor(Color.ORANGE)
    if (InputManager.mouseOutside) {
      val r = InputManager.getSelectionRectangle()
      g.drawRect(r._1, r._2, r._3 - r._1, r._4 - r._2)
    }
  }

  def rescale(factor: Double) = {
    val oldZoom = this.zoom
    this.zoom *= factor

    this.cameraPosition.x += ((1d - factor) / 2 * this.size.width / this.zoom).toInt
    this.cameraPosition.y += ((1d - factor) / 2 * this.size.height / this.zoom).toInt

    this.repaint()
  }

  // Rendering Hints are set to make transformed images look smoother
  var rh = new RenderingHints(
    RenderingHints.KEY_INTERPOLATION,
    RenderingHints.VALUE_INTERPOLATION_BILINEAR)

  override def paintComponent(g: Graphics2D) = {
    super.paintComponent(g)
    g.setRenderingHints(rh)
    g.scale(this.zoom, this.zoom)
    g.translate(cameraPosition.x, cameraPosition.y)
    project.draw(g)

    Grid.draw(g, project.getSceneSize)

    drawSelectionRectangle(g)

    g.dispose()
  }
}
