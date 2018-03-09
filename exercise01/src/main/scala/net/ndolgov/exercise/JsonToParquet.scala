package net.ndolgov.exercise

import org.apache.spark.sql.SparkSession
import org.slf4j.{Logger, LoggerFactory}

/**
  * JSON to Parquet file conversion utils
  */
class JsonToParquet(session: SparkSession, jsonRoot : String, parquetRoot : String) {
  def transformClicks(): String = {
    val destDir: String = parquetRoot + "clicks"
    transform(jsonRoot + "click.json", destDir)
    destDir
  }

  def transformInserts(): String = {
    val destDir: String = parquetRoot + "inserts"
    transform(jsonRoot + "insert.json", destDir)
    destDir
  }

  def transformImpressions(): String = {
    val destDir: String = parquetRoot + "impressions"
    transform(jsonRoot + "view.json", destDir)
    destDir
  }

  private def transform(jsonFile : String, parquetDir: String): Unit = {
    session.
      read.json(jsonFile).
      write.parquet(parquetDir)
  }
}

object JsonToParquet {
  private val logger : Logger = LoggerFactory.getLogger(this.getClass)

  def transformAll(session: SparkSession, jsonFile : String, parquetDir: String): Unit = {
    val transformer = new JsonToParquet(session, jsonFile, parquetDir)

    val startedClicksAt = System.currentTimeMillis
    val clicksDir = transformer.transformClicks()
    logger.info("Converted clicks in: " + secondsSince(startedClicksAt) + " seconds, written files to: " + clicksDir)

    val startedInsertsAt = System.currentTimeMillis
    val insertsDir = transformer.transformInserts()
    logger.info("Converted inserts in: " + secondsSince(startedInsertsAt) + " seconds, written files to: " + insertsDir)

    val startedImpressionsAt = System.currentTimeMillis
    val impressionsDir = transformer.transformImpressions()
    logger.info("Converted impressions in: " + secondsSince(startedImpressionsAt) + " seconds, written files to: " + impressionsDir)
  }

  private def secondsSince(startedClicksAt: Long): Long = {
    (System.currentTimeMillis - startedClicksAt) / 1000
  }
}
