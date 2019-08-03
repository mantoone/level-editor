package logic

import scala.collection.mutable.Buffer
import scala.xml.NodeSeq
import gui.LevelEditor

/**
 * Class Layer represents one layer in the scenes. Layers can contain any number of actors.
 */
class Layer(private val id: String, private val actors: Buffer[Actor], private val otherNodes: NodeSeq) {

  var visible = true
  var locked = false

  def name = {
    (this.otherNodes \ "text").text
  }

  def removeActor(actor: Actor) = {
    this.actors -= actor
  }

  def duplicateActor(actor: Actor): Option[Actor] = {
    val i = this.actors.indexOf(actor)
    if (i >= 0) {
      val copy = actor.copy()
      this.actors.insert(i, copy)
      Some(copy)
    } else {
      None
    }
  }

  def getActors = if (this.visible) this.actors.toVector else Vector[Actor]()

  def copy() = {
    new Layer(this.id, this.actors.clone(), this.otherNodes)
  }

  def deselectSelected(): Unit = {
    for (actor <- this.getActors.filter { _.selected }) {
      LevelEditor.project.selectedActors -= actor
      actor.selected = false
    }
  }

  def toXML(sceneHeight: Int) = {
    <layer id={ this.id }>
      <actors>
        { this.actors.flatMap(_.toXML(sceneHeight)) }
      </actors>
      { this.otherNodes }
    </layer>
  }

  override def toString = {
    "Layer with " + this.actors.size + " actors." + "\n----------------\n" + this.actors.mkString(", ")
  }
}