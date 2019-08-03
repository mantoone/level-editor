package actions

import logic.Actor
import logic.Scene

/**
 * Class DuplicateAction represents an action where selected actors are deleted.
 */
class DuplicateAction(scene: Scene, selectedActors: Vector[Actor])
  extends ModifyActorsAction(scene, selectedActors, scene.duplicateActor) {}