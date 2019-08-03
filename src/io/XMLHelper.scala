package io

import java.awt.Color
import java.awt.Dimension
import java.awt.Point

import scala.xml.Node
import scala.xml.NodeBuffer
import scala.xml.NodeSeq
import scala.xml.NodeSeq.seqToNodeSeq

import logic.Actor
import logic.ActorPrototype
import logic.Image
import logic.Project

/**
 * The object XMLHelper contains methods for converting XML nodes to objects.
 */
object XMLHelper {
  /**
   * Converts a node to java.awt.Point object. sceneHeight is needed to be able to convert the y value.
   */
  def nodeToPoint(sceneHeight: Int, node: NodeSeq) = {
    val reals = node \ "real"
    val x = nodeToDouble(findNode(reals, "id", "x")).toInt
    val y = sceneHeight - nodeToDouble(findNode(reals, "id", "y")).toInt
    new Point(x, y)
  }
  
  def nodeToPoint(node: NodeSeq) = {
    val reals = node \ "real"
    val x = nodeToDouble(findNode(reals, "id", "x")).toInt
    val y = nodeToDouble(findNode(reals, "id", "y")).toInt
    new Point(x, y)
  }

  /**
   * Converts a node to java.awt.Dimension object.
   */
  def nodeToDimension(node: NodeSeq) = {
    val reals = node \ "real"
    val w = nodeToDouble(findNode(reals, "id", "width")).toInt
    val h = nodeToDouble(findNode(reals, "id", "height")).toInt
    new Dimension(w, h)
  }

  /**
   * Converts a node to java.awt.Color object.
   */
  def nodeToColor(node: NodeSeq) = {
    val reals = node \ "real"
    val r = nodeToDouble(findNode(reals, "id", "red")).toFloat
    val g = nodeToDouble(findNode(reals, "id", "green")).toFloat
    val b = nodeToDouble(findNode(reals, "id", "blue")).toFloat
    val a = nodeToDouble(findNode(reals, "id", "alpha")).toFloat
    new Color(r, g, b, a)
  }
  
  def nodeToColor(node: NodeSeq, prototype: ActorPrototype) = {
    val reals = node \ "real"
    val rOpt = findNodeOption(reals, "id", "red")
    val r = if(rOpt.isDefined) nodeToDouble(rOpt.get).toFloat else (prototype.color.getRed.toDouble / 255.0d).toFloat
    
    val gOpt = findNodeOption(reals, "id", "green")
    val g = if(gOpt.isDefined) nodeToDouble(gOpt.get).toFloat else (prototype.color.getGreen.toDouble / 255.0d).toFloat
    
    val bOpt = findNodeOption(reals, "id", "blue")
    val b = if(bOpt.isDefined) nodeToDouble(bOpt.get).toFloat else (prototype.color.getBlue.toDouble / 255.0d).toFloat
    
    val aOpt = findNodeOption(reals, "id", "alpha")
    val a = if(aOpt.isDefined) nodeToDouble(aOpt.get).toFloat else (prototype.color.getAlpha.toDouble / 255.0d).toFloat
    
    new Color(r, g, b, a)
  }

  /**
   * Converts a node's text to double and returns it. If there is an error converting it 0 is returned.
   */
  def nodeToDouble(node: NodeSeq) = try {
    node.text.toDouble
  } catch {
    case e: NumberFormatException => {
      0
    }
  }

  /**
   * Returns the node that has an attribute with a specific value.
   */
  def findNode(nodeSeq: NodeSeq, attribute: String, value: String): Node = {
    val temp = findNodeOption(nodeSeq, attribute, value)
    if(!temp.isDefined) println(nodeSeq)
    temp.get
  }
  
  def findNodeOption(nodeSeq: NodeSeq, attribute: String, value: String): Option[Node] = {
    nodeSeq.find { node => (node \ ("@" + attribute)).text == value }
  }
  
  def convertPosToScene(pos: Point, sceneHeight: Int) = {
    new Point(pos.x, sceneHeight - pos.y)
  }

