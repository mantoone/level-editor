package actions

import scala.collection.mutable.Stack
import gui.LevelEditor

/**
 * The object ActionController keeps track of all executed actions, making it possible to undo and redo actions.
 */

object ActionController {
  private val undos = Stack[Action]()
  private val redos = Stack[Action]()

  /**
   * Adds an action to undos and clears redos.
   */
  def add(action: Action) = {
    undos.push(action)
    redos.clear()
  }

  /**
   * Undoes the last action.
   */
  def undo() = {
    if (!undos.isEmpty) {
      val action = undos.pop()
      redos.push(action)
      action.undo()
      LevelEditor.reDraw()
    }
  }

  /**
   * Redoes the last undone action.
   */
  def redo() = {
    if (!redos.isEmpty) {
      val action = redos.pop()
      undos.push(action)
      action.redo()
      LevelEditor.reDraw()
    }
  }

  /**
   * Resets both undos and redos.
   */
  def clear() = {
    undos.clear()
    redos.clear()
  }
  
  def getStack = {
    this.undos.toVector
  }
}