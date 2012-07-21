package com.github.seanparsons

import java.io.File

package object datastores {
  def withTempFile[T](expression: File => T): T = {
    val file = File.createTempFile("unit", "test")
    try {
      expression(file)
    } finally {
      file.delete()
    }
  }
}
