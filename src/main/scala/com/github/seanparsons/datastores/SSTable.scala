package com.github.seanparsons.datastores

import scalaz._
import scalaz.effect._
import scalaz.iteratee._
import scalaz.iteratee.IterateeT._
import scalaz.Ordering._
import java.io._
import annotation.tailrec
import IOUtils.ioToPromise

case class FileSSTable[T, U](file: File) {
  def write(contents: Map[T, U])(keyOrder: Order[T], keyFixedSerDe: FixedSizeSerDe[T], valueFixedSerDe: FixedSizeSerDe[U]): IO[Unit] = {
    IO(new FileOutputStream(file)).bracket(stream => IO(stream.close)){stream =>
      IO(contents.keySet.toSeq.sortWith(keyOrder.order(_, _) == LT).foreach{key =>
        stream.write(keyFixedSerDe.serialize(key))
        stream.write(valueFixedSerDe.serialize(contents(key)))
      })
    }
  }
  def read()(keyOrder: Order[T], keyFixedSerDe: FixedSizeSerDe[T], valueFixedSerDe: FixedSizeSerDe[U]): IO[Map[T, U]] = {
    IO(new FileInputStream(file)).bracket(stream => IO(stream.close)){stream =>
      val keyBytes: Array[Byte] = Array.ofDim(keyFixedSerDe.size)
      val valueBytes: Array[Byte] = Array.ofDim(valueFixedSerDe.size)
      @tailrec
      def readEntry(mapSoFar: Map[T, U]): Map[T, U] = {
        if (stream.read(keyBytes) >= 0 && stream.read(valueBytes) >= 0) {
          readEntry(mapSoFar + (keyFixedSerDe.deserialize(keyBytes) -> valueFixedSerDe.deserialize(valueBytes)))
        } else mapSoFar
      }
      IO(readEntry(Map.empty))
    }
  }
}

object FileSSTable {
  def write[T, U](file: File, enumerator: EnumeratorT[(T, U), IO])(keyFixedSerDe: FixedSizeSerDe[T], valueFixedSerDe: FixedSizeSerDe[U]): IO[Unit] = {
    IO(new FileOutputStream(file)).bracket(stream => IO(stream.close)){stream =>
      def write(element: (T, U)) = for {
        _ <- IO(stream.write(keyFixedSerDe.serialize(element._1)))
        _ <- IO(stream.write(valueFixedSerDe.serialize(element._2)))
      } yield ()
      foldM[(T, U), IO, Unit](())((_: Unit, e: (T, U)) => write(e)).run
    }
  }
}
