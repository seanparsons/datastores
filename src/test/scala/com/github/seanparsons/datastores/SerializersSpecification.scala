package com.github.seanparsons.datastores

import org.scalacheck.Prop._
import org.scalacheck.Gen._
import org.scalacheck.Gen
import org.scalacheck.Arbitrary._
import org.scalacheck.Choose._
import org.scalacheck.Choose
import scalaz._
import Scalaz._
import scala.math.Numeric.Implicits
import org.specs2.{Specification, ScalaCheck}

case class SerializersSpecification() extends Specification with ScalaCheck with SerDes {
  implicit val params = set(minTestsOk -> 10000)
  def longSerializerSpec = 
    "longSerializer" ^
      "Any long value" !
        forAllNoShrink(arbitrary[Long]){long =>
      	  val serialized = longSerDe.serialize(long)
      	  val deserialized = longSerDe.deserialize(serialized)
      	  ("deserialized = " + deserialized) |: deserialized === long
        } ^ end
  def intSerializerSpec = 
    "intSerializer" ^
      "Any int value" ! 
      	forAllNoShrink(arbitrary[Int]){int =>
      	  val serialized = intSerDe.serialize(int)
      	  val deserialized = intSerDe.deserialize(serialized)
      	  ("deserialized = " + deserialized) |: deserialized === int
    	  } ^ end
  def shortSerializerSpec = 
    "shortSerializer" ^
      "Any short value" !
        forAllNoShrink(arbitrary[Short]){short =>
      	  val serialized = shortSerDe.serialize(short)
      	  val deserialized = shortSerDe.deserialize(serialized)
      	  ("deserialized = " + deserialized) |: deserialized === short
        } ^ end
  def byteSerializerSpec = 
    "byteSerializer" ^
      "Any byte value" !
        forAllNoShrink(arbitrary[Byte]){byte =>
      	  val serialized = byteSerDe.serialize(byte)
      	  val deserialized = byteSerDe.deserialize(serialized)
      	  ("deserialized = " + deserialized) |: deserialized === byte
        } ^ end
  def booleanSerializerSpec = 
    "booleanSerializer" ^
      "Any boolean value" !
        forAllNoShrink(arbitrary[Boolean]){boolean =>
      	  val serialized = booleanSerDe.serialize(boolean)
      	  val deserialized = booleanSerDe.deserialize(serialized)
      	  ("deserialized = " + deserialized) |: deserialized === boolean
        } ^ end        
        
  def is = longSerializerSpec ^ intSerializerSpec ^ shortSerializerSpec ^ byteSerializerSpec ^ booleanSerializerSpec
}
