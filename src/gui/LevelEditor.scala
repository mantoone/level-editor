package gui

import java.awt.Dimension
import java.awt.Toolkit
import scala.swing.Component
import scala.swing.MainFrame
import scala.swing.Orientation
import scala.swing.SimpleSwingApplication
import scala.swing.SplitPane
import actions.ActionController
import debug.DebugLogger.log
import io.ProjectManager
import io.SceneManager
import javax.swing.UIDefaults
import javax.swing.UIManager
import logic.Project
import scala.swing.ScrollPane

/**
 * The top most component of the GUI. The application can be launched by running this object as Scala application.
 */
object LevelEditor extends SimpleSwingApplication {
  
  val version = "1.0.1"

  // Set to system look and feel
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

  // Disable arrowkeys from moving the scrollpane view
  UIManager.getDefaults().put("ScrollPane.ancestorInputMap",
    new UIDefaults.LazyInputMap(Array[Object]()))

  // Set scroll speed to 4 on Mac and 16 on other operating systems
  private val scrollSpeed =
    if (System.getProperty("os.name").startsWith("Mac")) {
      System.setProperty("apple.laf.useScreenMenuBar", "true")
      UIManager.put("List.lockToPositionOnScroll", false);
      4
    } else 16

  // Set size to max screen size
  val s = Toolkit.getDefaultToolkit().getScreenSize()
  val screenSize = new Dimension((s.width * 0.95).toInt, (s.height * 0.9).toInt)

  // Settings
  var pixelSmoothing = true
  var project: Project = null
  var projectPath = ""

  def loadProject(): Unit = {
    val file = ProjectChooser.open()
    if (file.isDefined) {
      projectPath = file.get.getAbsolutePath()
      project = ProjectManager.loadProject(projectPath)
      sceneSelect.update()
      this.goToSceneSelect()
    }
  }

  val scenePanel = new ScenePanel()

  val layerPanel = new LayerPanel()

  val layerScrollPane = new ScrollPane(layerPanel) {
    border = null
  }

  val leftPane = new SplitPane(Orientation.Horizontal, AttributePanel, layerScrollPane) {
    enabled = false
    border = null
    continuousLayout = true
  }

  val mainPanel = new SplitPane(Orientation.Vertical, leftPane, scenePanel) {
    enabled = false
    border = null
    continuousLayout = true
  }

  val startPanel = new StartPanel()

  val sceneSelect = new SceneSelectPanel(scrollSpeed)

  val mainFrame = new MainFrame {
    title = "Scene Builder"
    menuBar = new TopBar()

    override def closeOperation() {

      this.contents.head match {
        case e: StartPanel       => super.closeOperation()
        case e: SplitPane        => LevelEditor.goToSceneSelect()
        case e: SceneSelectPanel => LevelEditor.goToStartScreen()
        case _                   =>
      }
      this.visible = true
    }
  }

  this.updateCurrentMain(startPanel)

  def top = mainFrame

  def getSize = this.mainPanel.size

  def reDraw() = {
    this.scenePanel.repaint()
  }

  def selectScene(scene: String) = {
    ActionController.clear()
    val sceneSize = SceneManager.loadScene(project, projectPath, scene)
    val size = new Dimension(sceneSize.width, sceneSize.height)
    scenePanel.maximumSize = size
    scenePanel.preferredSize = size
    scenePanel.updateSceneSize(sceneSize)
    mainPanel.preferredSize = screenSize
    this.updateCurrentMain(mainPanel)

    // Update layers
    layerPanel.updateLayers(project.currentScene.get.getLayers)

    scenePanel.requestFocus()
  }

  def goToSceneSelect() = {
    this.updateCurrentMain(sceneSelect)
  }

  def goToStartScreen() = {
    this.updateCurrentMain(startPanel)
  }

  def updateCurrentMain(target: Component) {
    mainFrame.resizable = true
    mainFrame.contents = target
    mainFrame.centerOnScreen()
  }

  def updateCurrentMain(target: Component, resizable: Boolean) {
    updateCurrentMain(target)
    mainFrame.resizable = resizable
  }

  def convertY(y: Int) = project.getSceneHeight - y
}
