package gui

import java.awt.Toolkit
import java.awt.event.KeyEvent
import scala.swing.Action
import scala.swing.Menu
import scala.swing.MenuBar
import scala.swing.MenuItem
import actions.ActionController
import gui.dialog.CircleDialog
import gui.dialog.GridDialog
import javax.swing.ImageIcon
import javax.swing.KeyStroke
import logic.Grid
import java.awt.RenderingHints
import java.awt.im.InputContext
import java.util.Locale
import javax.imageio.ImageIO
import scala.swing.Frame

/**
 * Represents the MenuBar that is shown on above of the application.
 */
class TopBar extends MenuBar {
  private val modifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
  val is = getClass.getResourceAsStream("/tick.png")
  private val tickIcon = new ImageIcon(ImageIO.read(is))

  contents += new Menu("Scene") {

    // Scene selection button
    contents += new MenuItem(new Action("Scene Select") {

      def apply {
        LevelEditor.goToSceneSelect()
      }
    })

    // Scene save button
    contents += new MenuItem(new Action("Save") {
      accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_S, modifier))

      def apply {
        LevelEditor.project.save()
      }
    })
    
    // Export as a CSV file
    contents += new MenuItem(new Action("Export as a CSV file") {
      def apply {
        LevelEditor.project.saveCSV()
      }
    })

  }
  contents += new Menu("Edit") {

    // Undo button
    contents += new MenuItem(new Action("Undo") {
      accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_Z, modifier))

      def apply {
        ActionController.undo()
      }
    })

    // Redo button
    contents += new MenuItem(new Action("Redo") {
      accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_R, modifier))

      def apply {
        ActionController.redo()
      }
    })
  }
  contents += new Menu("Actor") {

    // Circle generation button
    contents += new MenuItem(new Action("Make Circle") {
      def apply {
        CircleDialog.show()
      }
    })
  }
  contents += new Menu("View") {

    // Grid toggle button
    contents += new MenuItem(new Action("Grid") {
      accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_G, modifier))

      def apply {
        val enabled = Grid.toggle()
        this.icon = if (enabled) {
          tickIcon
        } else { null }
        LevelEditor.reDraw()
      }
    })

    // Grid settings button
    contents += new MenuItem(new Action("Grid Settings") {
      def apply {
        GridDialog.show()
      }
    })

    // Pixel smoothing toggle button
    contents += new MenuItem(new Action("Smooth Pixels") {
      this.icon = if (enabled) {
        tickIcon
      } else { null }

      def apply {
        LevelEditor.pixelSmoothing = !LevelEditor.pixelSmoothing
        val enabled = LevelEditor.pixelSmoothing

        LevelEditor.scenePanel.rh = if (enabled) {
          new RenderingHints(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        } else {
          new RenderingHints(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
        }

        this.icon = if (enabled) {
          tickIcon
        } else { null }
        LevelEditor.reDraw()
      }
    })

    // Zoom in
    contents += new MenuItem(new Action("Zoom in") {
      val context = InputContext.getInstance()
      val locale = context.getLocale()
      
      var input: Option[KeyStroke] = None
      if (locale == Locale.UK || locale == Locale.US) {
        input = Some(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, modifier))
      } else {
        input = Some(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, modifier))
      }
      accelerator = input
      
      def apply {
        LevelEditor.scenePanel.rescale(1.1d)
      }
    })

    // Zoom out
    contents += new MenuItem(new Action("Zoom out") {
      accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, modifier))

      def apply {
        LevelEditor.scenePanel.rescale(0.9d)
      }
    })
    
    // Scripting
    contents += new MenuItem(new Action("ScriptEditor") {
      accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_A, modifier))

      def apply {
        val f = new Frame() {
          this.contents = scripting.ScriptEditor
        }
        f.centerOnScreen()
        f.visible = true
        scripting.ScriptEditor.update()
      }
    })
  }

}