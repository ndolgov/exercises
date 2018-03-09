package net.ndolgov.exercise

import net.ndolgov.exercise.SparkBootstrap.sparkSession
import org.apache.spark.sql.SparkSession
import org.scalatest.{Assertions, FlatSpec}

/**
  * Wrap raw data conversion into a unit test to avoid running the entire Spark standalone cluster
  * Run it once to pre-process data.
  */
final class JsonToParquetTransformationTestSuit extends FlatSpec with Assertions {
  private val FILE_ROOT_DIR: String = "target/test-classes/"
  private val JSON_ROOT = s"$FILE_ROOT_DIR/json/"
  private val PARQUET_ROOT = s"$FILE_ROOT_DIR/parquet/"

  "JSON files " should "be transformed to Parquet format" in {
    val session: SparkSession = sparkSession("JsonToParquetTransformationTestSuit")

    try {
      //JsonToParquet.transformAll(session, JSON_ROOT, PARQUET_ROOT)
      convertTestData(session)
    } finally {
      session.stop
    }
  }

  private def convertTestData(session: SparkSession): Unit = {
    session.
      read.json(JSON_ROOT + "clicks.json").
      write.parquet(PARQUET_ROOT + "clicks")

    session.
      read.json(JSON_ROOT + "impressions.json").
      write.parquet(PARQUET_ROOT + "impressions")

    session.
      read.json(JSON_ROOT + "inserts.json").
      write.parquet(PARQUET_ROOT + "inserts")
  }
}


