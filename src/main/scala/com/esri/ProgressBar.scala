package com.esri

/**
  * A progress bar trait.
  */
trait ProgressBar {
  /**
    * To be invoked when the work is in progress.
    */
  def progress(increment: Int = 1): Unit

  /**
    * To be invoked when the work in progress is finished.
    */
  def finish(): Unit
}
