package actions

/**
 * The trait Action represents an abstract undoable and redoable action.
 */

trait Action {
  def execute()
  def undo()
  def redo() = this.execute()
  
  override def toString = "Action"
}