package com.esri

import jline.TerminalFactory

/**
  * Report job process to the terminal console. It display the progress in the following form:
  *
  * progress/progressMax[======>           ]
  *
  * Where the width is proportional to the terminal/console width.
  *
  * @param progressMax the progress max steps.
  */
class TerminalProgressBar(progressMax: Int) extends ProgressBar {

  val termWidth = TerminalFactory.get().getWidth() - 1
  var progressInc = 0

  /**
    * Show the progress bar.
    *
    * @param increment the progress increment.
    */
  override def progress(increment: Int = 1) = {
    progressInc += increment
    if (progressInc <= progressMax) {
      val prefix = s"$progressInc/$progressMax"
      val width = termWidth - prefix.length - 3
      val width1 = width * progressInc / progressMax
      val width2 = width - width1

      val sb = new StringBuilder(termWidth)
      sb.append("\r")
      sb.append(prefix)
      sb.append("[")
      var i = 0
      while (i < width1) {
        sb.append("=")
        i += 1
      }
      sb.append(">")
      i = 0
      while (i < width2) {
        sb.append(" ")
        i += 1
      }
      sb.append("]")
      print(sb.toString)
    }
  }

  /**
    * Clear the line and move the cursor up one line - http://tldp.org/HOWTO/Bash-Prompt-HOWTO/x361.html
    */
  override def finish() = {
    print("\r" + " " * termWidth + " \033[1A")
  }
}

object TerminalProgressBar {
  def apply(progressMax: Int): TerminalProgressBar = new TerminalProgressBar(progressMax)
}
