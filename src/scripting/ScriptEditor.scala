package scripting

import scala.swing.BoxPanel
import scala.swing.Button
import scala.swing.Orientation
import scala.swing.TextArea
import javax.script.ScriptEngineManager
import scala.swing.event.ButtonClicked
import java.awt.Dimension
import gui.LevelEditor

object ScriptEditor extends BoxPanel(Orientation.Vertical) {
  
  this.preferredSize = new Dimension(480, 320)
  this.minimumSize = new Dimension(320, 240)
  
  val factory = new ScriptEngineManager();
  // create a JavaScript engine
  val engine = factory.getEngineByName("JavaScript");
  // evaluate JavaScript code from String

  object asd {
    val a = "hello"
    var x = 0
    var y = 0

    def setx(newx: Int) = {
      this.x = newx
    }
  }

  def update() {
    if(LevelEditor.project.selectedActors.length != 0){
      engine.put("actors", new ActorWrapper(LevelEditor.project.selectedActors))
    }
  }

  val textArea = new TextArea()
  val btn = new Button("Execute")

  this.contents += (textArea, btn)
  
  this.listenTo(btn)
  
  this.reactions += {
    case e: ButtonClicked => {
      engine.eval(textArea.text)
    }
  }
}
