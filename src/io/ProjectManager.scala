package io

import java.awt.image.BufferedImage
import java.io.File

import scala.Vector
import scala.collection.mutable.Map
import scala.xml.XML

import XMLHelper.createActorPrototype
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import logic.Actor
import logic.ActorPrototype
import logic.Image
import logic.Project

/**
 * ProjectManager can be used to load GS projects.
 */
object ProjectManager {
  private val prototypes = Map[String, ActorPrototype]()

  /**
   * Loads the project from the path given as parameter. The path must include the name of the project. For example "documents/myproject.gameproj".
   */
  def loadProject(projectPath: String) = {

    val startTime = System.nanoTime()

    // Load Images
    val assets = XML.loadFile(projectPath + "/assets.xml")
    val imgFileToName = Map[String, String]()

    (assets \ "images" \ "_").foreach({ imageNode =>
      val name = (imageNode \ "@name").text
      val filename = (imageNode \ "file" \ "@name").text
      imgFileToName += filename -> name
    })

    val imagesDir = new File(projectPath + "/images")
    Image.bufferedImages.clear()
    imagesDir.listFiles().foreach(file => {
      if (imgFileToName.contains(file.getName)) {
        Image.bufferedImages += imgFileToName(file.getName) -> ImageIO.read(file)
      }
    })

    // Go through assets and create image objects
    Image.images.clear()

    (assets \ "images" \ "_").foreach({ imageNode =>
      val name = (imageNode \ "@name").text
      Image.images += name -> Image(name)
    })

    // Load ActorPrototypes
    prototypes.clear()
    val actorsDir = new File(projectPath + "/actors")
    actorsDir.listFiles().foreach(file => {
      val prototype = createActorPrototype(XML.loadFile(file))
      prototypes += prototype.id -> prototype
    })

    // Check if a file is a XML file.
    def isXMLFile(file: File) = {
      val name = file.getName()
      val i = name.lastIndexOf(".")
      if (i != -1) {
        if (name.substring(i + 1, name.length()) == "xml") true else false
      } else {
        false
      }
    }

    // Create blank icon
    val is = getClass.getResourceAsStream("/blank.png")
    val blankIcon = new ImageIcon(ImageIO.read(is))

    // Load scene icons and names
    val scenesDir = new File(projectPath + "/scenes")
    val scenes = scenesDir.listFiles().filter(isXMLFile)
    val sceneArray = scenes.map { file =>
      val scene = XML.loadFile(file)
      val id = (scene \ "@id").text
      val name = (scene \ "attributes" \ "text").text
      val imageFile = new File(projectPath + "/screenshots/" + id + ".png")
      val icon =
        if (imageFile.exists()) {
          val imgIcon = new ImageIcon(imageFile.getAbsolutePath)

          // Scale the image to be the right size (100 by 84 pixels)
          val image = imgIcon.getImage
          val newImage = image.getScaledInstance(100, 84, java.awt.Image.SCALE_SMOOTH)
          imgIcon.setImage(newImage)
          imgIcon
        } else {
          blankIcon
        }

      (id, (icon, name, file.toString))

    }
    val sceneMap = sceneArray.toMap

    // Get scene order
    val objectXML = XML.loadFile(projectPath + "/object.xml")
    val sceneOrder = objectXML \ "scenes" \ "scene"
    val orderedScenes = sceneOrder.map(s => sceneMap((s \ "@id").text)).toArray

    val endTime = System.nanoTime()
    println("Loading the project took " + (endTime - startTime) / 1000000000d + " seconds")

    new Project(prototypes, orderedScenes)
  }
}