package net.ndolgov.manalytics

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, concat, explode, lit, sum, when}
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.{DataFrame, Encoders, SparkSession}

/**
 * Build purchases attribution projection.
 * The approach to session detection with summing is inspired by https://mode.com/blog/finding-user-sessions-sql/
 */
object PurchaseAttribution {
  private val Sessions = "sessions"

  def attributeWithSparkSql(csvRoot: String, spark: SparkSession): DataFrame = {
    val dfClickstream = CsvLoader.loadClickStream(csvRoot, spark)
    val dfSessions = detectSessions(dfClickstream)
    apply(csvRoot, spark, dfClickstream, dfSessions)
  }

  def attributeWithCustomAggregator(csvRoot: String, spark: SparkSession): DataFrame = {
    val dfClickstream = CsvLoader.loadClickStream(csvRoot, spark)

    val dfSessions = dfClickstream.as[ClickEvent](Encoders.product[ClickEvent])
      .groupByKey(_.userId)(Encoders.STRING)
      .agg(SessionAggregator.toColumn.name(Sessions))
      .map({case (_, sessionHistory) => sessionHistory})(Encoders.product[SessionHistory])
      .toDF()
      .withColumn(Sessions, explode(col(Sessions)))
      .select(
        s"$Sessions.$SessionId",
        s"$Sessions.$UserId",
        s"$Sessions.$Start",
        s"$Sessions.$End",
        s"$Sessions.$CampaignId",
        s"$Sessions.$ChannelId")

    apply(csvRoot, spark, dfClickstream, dfSessions)
  }

  private def apply(csvRoot: String, spark: SparkSession, dfClickstream: DataFrame, dfSessions: DataFrame): DataFrame = {
    val dfSessionizedPurchaseEvents = assignSessionToPurchaseEvents(dfClickstream, dfSessions)

    val dfPurchases = CsvLoader.loadPurchases(csvRoot, spark)

    val dfAttributionProjection = dfSessionizedPurchaseEvents.join(
      dfPurchases,
      dfPurchases(PurchaseId) === dfSessionizedPurchaseEvents(PurchaseId),
      "left")

    dfAttributionProjection.select(
      dfPurchases(PurchaseId),
      dfPurchases(PurchaseTime),
      dfPurchases(BillingCost),
      dfPurchases(IsConfirmed),
      dfSessionizedPurchaseEvents(SessionId),
      dfSessionizedPurchaseEvents(CampaignId),
      dfSessionizedPurchaseEvents(ChannelId)
    )
  }

  /**
   * Bucket purchase events by session
   *
   * @param dfClickstream clickstream events with json attributes parsed
   * @param dfSessions sessions inferred from the clickstream
   * @return purchase events augmented with session data
   */
  private def assignSessionToPurchaseEvents(dfClickstream: DataFrame, dfSessions: DataFrame): DataFrame = {
    val dfPurchaseEvents = dfClickstream
      .select(
        col(UserId),
        col(EventTime),
        col(s"$Attributes.purchase_id").as(PurchaseId)
      )
      .where(col(EventType) === "purchase")

    val dfSessionizedPurchaseEvents = dfPurchaseEvents
      .join(
        dfSessions,
        dfPurchaseEvents(UserId) === dfSessions(UserId) &&
        dfPurchaseEvents(EventTime) >= dfSessions(Start) &&
        dfPurchaseEvents(EventTime) < dfSessions(End),
        "left")

    dfSessionizedPurchaseEvents
  }

  /**
   * Create a session table by joining start and stop session events.
   * Use user session ids to generate globally unique session ids.
   * @param dfClickstream cleaned up click stream data with json attributes parsed
   * @return session table inferred from the click stream
   */
  private def detectSessions(dfClickstream: DataFrame): DataFrame = {
    val dfWithUserSessionIds = assignUserSessionIds(dfClickstream)

    val dfStarts = dfWithUserSessionIds
      .select(
        col(UserId),
        col(EventTime).as(Start),
        col(CampaignId),
        col(ChannelId),
        col(IsNewSession),
        col(UserSessionId)
      )
      .where(col(IsNewSession) === 1)

    val dfEnds = dfWithUserSessionIds
      .select(
        col(UserId),
        col(EventTime).as(End),
        col(IsNewSession),
        col(UserSessionId)
      )
      .where(col(IsNewSession) === 0)

    dfStarts
      .join(dfEnds, dfStarts(UserId) === dfEnds(UserId) && dfStarts(UserSessionId) === dfEnds(UserSessionId), "inner")
      .drop(IsNewSession)
      .drop(dfEnds(UserId))
      .drop(dfEnds(UserSessionId))
      .withColumn(
        SessionId,
        concat(col(UserId), lit("_"), col(UserSessionId)) // just a unique string
      )
      .drop(UserSessionId)
  }

  /**
   * Assign sequential user (not global!) session ids to app_open/app_close events from the click stream.
   * The trick is to count every begin session event as 1 and sum them up by user to generate a sequential counter.
   * @param dfClickstream all events
   * @return filtered click stream with auxiliary columns attached and nested fields extracted to the top level
   */
  private def assignUserSessionIds(dfClickstream: DataFrame): DataFrame = {
    val windowByUserOnTime = Window.partitionBy(UserId).orderBy(EventTime)

    dfClickstream
      .withColumn(
        IsNewSession,
        when(col(EventType) === AppOpen, 1).otherwise(0)
      )
      .select(
        col(UserId),
        col(EventTime),
        col(s"$Attributes.campaign_id").as(CampaignId),
        col(s"$Attributes.channel_id").as(ChannelId),
        col(IsNewSession)
      )
      .where(col(EventType) === AppOpen || col(EventType) === AppClose)
      .withColumn(
        UserSessionId,
        sum(IsNewSession).over(windowByUserOnTime).cast(DataTypes.LongType)
      )
  }
}