  /**
   * Creates an Actor object from node and returns it.
   */
  def createActor(project: Project, sceneHeight: Int, actor: Node) = {
    // Get id and prototype
    val attrMap = actor.attributes.asAttrMap
    val id = attrMap.get("id").get
    val prototypeID = attrMap.get("prototype").get
    val prototype = project.prototypes.get(prototypeID).get

    // Get attributes
    val otherNodes = new NodeBuffer()
    val unusedAttributes = new NodeBuffer()
    var position: Option[Point] = None
    var size: Option[Dimension] = None
    var rotation: Option[Double] = None
    var color: Option[Color] = None
    var image: Option[Image] = None
    (actor \ "_").foreach(node => {
      node.label match {
        case "attributes" => {

          val attributes = node \ "_"

          attributes.foreach(node => {
            if (node.attribute("name").isDefined) {
              unusedAttributes += node
            } else {
              node.attribute("id").get.text match {
                case "position" => {
                  position = Some(nodeToPoint(sceneHeight, node))
                }
                case "size" => {
                  
                  val reals = node \ "real"
                  
                  val wOpt = findNodeOption(reals, "id", "width")
                  val w = if(wOpt.isDefined) nodeToDouble(wOpt.get).toInt else prototype.size.width
                  
                  val hOpt = findNodeOption(reals, "id", "height")
                  val h = if(hOpt.isDefined) nodeToDouble(hOpt.get).toInt else prototype.size.height
                  
                  
                  size = Some(new Dimension(w, h))
                }
                case "rotation" => {
                  rotation = Some(nodeToDouble(node))
                }
                case "color" => {
                  color = Some(nodeToColor(node, prototype))
                }
                case "image" => {
                  image = Some(Image(node.text.replace(".png", "")))
                }
                case _ => {
                  unusedAttributes += node
                }
              }
            }
          })

        }
        case _ => otherNodes += node
      }
    })

    new Actor(id, prototype,
      position.getOrElse(convertPosToScene(prototype.position, sceneHeight)),
      size.getOrElse(prototype.size),
      rotation.getOrElse(prototype.rotation),
      color.getOrElse(prototype.color),
      image.getOrElse(prototype.image),
      unusedAttributes.toSeq,
      otherNodes.toSeq)
  }

  /**
   * Creates an ActorPrototype object from node and returns it.
   */
  def createActorPrototype(actor: Node) = {
    // Get id and prototype
    val attrMap = actor.attributes.asAttrMap
    val id = attrMap.get("id").get

    // Get attributes
    val otherNodes = new NodeBuffer()
    val unusedAttributes = new NodeBuffer()
    var position: Option[Point] = None
    var size: Option[Dimension] = None
    var rotation: Option[Double] = None
    var color: Option[Color] = None
    var image: Option[Image] = None
    (actor \ "_").foreach(node => {
      node.label match {
        case "attributes" => {

          val attributes = node \ "_"

          attributes.foreach(node => {
            if (node.attribute("name").isDefined) {
              unusedAttributes += node
            } else {
              node.attribute("id").get.text match {
                case "position" => {
                  position = Some(nodeToPoint(node))
                }
                case "size" => {
                  size = Some(nodeToDimension(node))
                }
                case "rotation" => {
                  rotation = Some(nodeToDouble(node))
                }
                case "color" => {
                  color = Some(nodeToColor(node))
                }
                case "image" => {
                  image = Some(Image(node.text))
                }
                case _ => {
                  unusedAttributes += node
                }
              }
            }
          })

        }
        case _ => otherNodes += node
      }
    })

    new ActorPrototype(id,
      position.getOrElse(new Point(0, 0)),
      size.getOrElse(new Dimension(100, 100)),
      rotation.getOrElse(0),
      color.getOrElse(Color.WHITE),
      image.getOrElse(Image("")),
      unusedAttributes.toSeq,
      otherNodes.toSeq)
  }
}