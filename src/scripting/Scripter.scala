package scripting

import javax.script._
import logic.Actor

object Scripter extends App {
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
  
  engine.put("asd", asd)
  engine.eval("print('Hello, World')");
  engine.eval("print(asd.setx(10));");
  engine.eval("print(asd.x());");
}