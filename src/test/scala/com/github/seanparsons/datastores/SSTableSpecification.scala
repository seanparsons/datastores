package com.github.seanparsons.datastores

import org.scalacheck.Prop._
import org.scalacheck.Gen._
import org.scalacheck.Gen
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.scalacheck.Choose._
import org.scalacheck.Choose
import scalaz._
import Scalaz.{mapOrder => _, _}
import scala.math.Numeric.Implicits
import org.specs2.{Specification, ScalaCheck}
import java.io.File
import scalaz.concurrent.Strategy

case class SSTableSpecification() extends Specification 
                                  with ScalaCheck 
                                  with SerDes 
                                  with FixedSizes {
  implicit val mapEqual: Equal[Map[Long, Long]] = Equal.equalA

  def ssTableLongLongSpec = 
    "SSTable" ^
      "Map[Long, Long]" !
        forAllNoShrink(arbitrary[Map[Long, Long]]){(contents) =>
          implicit val strategy = Strategy.Sequential
      	  withTempFile{file =>
      	    val store = new FileSSTable[Long, Long](file)
      	    store.write(contents).get
      	    val deserialized: Map[Long, Long] = store.read().get
      	    ("deserialized = " + deserialized) |: deserialized === contents
      	  }
      	} ^ end
  def is = ssTableLongLongSpec      
}				  

