package net.ndolgov.exercise

import net.ndolgov.exercise.SparkBootstrap.sparkSession
import org.apache.spark.sql.SparkSession
import org.scalatest.{Assertions, FlatSpec}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Execute exercise queries, write results into stdout (grep for "***" if the output is too verbose)
  */
final class ExerciseQueriesTestSuit extends FlatSpec with Assertions {
  private val logger : Logger = LoggerFactory.getLogger(this.getClass)
  private val FILE_ROOT_DIR = s"target/test-classes/parquet/"

  "Exercise queries" should "finish successfully" in {
    val session: SparkSession = sparkSession("JsonToParquetTransformationTestSuit")
    try {
      // Q1
      val topDomains = QueryRunner.topPublisherDomains(session, FILE_ROOT_DIR + "impressions").take(5)
      topDomains.foreach(row => System.out.println("*** Top domain: " + row.getString(0)))

      // Q2
      val topForPublisher = QueryRunner.frequentPhrases(session, FILE_ROOT_DIR + "inserts")
      topForPublisher.take(3).foreach(row => System.out.println("*** Publisher: " + row.getString(0) + ", phrase: " + row.getString(1)))

      // Q3
      val ctrs = QueryRunner.computeCTRs(session, FILE_ROOT_DIR + "clicks", FILE_ROOT_DIR + "impressions")
      ctrs.foreach(row => System.out.println("*** Publisher: " + row.getString(0) + ", CTR: " + row.getDouble(3)))

      // Q4
      val (bottomTen, topTen) = QueryRunner.clickDelays(session, FILE_ROOT_DIR + "clicks", FILE_ROOT_DIR + "impressions")
      bottomTen.take(10).foreach(row => System.out.println("*** Bottom: " + row.toString))
      topTen.take(10).foreach(row => System.out.println("*** Top   : " + row.toString))
    } finally {
      session.stop
    }
  }
}
