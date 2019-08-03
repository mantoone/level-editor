package gui

import scala.swing.ScrollPane
import scala.swing.Component
import scala.swing.GridBagPanel
import scala.swing.GridBagPanel._
import scala.swing.FlowPanel

class SceneScrollPane extends ScrollPane {

  def getScrollPosition: (Int, Int) = {
    (this.horizontalScrollBar.value, this.verticalScrollBar.value)
  }

  def setScrollPosition(hpos: Double, vpos: Double): Unit = {
    this.horizontalScrollBar.value = hpos.toInt
    this.verticalScrollBar.value = vpos.toInt
    this.repaint()
  }

  private val gridBagPanel = new GridBagPanel() {
    //val c = new Constraints
  }
  this.contents = gridBagPanel

  def setContents(a: Component) = {
    this.contents = new GridBagPanel() {
      this.add(a, (0, 0))
    }
    this.revalidate()
  }

}
