package com.github.seanparsons.datastores

import scalaz._
import Scalaz._
import scalaz.concurrent._
import scalaz.effect._

object IOUtils {
  def ioToPromise[T](io: IO[T])(implicit strategy: Strategy): Promise[T] = Promise(io.unsafePerformIO())
}
