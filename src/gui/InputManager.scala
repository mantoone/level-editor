package gui

import java.awt.Point

import scala.swing.Reactor
import scala.swing.event.Key
import scala.swing.event.KeyPressed
import scala.swing.event.KeyReleased
import scala.swing.event.MouseDragged
import scala.swing.event.MousePressed
import scala.swing.event.MouseReleased

import LevelEditor.project
import LevelEditor.reDraw
import actions.DeleteAction
import actions.DuplicateAction
import actions.MoveAction
import logic.Actor

/**
 * InputManager handles all mouse and keyboard events that are sent to the ScenePanel.
 */
object InputManager extends Reactor {

  /**
   * Create a point that is the transition from point a to point b.
   */
  private def deltaPoint(a: Point, b: Point) = new Point(b.x - a.x, b.y - a.y)

  /**
   * Moves all selected actors by the point given as parameter.
   */
  private def arrowKey(point: Point) = {
    val a = if (shiftPressed) 10 else 1
    val movement = new Point(point.x * a, point.y * a)
    project.modifySelected(_.moveBy(movement))
    new MoveAction(project.getSelectedActors, movement)
    AttributePanel.updateFields()
  }

  /**
   * Deselets all actors
   */
  private def deselect() = {
    project.deselectSelected()
  }

  /**
   * Deselects the actor given as parameter.
   */
  private def deselect(actor: Actor): Unit = {
    actor.selected = false
    project.selectedActors -= actor
  }

  /**
   * Creates an DuplicateAction of the selected actors.
   */
  private def duplicate() = {
    if (project.currentScene.isDefined) {
      val selected = project.getSelectedActors
      if (selected.size > 0) {
        new DuplicateAction(project.currentScene.get, selected)
        LevelEditor.reDraw()
      }
    }
  }

  /**
   * Creates an DeleteAction of the selected actors.
   */
  private def delete() = {
    if (project.currentScene.isDefined) {
      val selected = project.getSelectedActors
      if (selected.size > 0) {
        new DeleteAction(project.currentScene.get, selected)
        project.selectedActors.clear()
        LevelEditor.reDraw()
      }
    }
  }

  /**
   * Converts position using scenePanel's size
   */
  def convertPosition(point: Point): Point = {
    val zoom = LevelEditor.scenePanel.getZoom()
    val cameraPosition = LevelEditor.scenePanel.getCameraPosition()
    val c_point = new Point((point.x / zoom - cameraPosition.x).toInt, (point.y / zoom - cameraPosition.y).toInt)
    return new Point(c_point.x, c_point.y)
  }

  /**
   * Return the x and y values of the selection rectangle.
   * Returned value is a tuple of the form (startX, startY, endX, endY).
   */
  def getSelectionRectangle(): (Int, Int, Int, Int) = {
    var sx = 0; var ex = 0; var sy = 0; var ey = 0
    if (lastMouse.x < currentMouse.x) {
      sx = lastMouse.x
      ex = currentMouse.x
    } else {
      sx = currentMouse.x
      ex = lastMouse.x
    }
    if (lastMouse.y < currentMouse.y) {
      sy = lastMouse.y
      ey = currentMouse.y
    } else {
      sy = currentMouse.y
      ey = lastMouse.y
    }
    (sx, sy, ex, ey)
  }

  /**
   * Selects all similar actors based on the actors selected.
   */
  private def selectSimilar() = {
    val actors = project.getActors
    if (project.selectedActors.size > 0) {
      val actor = project.selectedActors.head
      //project.modifySelected(actor => {
      val selected = actors.filter(_.prototype == actor.prototype)
      selected.foreach(project.selectActor(_))
      LevelEditor.reDraw()
      //})
    }
  }

  private var lastMouse = new Point(0, 0)
  private var currentMouse = new Point(0, 0)
  private var dragStart = new Point(0, 0)

  private var mousePressed = false
  private var shiftPressed = false
  private var spaceDown = false
  var mouseOutside = false

