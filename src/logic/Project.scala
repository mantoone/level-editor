package logic

import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Point
import scala.Vector
import scala.collection.mutable.Buffer
import scala.collection.mutable.Map
import gui.LevelEditor
import javax.swing.ImageIcon
import scala.swing.FileChooser
import java.io.PrintWriter
import java.io.File

/**
 * The class Project represents one loaded GS project.
 */
class Project(val prototypes: Map[String, ActorPrototype],
              val scenes: Array[(ImageIcon, String, String)]) {

  var currentScene: Option[Scene] = None
  var currentScenePath: Option[String] = None
  val selectedActors = Buffer[Actor]()

  def draw(g: Graphics2D) = {
    this.currentScene.foreach(_.draw(g))
  }

  def deselectSelected(): Unit = {
    modifySelected(_.selected = false)
    selectedActors.clear()
  }

  def getSceneHeight = this.currentScene.map(_.size.height).getOrElse(0)

  def getSceneSize = this.currentScene.map(_.size).getOrElse(new Dimension(0, 0))

  def mousePress(point: Point) = {
    val actors = this.getActors.reverse
    this.getActorAtPoint(actors, point)
  }

  def getActorAtPoint(actors: Vector[Actor], point: Point): Option[Actor] = {
    var found: Option[Actor] = None
    var actor = 0
    while (found.isEmpty && actor < actors.size) {
      found = LogicHelper.pointInside(actors(actor), point)
      actor += 1
    }
    found
  }

  def selectActor(actor: Actor) = {
    if (!this.selectedActors.exists(_ == actor)) {
      this.selectedActors += actor
      actor.selected = true
    }
  }

  def selectActors(rect: (Int, Int, Int, Int)) = {
    this.getActors.filter(actor => LogicHelper.actorIsInside(actor, rect._1, rect._2, rect._3, rect._4))
      .foreach(this.selectActor(_))
  }

  def getSelectedActors = this.selectedActors.toVector

  def save() = {
    if (this.currentScenePath.isDefined && this.currentScene.isDefined) {
      this.currentScene.get.save(this.currentScenePath.get)
    }
  }

  def saveCSV() = {
    if (this.currentScenePath.isDefined && this.currentScene.isDefined) {
      val fc = new FileChooser()
      var selectedFile: Option[File] = None

      var loop = true
      while (loop && (selectedFile.isEmpty || selectedFile.get.exists())) {
        if (selectedFile.isDefined && selectedFile.get.exists()) {
          fc.title = "File already exists!"
        } else {
          fc.title = "Enter the name for a new csv file"
        }
        val result = fc.showSaveDialog(null)

        if (result == FileChooser.Result.Approve) {
          selectedFile = Some(fc.selectedFile)
        } else if (result == FileChooser.Result.Cancel) {
          loop = false
        }
      }

      // Write to the file
      val pw = new PrintWriter(selectedFile.get)
      val actors = this.getActors
      var name = "Unknown actor"
      for (actor <- actors) {
        actor.prototype.attributes.foreach(node => {
          node.attribute("id").get.text match {
            case "name" => name = node.text
            case _      =>
          }
        })
        print(actor.prototype.otherNodes)
        pw.println(name + "," + actor.position.getX() * 2 + "," + actor.position.getY() * 2 + "," + actor.rotation)
      }
      pw.close()
    }
  }

  def getActors = {
    if (currentScene.isDefined) currentScene.get.getLayers.filter(!_.locked).flatMap { _.getActors } else Vector[Actor]()
  }

  def modifySelected(f: Actor => Unit) = {
    if (selectedActors.size > 0) {
      selectedActors.foreach(f)
      LevelEditor.reDraw()
    }
  }
}
