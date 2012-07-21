package com.github.seanparsons.datastores

import org.scalacheck.Prop._
import org.scalacheck.Gen._
import org.scalacheck.Gen
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.scalacheck.Choose._
import org.scalacheck.Choose
import scalaz._
import Scalaz._
import scala.math.Numeric.Implicits
import org.specs2.{Specification, ScalaCheck}
import java.io.File

case class FixedSizeIndexedStoreSpecification() extends Specification 
                                                with ScalaCheck 
                                                with SerDes 
                                                with FixedSizes {
						  
	val indexValueGen: Gen[Map[Int, Long]] = arbitrary(arbImmutableMap[Int, Long](Arbitrary(chooseNum[Int](0, 2000)), arbLong)) 
						  
  def fixedSizeIndexedStoreSpec = 
    "FixedSizeIndexedStore" ^
      "Any long value" !
        forAllNoShrink(indexValueGen){(indexesAndValues) =>
      	  withTempFile{file =>
      	    val store = new FixedSizeIndexedStore[Long](file)
      	    indexesAndValues.map(pair => store.write(pair._1, pair._2)).toList.sequence.unsafePerformIO
      	    val deserialized: Map[Int, Long] = indexesAndValues.keySet.map(index => (index, store.read(index).unsafePerformIO)).toMap
      	    ("deserialized = " + deserialized) |: deserialized === indexesAndValues
      	  }
      	} ^ end
  def is = fixedSizeIndexedStoreSpec
}
