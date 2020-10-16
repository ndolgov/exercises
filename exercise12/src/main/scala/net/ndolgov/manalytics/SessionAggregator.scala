package net.ndolgov.manalytics

import java.sql.Timestamp

import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql.{Encoder, Encoders}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/** A click stream events */
case class ClickEvent(
  userId: String,
  eventId: String,
  eventTime: Timestamp,
  eventType: String,
  attributes: Option[Map[String, String]]
)

/** A mutable PurchaseSession */
case class EventSession(
 var sessionId: String, // globally unique
 var userId: String,
 var start: Timestamp,
 var end: Timestamp,
 var campaignId: String,
 var channelId: String
)

/** A row in the final session table */
case class PurchaseSession(
  sessionId: String,
  userId: String,
  start: Timestamp,
  end: Timestamp,
  campaignId: String,
  channelId: String
)

/** A means of accumulating session history of a particular user */
case class SessionState(
  var current: EventSession, // session whose end event has not been found yet
  sessions: mutable.Buffer[PurchaseSession] // already detected sessions
)

/** A convenience wrapper for the Aggregator output */
case class SessionHistory(sessions: Seq[PurchaseSession])

/**
 * Assume session events arrive "in order" (i.e. | APP_OPENi | .. | APP_CLOSEi |)
 * On every APP_CLOSE event remember the current session as an immutable object in a collection
 * Generate a unique session id by concatenating user id with a per-user sequentially-generated number
 */
object SessionAggregator extends Aggregator[ClickEvent, SessionState, SessionHistory] {
  override def zero: SessionState = SessionState(emptySession(), ArrayBuffer())

  private def emptySession(): EventSession =
    EventSession(
      sessionId = null,
      userId = null,
      start = null,
      end = null,
      campaignId = null,
      channelId = null
    )

  override def reduce(state: SessionState, event: ClickEvent): SessionState = {
    event.eventType match {
      case AppOpen =>
        state.current.sessionId = event.userId + "_" + state.sessions.size.toString // UID + a sequentially generated suffix
        state.current.userId = event.userId
        state.current.start = event.eventTime
        state.current.campaignId = event.attributes.get("campaign_id")
        state.current.channelId = event.attributes.get("channel_id")

      case AppClose =>
        state.current.end = event.eventTime
        state.sessions.append(complete(state.current))

        state.current = emptySession()

      case _ =>
    }

    state
  }

  private def complete(state: EventSession): PurchaseSession = {
    PurchaseSession(
      sessionId = state.sessionId,
      userId = state.userId,
      start = state.start,
      end = state.end,
      campaignId = state.campaignId,
      channelId = state.channelId
    )
  }

  override def merge(left: SessionState, right: SessionState): SessionState = {
    val deduplicated = mutable.Set[PurchaseSession]()
    left.sessions.foreach(ps => deduplicated.add(ps))
    right.sessions.foreach(ps => deduplicated.add(ps))

    SessionState(
      current = emptySession(), // presumably two state instances are never merged when their "currents" are incomplete; it's not even clear how to do it otherwise
      sessions = deduplicated.toBuffer
    )
  }

  // optimistically assume perfect match between open/close events and no session is left unfinished
  override def finish(state: SessionState): SessionHistory = SessionHistory(state.sessions)

  override def bufferEncoder: Encoder[SessionState] = Encoders.product[SessionState]

  override def outputEncoder: Encoder[SessionHistory] = Encoders.product[SessionHistory]
}
