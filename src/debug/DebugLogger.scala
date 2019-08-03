package debug

object DebugLogger {
  val s = System.nanoTime()
  def log(msg: String) = println((System.nanoTime() - s) / 1000000 + " " + msg)
}