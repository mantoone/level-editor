package logic

import java.awt.Color
import java.awt.Dimension
import java.awt.Point

import scala.xml.NodeSeq

/**
 * ActorPrototype is a prototype for actors. Multiple actors can have the same prototype.
 * If some of actors properties are not defined they are loaded from its prototype.
 */
class ActorPrototype(val id: String, val position: Point, val size: Dimension, val rotation: Double,
                     val color: Color, val image: Image, val attributes: NodeSeq, val otherNodes: NodeSeq) {

  override def toString = "Actor Prototype: " + this.id
}