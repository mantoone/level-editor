package scripting

import logic.Actor
import gui.LevelEditor
import java.awt.Color

class ActorWrapper(actors: Seq[Actor]) {
  def setx(nx: Int) = {
    for (actor <- actors) {
      actor.position.x = nx
    }
    LevelEditor.reDraw()
  }
  
  def setcolor(r: Int, g: Int, b: Int) = {
    for (actor <- actors) {
      actor.color = new Color(r, g, b)
    }
    LevelEditor.reDraw()
  }
  
  def setcolor(r: Int, g: Int, b: Int, a: Int) = {
    for (actor <- actors) {
      actor.color = new Color(r, g, b, a)
    }
    LevelEditor.reDraw()
  }
}