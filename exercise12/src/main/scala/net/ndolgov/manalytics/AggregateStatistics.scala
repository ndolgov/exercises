package net.ndolgov.manalytics

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, count, rank, sum}

/** Calculate top campaigns and channels engagement reports with plain SQL */
object PlainSqlAggregateStatistics {
  private val AttributionProjection = "attribution_projection"

  def topCampaigns(dfAttributionProjection: DataFrame, spark: SparkSession): DataFrame = {
    dfAttributionProjection.createOrReplaceTempView(AttributionProjection)

    spark.sql(s"""
      |select
      |  campaignId,
      |  sum(billingCost) as revenue
      |from $AttributionProjection
      |where isConfirmed
      |group by campaignId
      |order by revenue desc
      |limit 10
      """.stripMargin)
  }

  def channelsEngagement(dfAttributionProjection: DataFrame, spark: SparkSession): DataFrame = {
    dfAttributionProjection.createOrReplaceTempView(AttributionProjection)

    val sessionCounts = "session_counts"

    val dfSessionCounts = spark.sql(s"""
      |select
      |  campaignId,
      |  channelId,
      |  count(sessionId) as session_count,
      |  rank() over (partition by campaignId order by count(sessionId) desc) as rnk
      |from $AttributionProjection
      |group by campaignId, channelId
      """.stripMargin)

    dfSessionCounts.createOrReplaceTempView(sessionCounts)

    spark.sql(s"""
      |select
      |  campaignId,
      |  channelId
      |from $sessionCounts
      |where rnk = 1
      """.stripMargin)
  }
}

/** Calculate top campaigns and channels engagement reports with SparkSQL */
object SparkSqlAggregateStatistics {
  def topCampaigns(dfAttributionProjection: DataFrame): DataFrame = {
    dfAttributionProjection
      .where(col(IsConfirmed) === true)
      .groupBy(col(CampaignId))
      .agg(
        sum(BillingCost).as("revenue")
      )
      .orderBy(col("revenue").desc)
      .limit(10)
  }

  def channelsEngagement(dfAttributionProjection: DataFrame): DataFrame = {
    val Rank = "rnk"
    val SessionCount = "session_count"

    val windowByCampaignOnSessionCount = Window.partitionBy(CampaignId).orderBy(col(SessionCount).desc)

    dfAttributionProjection
      .groupBy(
        col(CampaignId),
        col(ChannelId))
      .agg(
        count(SessionId).as(SessionCount)
      )
      .withColumn(
        Rank,
        rank().over(windowByCampaignOnSessionCount)
      )
      .where(col(Rank) === 1)
      .select(
        col(CampaignId),
        col(ChannelId)
      )
  }
}