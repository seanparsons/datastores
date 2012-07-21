package com.github.seanparsons.datastores

import scalaz._
import Scalaz._
import scalaz.effect._
import java.io._

trait FixedSize[T] {
  def size: Int
}

trait FixedSizeSerDe[T] extends FixedSize[T] with SerDe[T]

trait FixedSizes {
  def fixedSize[T](fixedSize: Int): FixedSize[T] = new FixedSize[T] {
    val size = fixedSize
  }
  implicit def fixedSizeSerDe[T](implicit fixedSize: FixedSize[T], serDe: SerDe[T]): FixedSizeSerDe[T] = new FixedSizeSerDe[T]{
    @inline def serialize(value: T): Array[Byte] = serDe.serialize(value)
    @inline def deserialize(bytes: Seq[Byte]): T = serDe.deserialize(bytes)
    @inline val size = fixedSize.size
  }
  implicit val longFixedSize: FixedSize[Long] = fixedSize(8)
  implicit val intFixedSize: FixedSize[Int] = fixedSize(4)
  implicit val shortFixedSize: FixedSize[Short] = fixedSize(2)
  implicit val byteFixedSize: FixedSize[Byte] = fixedSize(1)
  implicit val booleanFixedSize: FixedSize[Boolean] = fixedSize(1)
}

case class FixedSizeIndexedStore[T](file: File, ensureWrites: Boolean = false)(implicit fixedSizeSerDe: FixedSizeSerDe[T]) extends Closeable {
  val randomAccess = new RandomAccessFile(file, if (ensureWrites) "rwd" else "rw")
  
  def close() = randomAccess.close()
  
  @inline
  private[this] def calculateFilePosition(index: Long): Long = index * fixedSizeSerDe.size.toLong
  
  def write(index: Long, value: T): IO[Unit] = {
    val filePosition: Long = calculateFilePosition(index)
    val bytes: Array[Byte] = fixedSizeSerDe.serialize(value)
    Monad[IO].point{
      randomAccess.seek(filePosition)
      randomAccess.write(bytes)
    }
  }
  def read(index: Long): IO[T] = {
    val filePosition: Long = calculateFilePosition(index)
    val bytes: Array[Byte] = Array.ofDim(fixedSizeSerDe.size)
    Monad[IO].point{
      randomAccess.seek(filePosition)
      randomAccess.readFully(bytes, 0, fixedSizeSerDe.size)
      fixedSizeSerDe.deserialize(bytes)
    }
  }
}
