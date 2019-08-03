package logic

import java.awt.Point

/**
 * The object LogicHelper contains methods for checking if a point is inside an actor and if an actor is inside a rectangle.
 */
object LogicHelper {
  /**
   * Rotates a point (px, py) around the center point of an actor based on the actors rotation.
   */
  private def rotatePoint(actor: Actor, px: Int, py: Int) = {
    val x = actor.position.x
    val y = actor.position.y
    val angle = Math.toRadians(actor.rotation)

    val dx = px - x
    val dy = py - y
    val tx = Math.cos(angle) * dx - Math.sin(angle) * dy + x
    val ty = Math.sin(angle) * dx + Math.cos(angle) * dy + y
    (tx.toInt, ty.toInt)
  }

  /**
   * Returns Some(actor) if the point given as parameter is inside the actor, otherwise returns None.
   */
  def pointInside(actor: Actor, point: Point): Option[Actor] = {
    val px = point.x
    val py = point.y

    val w = actor.size.width
    val h = actor.size.height
    val x = actor.position.x
    val y = actor.position.y

    val rotated = rotatePoint(actor, px, py)
    val tx = rotated._1
    val ty = rotated._2

    if (tx >= x - w / 2 && ty >= y - h / 2 && tx <= x + w / 2 && ty <= y + h / 2) {
      Some(actor)
    } else None
  }

  /**
   * Returns true if the actor given as parameter is inside the rectangle. Rectangle is given in the form startX, startY, endX, endY.
   */
  def actorIsInside(actor: Actor, startX: Int, startY: Int, endX: Int, endY: Int) = {
    val w = actor.size.width
    val h = actor.size.height
    var x = actor.position.x - w / 2
    var y = actor.position.y - h / 2
    var r = rotatePoint(actor, x, y)
    x = r._1; y = r._2
    val topleft = (x > startX && x < endX && y > startY && y < endY)

    x = actor.position.x + w / 2
    y = actor.position.y - h / 2
    r = rotatePoint(actor, x, y)
    x = r._1; y = r._2
    val topRight = (x > startX && x < endX && y > startY && y < endY)

    x = actor.position.x + w / 2
    y = actor.position.y + h / 2
    r = rotatePoint(actor, x, y)
    x = r._1; y = r._2
    val botRight = (x > startX && x < endX && y > startY && y < endY)

    x = actor.position.x - w / 2
    y = actor.position.y + h / 2
    r = rotatePoint(actor, x, y)
    x = r._1; y = r._2
    val botLeft = (x > startX && x < endX && y > startY && y < endY)

    topleft && topRight && botRight && botLeft
  }
}