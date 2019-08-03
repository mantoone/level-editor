package gui.dialog

import java.awt.Point

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

/**
 * The dialog that is used to choose settings for actor circle generation.
 */
object CircleDialog extends Reactor {

  private val radius = new TextField(8)
  private val segments = new TextField(8)
  private val generate = new Button("Generate")
  private val cancel = new Button("Cancel")

  private val frame = new Frame {
    title = "Make circle"
    contents = new BoxPanel(Orientation.Vertical) {
      contents += new FlowPanel {
        contents += new Label("Radius: ")
        contents += radius
      }
      contents += new FlowPanel {
        contents += new Label("Segments: ")
        contents += segments
      }

      contents += new FlowPanel(cancel, generate)
    }
    resizable = false
  }

  this.listenTo(generate, cancel)

  def show() = {
    radius.text = ""
    segments.text = ""
    this.frame.centerOnScreen()
    this.frame.visible = true
  }

  def hide() = {
    this.frame.visible = false
  }

  this.reactions += {
    case ButtonClicked(source) => {
      source.text match {
        case "Generate" => {
          val project = LevelEditor.project
          project.getSelectedActors.foreach { a =>
            val cx = a.position.x
            val cy = a.position.y
            val n = segments.text.toInt
            val r = radius.text.toInt
            val rad = (2d * Math.PI) / n
            val degrees = 360d / n
            val startRotation = a.rotation
            for (i <- 0 until n) {
              val duplicate = a.copy()
              val t = project.currentScene.map { _.duplicateActor(a) }.get
              val x = cx + Math.round(Math.cos(rad * i) * r).toInt
              val y = cy + Math.round(Math.sin(rad * i) * r).toInt
              val rotation = (-i * degrees + startRotation) % 360
              t.foreach { actor =>
                actor.setPosition(new Point(x, y))
                actor.setRotation(rotation)
              }
            }
          }
          hide()
          LevelEditor.reDraw()
        }
        case _ => hide()
      }
    }
  }
}