package net.ndolgov.exercise

import net.ndolgov.exercise.QueryRunner.{PHRASE, TOTAL}
import net.ndolgov.exercise.SparkBootstrap.sparkSession
import org.apache.spark.sql.SparkSession
import org.scalatest.{Assertions, FlatSpec}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Execute exercise queries on a small test data to ensure infrastructure is fully functional
  */
final class DryRunQueriesTestSuit extends FlatSpec with Assertions {
  private val logger : Logger = LoggerFactory.getLogger(this.getClass)
  private val FILE_ROOT_DIR = "target/test-classes/"

  "Search for frequent phrases " should "find two known phrases" in {
    val session: SparkSession = sparkSession("JsonToParquetTransformationTestSuit")
    try {
      val topForPublisher = QueryRunner.frequentPhrases(session, FILE_ROOT_DIR + "inserts")
      assert(topForPublisher.select(TOTAL, PHRASE).where(topForPublisher(PHRASE).equalTo("nattie")).first().getLong(0) == 2)
      assert(topForPublisher.select(TOTAL, PHRASE).where(topForPublisher(PHRASE).equalTo("light duty shop")).first().getLong(0) == 3) // 2 if case sensitive
    } finally {
      session.stop
    }
  }

  "Search for top publishers " should "find two known domains" in {
    val session: SparkSession = sparkSession("JsonToParquetTransformationTestSuit")
    try {
      val topDomains = QueryRunner.topPublisherDomains(session, FILE_ROOT_DIR + "impressions").take(5)
      topDomains.foreach(row => "*** " + System.out.println(row.toString))
      assert(topDomains(0).getString(0) == "bf9ccfa0caea9be84bf534a2c97592634e247b54.com" && topDomains(0).getInt(1) == 4)
      assert(topDomains(1).getString(0) == "P101.com" && topDomains(1).getInt(1) == 3)
      assert(topDomains(2).getString(0) == "P100.com" && topDomains(2).getInt(1) == 2)
    } finally {
      session.stop
    }
  }
}
