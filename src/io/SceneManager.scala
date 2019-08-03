package io

import java.awt.Color

import scala.collection.mutable.Buffer
import scala.xml.XML

import XMLHelper.createActor
import XMLHelper.nodeToColor
import XMLHelper.nodeToDimension
import logic.Actor
import logic.Layer
import logic.Project
import logic.Scene

/**
 * SceneManager is used to load a scene in a project.
 */
object SceneManager {
  def loadScene(project: Project, projectPath: String, sceneNumber: String) = {
    val scenePath = projectPath + s"/scenes/$sceneNumber.xml"
    val sceneData = XML.load(scenePath)
    val sceneSize = nodeToDimension(sceneData \ "attributes" \ "size")
    val sceneId = (sceneData \ "@id").text
    val gsversion = (sceneData \ "@GSCVersion").text
    val pgfversion = (sceneData \ "@PGFVersion").text
    val sceneOtherNodes = (sceneData \ "_").filter(_.label != "layers")

    // Create layers and actors
    val layers = Buffer[Layer]()
    (sceneData \ "layers" \ "layer").foreach(
      // Create layers
      layer => {
        // Create actors
        val actors = Buffer[Actor]()
        (layer \ "actors" \ "actor").foreach(actorNode => {
          actors += createActor(project, sceneSize.height, actorNode)
        })

        // Layer data
        val layerId = (layer \ "@id").text
        val otherNodes = (layer \ "_").filter(_.label != "actors")

        layers += new Layer(layerId, actors, otherNodes)
      })

    val sceneColorNode = sceneData \ "attributes" \ "color"
    val sceneColor = Some(nodeToColor(sceneColorNode)).getOrElse(Color.WHITE)
    val scene = new Scene(sceneId, gsversion, pgfversion, layers.toVector, sceneSize, sceneColor, sceneOtherNodes)

    project.currentScene = Some(scene)
    project.currentScenePath = Some(scenePath)

    sceneSize
  }
}