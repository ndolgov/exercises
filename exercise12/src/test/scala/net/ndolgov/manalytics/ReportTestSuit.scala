package net.ndolgov.manalytics

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.col
import org.scalatest.{Assertions, FlatSpec}

/** Trigger marketing report preparation */
final class ReportTestSuit extends FlatSpec with Assertions {
  private val FILE_ROOT_DIR: String = "target/test-classes/"
  private val CSV_ROOT = s"$FILE_ROOT_DIR/csv/"

  private val spark = SparkBootstrap.sparkSession("ReportTestSuit")
  spark.sparkContext.setLogLevel("ERROR")

  "Task #1.1 Purchase attribution" should "be built with SparkSQL" in {
    val dfAttributionProjection = PurchaseAttribution.attributeWithSparkSql(CSV_ROOT, spark)
    assert(dfAttributionProjection.count() == 6)

    println("Purchase attribution with SparkSQL")
    dfAttributionProjection.show()
  }

  "Task #1.2 Purchase attribution" should "be built with UDAF" in {
    val dfAttributionProjection = PurchaseAttribution.attributeWithCustomAggregator(CSV_ROOT, spark)
    assert(dfAttributionProjection.count() == 6)

    println("Purchase attribution with UDAF")
    dfAttributionProjection.show()
  }

  "Task #1.3 Purchase attribution" should "be built with plain SQL" in {
    createTempViewsFromCsvFiles()

    val dfAttributionProjection = PlainSqlPurchaseAttribution.attributeWithPlainSql(spark)
    assert(dfAttributionProjection.count() == 6)

    println("Purchase attribution with plain SQL")
    dfAttributionProjection.show()
  }

  "Task #2.1 Top 10 Campaigns report" should "be built with plain SQL and SparkSQL" in {
    val dfAttributionProjection = PurchaseAttribution.attributeWithCustomAggregator(CSV_ROOT, spark)

    val dfTopCampaigns = SparkSqlAggregateStatistics.topCampaigns(dfAttributionProjection)
    assert(dfTopCampaigns.count() == 2)
    println("Top 10 Campaigns with SparkSQL")
    dfTopCampaigns.show()

    val dfTopCampaignsPlain = PlainSqlAggregateStatistics.topCampaigns(dfAttributionProjection, spark)
    assert(dfTopCampaignsPlain.count() == 2)
    println("Top 10 Campaigns with plain SQL")
    dfTopCampaignsPlain.show()
  }

  "Task #2.2 Most popular channels by campaign report" should "be built with plain SQL and SparkSQL" in {
    val dfAttributionProjection = PurchaseAttribution.attributeWithCustomAggregator(CSV_ROOT, spark)

    val dfChannelsEngagement = SparkSqlAggregateStatistics.channelsEngagement(dfAttributionProjection)
    assert(dfChannelsEngagement.count() == 2)
    println("Most popular channels by campaign with SparkSQL")
    dfChannelsEngagement.show()

    val dfChannelsEngagementPlain = PlainSqlAggregateStatistics.channelsEngagement(dfAttributionProjection, spark)
    assert(dfChannelsEngagementPlain.count() == 2)
    println("Most popular channels by campaign with plain SQL")
    dfChannelsEngagementPlain.show()
  }

  "Task #2.3 Most popular channels by campaign report" should "be built with a single plain SQL pass" in {
    createTempViewsFromCsvFiles()

    val dfChannelsEngagement = PlainSqlPurchaseAttribution.channelsEngagement(spark)
    assert(dfChannelsEngagement.count() == 2)
    println("Most popular channels by campaign with a single plain SQL pass")
    dfChannelsEngagement.show()
  }

  private def createTempViewsFromCsvFiles(): Unit = {
    CsvLoader.loadClickStream(CSV_ROOT, spark)
      .withColumn(CampaignId, col(s"$Attributes.campaign_id"))
      .withColumn(ChannelId, col(s"$Attributes.channel_id"))
      .withColumn(PurchaseId, col(s"$Attributes.purchase_id"))
      .createOrReplaceTempView("clickstream")

    val dfChannelsEngagementPlain = CsvLoader.loadPurchases(CSV_ROOT, spark)
      .createOrReplaceTempView("purchases")
  }
}


