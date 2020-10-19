package net.ndolgov.manalytics

import org.apache.spark.sql.{DataFrame, SparkSession}

/** Plain SQL implementation of purchase attribution */
object PlainSqlPurchaseAttribution {
  /**
    * Assume "clickstream" and "purchases" tables are available from the provided spark session
    * @return |campaignId| channelId|
    */
  def channelsEngagement(spark: SparkSession): DataFrame = {
    spark.sql(s"""
      |with clickstream_with_sessions as
      |(
      |  with clickstream_tmp as
      |  (
      |    select
      |      userId, eventTime, campaignId, channelId,
      |      case when eventType='app_open' then 1 else 0 end as isNewSession
      |    from clickstream
      |    where eventType = 'app_open' or eventType = 'app_close'
      |  )
      |  select
      |    userId, eventTime, campaignId, channelId, isNewSession,
      |    sum(isNewSession) over (partition by userId order by eventTime) as userSessionId
      |  from clickstream_tmp
      |),
      |sessions_starts as
      |(
      |  select
      |    userId, eventTime as session_start, campaignId, channelId, isNewSession, userSessionId
      |  from clickstream_with_sessions
      |  where isNewSession=1
      |),
      |sessions_ends as
      |(
      |  select
      |    userId, eventTime as session_end, campaignId, channelId, isNewSession, userSessionId
      |  from clickstream_with_sessions
      |  where isNewSession=0
      |),
      |sessions as
      |(
      |  select
      |    starts.userId, starts.session_start as start, ends.session_end as end, starts.campaignId, starts.channelId,
      |    concat(starts.userId,'_',starts.userSessionId) as sessionId
      |  from sessions_starts starts join sessions_ends ends
      |  where starts.userId = ends.userId and starts.userSessionId = ends.userSessionId
      |),
      |attribution_projection as
      |(
      |  select
      |    clickstream.purchaseId, purchases.purchaseTime, purchases.billingCost, purchases.isConfirmed, sessions.sessionId, sessions.campaignId, sessions.channelId
      |  from clickstream left outer join sessions left outer join purchases
      |  where
      |    clickstream.eventType = 'purchase' and clickstream.userId = sessions.userId and clickstream.eventTime >= sessions.start and clickstream.eventTime < sessions.end
      |    and purchases.purchaseId = clickstream.purchaseId
      |),
      |session_counts as
      |(
      |  select
      |    campaignId,
      |    channelId,
      |    count(sessionId) as session_count,
      |    rank() over (partition by campaignId order by count(sessionId) desc) as rnk
      |  from attribution_projection
      |  group by campaignId, channelId
      |)
      |select
      |  campaignId,
      |  channelId
      |from session_counts
      |where rnk = 1
      """.stripMargin)
  }

  /**
    * Assume "clickstream" and "purchases" tables are available from the provided spark session
    *
    * @return |purchaseId|purchaseTime|billingCost|isConfirmed|sessionId|campaignId| channelId|
    */
  def attributeWithPlainSql(spark: SparkSession): DataFrame = {
    spark.sql(s"""
      |with clickstream_with_sessions as
      |(
      |  with clickstream_tmp as
      |  (
      |    select
      |      userId, eventTime, campaignId, channelId,
      |      case when eventType='app_open' then 1 else 0 end as isNewSession
      |    from clickstream
      |    where eventType = 'app_open' or eventType = 'app_close'
      |  )
      |  select
      |    userId, eventTime, campaignId, channelId, isNewSession,
      |    sum(isNewSession) over (partition by userId order by eventTime) as userSessionId
      |  from clickstream_tmp
      |),
      |sessions_starts as
      |(
      |  select
      |    userId, eventTime as session_start, campaignId, channelId, isNewSession, userSessionId
      |  from clickstream_with_sessions
      |  where isNewSession=1
      |),
      |sessions_ends as
      |(
      |  select
      |    userId, eventTime as session_end, campaignId, channelId, isNewSession, userSessionId
      |  from clickstream_with_sessions
      |  where isNewSession=0
      |),
      |sessions as (
      |  select
      |    starts.userId, starts.session_start as start, ends.session_end as end, starts.campaignId, starts.channelId,
      |    concat(starts.userId,'_',starts.userSessionId) as sessionId
      |  from sessions_starts starts join sessions_ends ends
      |  where starts.userId = ends.userId and starts.userSessionId = ends.userSessionId
      |)
      |select
      |  clickstream.purchaseId, purchases.purchaseTime, purchases.billingCost, purchases.isConfirmed, sessions.sessionId, sessions.campaignId, sessions.channelId
      |from clickstream left outer join sessions left outer join purchases
      |where
      |  clickstream.eventType = 'purchase' and clickstream.userId = sessions.userId and clickstream.eventTime >= sessions.start and clickstream.eventTime < sessions.end
      |  and purchases.purchaseId = clickstream.purchaseId
    """.stripMargin)
  }
}
