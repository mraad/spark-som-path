package com.esri

/**
  * No Op progress bar implementation.
  */
class NoopProgressBar() extends ProgressBar {
  override def progress(increment: Int = 1): Unit = {}

  override def finish(): Unit = {}
}

object NoopProgressBar {
  def apply(): NoopProgressBar = new NoopProgressBar()
}
