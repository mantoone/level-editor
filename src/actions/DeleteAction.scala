package actions

import logic.Actor
import logic.Scene

/**
 * Class DeleteAction represents an action where selected actors are deleted.
 */
class DeleteAction(scene: Scene, selectedActors: Vector[Actor])
  extends ModifyActorsAction(scene, selectedActors, scene.removeActor){}