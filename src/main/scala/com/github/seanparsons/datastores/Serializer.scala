package com.github.seanparsons.datastores

import java.nio._

// TODO: Combine FixedSize, Serializer and Deserializer, potentially FixedSizeSerDe.

trait SerDes {
  implicit val longSerDe: SerDe[Long] = new SerDe[Long]{
    def deserialize(bytes: Seq[Byte]): Long = {
      (bytes(0) & 0xFFL) << 56 | 
      (bytes(1) & 0xFFL) << 48 | 
      (bytes(2) & 0xFFL) << 40 | 
      (bytes(3) & 0xFFL) << 32 | 
      (bytes(4) & 0xFFL) << 24 | 
      (bytes(5) & 0xFFL) << 16 | 
      (bytes(6) & 0xFFL) << 8 | 
      (bytes(7) & 0xFFL)
    }
    def serialize(value: Long): Array[Byte] = {
      Array[Byte](
        (value >> 56).toByte,
        (value >> 48).toByte,
        (value >> 40).toByte,
        (value >> 32).toByte,
        (value >> 24).toByte,
        (value >> 16).toByte,
        (value >> 8).toByte,
        value.toByte
      )
    }
  }
  implicit val intSerDe: SerDe[Int] = new SerDe[Int]{
    def serialize(value: Int) = { 
      Array[Byte](
        (value >>> 24).toByte, 
        (value >>> 16).toByte, 
        (value >>> 8).toByte, 
        value.toByte
      )
    }
    def deserialize(bytes: Seq[Byte]): Int = {
      bytes(0) << 24 | 
      (bytes(1) & 0xFF) << 16 | 
      (bytes(2) & 0xFF) << 8 | 
      (bytes(3) & 0xFF)
    }
  }
  implicit val shortSerDe: SerDe[Short] = new SerDe[Short]{
    def serialize(value: Short) = {
      Array[Byte](
        (value >>> 8.toShort).toByte,
        value.toByte
      )
    }
    def deserialize(bytes: Seq[Byte]): Short = {
      ((bytes(0) << 8) | (bytes(1) & 0xFF)).toShort
    }
  }
  implicit val byteSerDe: SerDe[Byte] = new SerDe[Byte]{
    def serialize(value: Byte) = {
      Array(value)
    }
    def deserialize(bytes: Seq[Byte]): Byte = {
      bytes(0)
    }
  }
  implicit val booleanSerDe: SerDe[Boolean] = new SerDe[Boolean]{
    def serialize(value: Boolean): Array[Byte] = {
      Array[Byte](
        if(value) 1.toByte else 0.toByte
      )
    }
    def deserialize(bytes: Seq[Byte]): Boolean = {
      if (bytes(0) == 1.toByte) true else false
    }
  }
}

trait SerDe[T] {
  def serialize(value: T): Array[Byte]
  def deserialize(bytes: Seq[Byte]): T
}