  this.reactions += {
    case e: MousePressed => {
      e.peer.getButton match {
        case java.awt.event.MouseEvent.BUTTON1 => {
          leftPressed(e)
        }
        case _ =>
      }
    }
    case e: MouseReleased => {
      mousePressed = false
      if (!spaceDown) {
        val selected = project.getSelectedActors
        if (selected.length > 0) {
          if (project.currentScene.isDefined) {
            //val dp = deltaPoint(dragStart, currentMouse)
            val first = selected.head
            val dp = deltaPoint(first.getLastPosition, first.position)
            if (dp.x != 0 || dp.y != 0) new MoveAction(selected, dp)
          }
          project.modifySelected(_.updateLastPosition())
          AttributePanel.updateFields()
        }
        
        if (mouseOutside) {
          mouseOutside = false
          reDraw()
        }
      } else {
        spaceDown = false
      }
    }
    case e: MouseDragged => {
      val mousePosition = this.convertPosition(e.point)
      if (mousePressed) {
        if (spaceDown) {
          currentMouse = mousePosition
          val zoom = LevelEditor.scenePanel.getZoom()
          val delta = deltaPoint(lastMouse, mousePosition)
          LevelEditor.scenePanel.moveCamera(new Point(delta.x, delta.y))
          reDraw()
        } else if (mouseOutside) {
          if (!shiftPressed) deselect()
          project.selectActors(getSelectionRectangle())
          dragStart = mousePosition
          currentMouse = mousePosition
          reDraw()
        } else {
          currentMouse = mousePosition
          project.modifySelected(_.drag(deltaPoint(lastMouse, mousePosition)))
        }
      }
    }
    case KeyPressed(_, Key.Delete, _, _) => {
      delete()
    }
    case KeyPressed(_, Key.BackSpace, _, _) => {
      delete()
    }
    case KeyPressed(_, Key.D, _, _) => {
      duplicate()
    }
    case KeyPressed(_, Key.Shift, _, _) => {
      shiftPressed = true
    }
    case KeyReleased(_, Key.Shift, _, _) => {
      shiftPressed = false
    }
    case KeyPressed(_, Key.Up, _, _) => {
      arrowKey(new Point(0, -1))
    }
    case KeyPressed(_, Key.Down, _, _) => {
      arrowKey(new Point(0, 1))
    }
    case KeyPressed(_, Key.Right, _, _) => {
      arrowKey(new Point(1, 0))
    }
    case KeyPressed(_, Key.Left, _, _) => {
      arrowKey(new Point(-1, 0))
    }
    case KeyPressed(_, Key.E, _, _) => {
      selectSimilar()
    }
    case KeyPressed(_, Key.Space, _, _) => {
      spaceDown = true
    }
    case KeyReleased(_, Key.Space, _, _) => {
      if (!mousePressed) spaceDown = false
    }
  }

  /**
   * Handles the presses of left mouse button. If an empty area is pressed this method sets up drawing selection
   * rectangle. If an unselected actor is clicked it is selected. If a selected actor is clicked this methods sets up
   * dragging of the actor.
   */
  private def leftPressed(e: MousePressed): Unit = {
    if (!LevelEditor.scenePanel.hasFocus) {
      LevelEditor.scenePanel.requestFocus()
      return
    }

    val mousePosition = this.convertPosition(e.point)

    mousePressed = true
    lastMouse = mousePosition
    currentMouse = mousePosition
    if (!spaceDown) {
      val actor = project.mousePress(mousePosition)
      if (!actor.isDefined) {
        if (!shiftPressed) deselect
        mouseOutside = true
      } else {
        dragStart = mousePosition
        if (!shiftPressed && !actor.get.selected) {
          deselect()
        }
        val currentScene = project.currentScene
        if (currentScene.isDefined && actor.isDefined) {
          if (actor.get.selected && shiftPressed) {
            deselect(actor.get)
          } else {
            project.selectActor(actor.get)
          }
        }
      }
      AttributePanel.updateFields()
      reDraw()
    }
  }
}