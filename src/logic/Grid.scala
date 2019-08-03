package logic

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Point

import gui.LevelEditor

/**
 * Grid is used to constrain the movement of the actors. Grid is disabled by default.
 */
object Grid {
  private var enabled = false
  private val color = Color.LIGHT_GRAY
  val size = new Dimension(100, 100)

  /**
   * Toggle grid on and off
   */
  def toggle() = {
    this.enabled = !this.enabled
    this.enabled
  }
  
  /**
   * Draw the lines of the grid.
   */
  def draw(g: Graphics2D, sceneSize: Dimension) = {
    if (enabled) {
      val w = sceneSize.width
      val h = sceneSize.height
      val xStep = this.size.width
      val yStep = this.size.height

      g.setColor(this.color)

      // Draw vertical lines
      for (x <- 0 until w by xStep) {
        g.drawLine(x, 0, x, h)
      }

      // Draw horizontal lines
      for (y <- 0 until h by yStep) {
        g.drawLine(0, h - y, w, h - y)
      }
    }
  }

  /**
   * Return the position of the center of a grid cell that is closest to the position given as parameter.
   */
  def convertPosition(pos: Point) = {
    if (enabled) {
      val sh = LevelEditor.project.getSceneHeight
      val x = pos.x
      val y = pos.y
      val gw = this.size.getWidth
      val gh = this.size.getHeight

      new Point((Math.floor(x / gw) * gw + gw / 2).toInt,
        (sh - Math.floor((sh - y) / gh) * gh - gh / 2).toInt)
    } else {
      pos
    }
  }
}