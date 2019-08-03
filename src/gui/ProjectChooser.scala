package gui

import java.awt.FileDialog
import java.io.File

import scala.swing.FileChooser

import javax.swing.filechooser.FileNameExtensionFilter

/**
 * ProjectChooser is used to launch FileChooser that is used to choose a GS Project to be opened.
 */
object ProjectChooser {  
  private val filter = new FileNameExtensionFilter("GS Project", "gameproj", "gsproj")
  private val fc = new FileChooser()
  fc.fileFilter = filter

  private def getExtension(file: File): Option[String] = {
    val name = file.getName()
    val i = name.lastIndexOf(".")
    if (i != -1) {
      Some(name.substring(i + 1, name.length()))
    } else {
      None
    }
  }
  
  /**
   * Shows a dialog that can be used to open a GS project.
   */
  def open(): Option[File] = {
    val result = fc.showOpenDialog(null)
    if (result == FileChooser.Result.Approve) {
      val selectedFile = fc.selectedFile
      val extension = getExtension(selectedFile)
      if (extension.isDefined && extension.get == "gsproj") {
        Some(selectedFile.getParentFile())
      } else Some(selectedFile)
    } else {
      None
    }
  }
}