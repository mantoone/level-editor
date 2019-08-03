package actions

import logic.Actor
import logic.Scene

/**
 * Class ModifyActorsAction represents an action where a function that is given as constructor parameter is
 * applied to all selected actors.
 */
class ModifyActorsAction(private val scene: Scene, private val selectedActors: Vector[Actor], function: Actor => Unit)
  extends Action {

  private val oldLayers = scene.copyLayers()
  selectedActors.foreach { actor => function(actor) }
  private val newLayers = scene.copyLayers()

  ActionController.add(this)

  override def redo() = {
    scene.layers = newLayers
  }

  def execute() = {}

  def undo() = {
    scene.layers = oldLayers
  }
  
  override def toString = "Modify Actors Action"
}