package logic

import java.awt.Color
import java.awt.Graphics2D
import scala.collection.mutable.Buffer
import scala.xml.NodeSeq
import scala.xml.XML
import java.awt.Dimension

/**
 * The class Scene represents one Scene (also called a level) of a project.
 */
class Scene(private val id: String, private val gsversion: String, private val pgfversion: String,
            var layers: Vector[Layer], val size: Dimension, private val color: Color,
            private val otherNodes: NodeSeq) {

  def removeActor(actor: Actor) = {
    this.layers.foreach(_.removeActor(actor))
  }

  def duplicateActor(actor: Actor) = {
    this.layers.map(_.duplicateActor(actor)).filter { _.isDefined }.map { _.get }.toVector
  }

  def draw(g: Graphics2D) = {
    g.setColor(color)
    g.fillRect(0, 0, this.size.width, this.size.height)
    this.layers.foreach(l =>
      l.getActors.foreach(_.draw(g, this, l.locked)))
  }

  //def getActors = this.layers.flatMap(_.getActors).toVector

  def copyLayers() = {
    this.layers.map { _.copy() }
  }

  def toXML = {
    <scene id={ this.id } GSCVersion={ this.gsversion } PGFVersion={ this.pgfversion }>
      <layers>
        { this.layers.flatMap(_.toXML(this.size.height)) }
      </layers>
      { this.otherNodes }
    </scene>
  }

  def save(filename: String) = {
    XML.save(filename, this.toXML, "UTF-8", true, null)
  }

  def getLayers = this.layers.toVector
}