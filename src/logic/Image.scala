package logic

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

import scala.collection.mutable.Map

/**
 * The object Image stores the BufferedImages and Images for the project.
 */
object Image {
  val bufferedImages = Map[String, BufferedImage]()
  val images = Map[String, Image]()
  def apply(name: String) = {
    val image = this.images.get(name)
    image.getOrElse({
      val newImage = new Image(name, this.bufferedImages.get(name))
      this.images += name -> newImage
      newImage
    })
  }
}

/**
 * The class Image represents the image of one or multiple actors.
 */
class Image(val name: String, private val image: Option[BufferedImage]) {
  private val coloredImages = Map[Color, BufferedImage]()

  def getImage(color: Color) = {
    val colored = coloredImages.get(color)
    if (colored.isDefined) {
      colored
    } else this.image
  }

  def createColored(color: Color) = {
    if (this.image.isDefined && !this.coloredImages.contains(color)) {
      val img = this.image.get
      val newImg = new BufferedImage(img.getWidth, img.getHeight, BufferedImage.TYPE_INT_ARGB)
      val ar = color.getRed() / 255f
      val ag = color.getGreen() / 255f
      val ab = color.getBlue() / 255f
      for (x <- 0 until img.getWidth; y <- 0 until img.getHeight) {
        val rgb = img.getRGB(x, y)
        val a = (rgb >> 24) & 255
        val r = (rgb >> 16) & 255
        val g = (rgb >> 8) & 255
        val b = rgb & 255
        val nrgb = (a << 24) | (Math.min((r * ar).toInt, 255) << 16) |
          (Math.min((g * ag).toInt, 255) << 8) | Math.min((b * ab).toInt, 255)
        newImg.setRGB(x, y, nrgb)
      }
      this.coloredImages += color -> newImg
    }
  }

  def draw(g: Graphics2D, scene: Scene, actor: Actor) = {
    val w = actor.size.width
    val h = actor.size.height
    val x = actor.position.x - w / 2
    val y = actor.position.y - h / 2
    val angle = -Math.toRadians(actor.rotation)

    // Image color and transparency
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, actor.color.getAlpha / 255f))

    val g2 = g.create().asInstanceOf[Graphics2D]
    g2.setColor(actor.color)
    g2.translate(x + w / 2, y + h / 2)
    g2.rotate(angle)
    g2.translate(-w / 2, -h / 2)

    // Draw image or rectangle
    val img = this.getImage(actor.color)
    if (img.isDefined) {
      val at = new AffineTransform()
      at.setToIdentity()
      at.scale(w.toDouble / img.get.getWidth(), h.toDouble / img.get.getHeight())
      g2.drawImage(img.get, at, null)
    } else {
      g2.fillRect(0, 0, w, h)
    }
    g2.dispose()
  }

  override def toString = this.name + " img = " + this.image.isDefined
}