package logic

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Point

import scala.collection.mutable.Buffer
import scala.util.Random
import scala.xml.NodeSeq

/**
 * Object Actor is used to store and generate unique actor ids.
 */
object Actor {
  val ids = Buffer[String]()
  val random = new Random()

  def newId = {
    var nid = ""
    do {
      nid = "id" + "%06d".format(this.random.nextInt(1000000))
    } while (this.ids.contains(nid))
    nid
  }
}

/**
 * Class Actor represents one actor in the scene.
 */
class Actor(private val id: String, val prototype: ActorPrototype, var position: Point, var size: Dimension,
            var rotation: Double, var color: Color, private val image: Image, private val attributes: NodeSeq,
            val otherNodes: NodeSeq) {

  this.image.createColored(color)
  Actor.ids += this.id

  var selected = false

  private var lastPosition = new Point(position.x, position.y)

  def getLastPosition = lastPosition
  
  /**
   * Moves the actor by the deltaPoint given as parameter without updating lastPosition. Useful when moving temporarly.
   */
  def drag(deltaPoint: Point) = {
    this.position =
      Grid.convertPosition(new Point(this.lastPosition.x + deltaPoint.x, this.lastPosition.y + deltaPoint.y))
  }

  /**
   * Moves the actor (permanently) by the deltaPoint given as parameter.
   */
  def moveBy(deltaPoint: Point) = {
    this.setPosition(new Point(this.position.x + deltaPoint.x, this.position.y + deltaPoint.y))
  }

  def setPosition(newPosition: Point) = {
    this.position = new Point(newPosition.x, newPosition.y)
    this.updateLastPosition()
  }

  def updateLastPosition() = {
    this.lastPosition = new Point(this.position.x, this.position.y)
  }

  def setX(newX: Int) = {
    this.setPosition(new Point(newX, this.position.y))
  }

  def setY(newY: Int) = {
    this.setPosition(new Point(this.position.x, newY))
  }

  def setWidth(newWidth: Int) = {
    this.size = new Dimension(newWidth, this.size.height)
  }

  def setHeight(newHeight: Int) = {
    this.size = new Dimension(this.size.width, newHeight)
  }

  def setRotation(newRotation: Double) {
    this.rotation = newRotation
  }

  def draw(g: Graphics2D, scene: Scene, locked: Boolean) = {
    this.image.draw(g, scene, this)

    val w = this.size.width
    val h = this.size.height
    val x = this.position.x
    val y = this.position.y
    if (locked) {
      val g2 = g.create().asInstanceOf[Graphics2D]
      g2.setColor(new Color(0.2f, 0.2f, 0.2f, 0.2f))
      g2.translate(x, y)
      g2.rotate(-Math.toRadians(this.rotation))
      g2.fillRect(-w / 2, -h / 2, w, h)
      g2.dispose()
    }
    if (this.selected) {
      
      val g2 = g.create().asInstanceOf[Graphics2D]
      g2.translate(x, y)
      g2.rotate(-Math.toRadians(this.rotation))
      g2.setColor(new Color(0f, 0f, 0f, 0.15f))
      g2.fillRect(-w / 2, -h / 2, w, h)
      g2.setColor(Color.CYAN)
      g2.drawRect(-w / 2, -h / 2, w, h)
      g2.dispose()
    }
    //g.drawString(this.locked.toString, x, y)

  }

  /**
   * Makes a copy of this actor and returns it. The only difference is that the new actor has a new unique id.
   */
  def copy() = {
    new Actor(Actor.newId, this.prototype, this.position, this.size.getSize, this.rotation,
      this.color, this.image, this.attributes, this.otherNodes)
  }

  def toXML(sceneHeight: Int) = {
    <actor id={ this.id } prototype={ this.prototype.id }>
      <attributes>
        <point id="position">
          <real id="x">{ this.position.getX }</real>
          <real id="y">{ sceneHeight - this.position.getY }</real>
        </point>
        <size id="size">
          <real id="width">{ this.size.width }</real>
          <real id="height">{ this.size.height }</real>
        </size>
        <angle id="rotation">{ this.rotation }</angle>
        <color id="color">
          <real id="red">{ this.color.getRed / 255f }</real>
          <real id="green">{ this.color.getGreen / 255f }</real>
          <real id="blue">{ this.color.getBlue / 255f }</real>
          <real id="alpha">{ this.color.getAlpha / 255f }</real>
        </color>
        <image id="image">{ this.image.name }</image>
        { this.attributes }
      </attributes>
      { this.otherNodes }
    </actor>
  }
}
