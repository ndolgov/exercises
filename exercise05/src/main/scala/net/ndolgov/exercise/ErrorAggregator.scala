package net.ndolgov.exercise

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Aggregate error counters from a daily log (so expect at most 24 hours in one file).
  * Assume the 'ERROR' and 'shard' keywords are always in the same case. Otherwise (?i)-style magic should be used.
  * see http://regexr.com/ for interactive regexp debugging
  */
final class ErrorAggregator() {
  private val shardToCountByHour = new Array[mutable.Map[Int,Int]](24)

  def onLogLine(line : String) : Unit = {
    line match {
      case ErrorAggregator.REGEXP(hr, shard) => inc(hr.toInt, shard.toInt)
      case _ =>
    }
  }

  def aggregatedCounters() : Iterator[String] = {
    val report = new ArrayBuffer[String]

    for (hour <- 0 to 23 ) {
      val byHour = shardToCountByHour(hour)

      if (byHour != null) {
        for (shard <- collection.immutable.SortedSet[Int]() ++ byHour.keySet) {
          report.append("%d,%d,%d".format(hour, shard, byHour(shard)))
        }
      }
    }

    report.toIterator
  }

  private def inc(hour : Int, shard : Int): Unit = {
    val byHour = shardToCount(hour)
    byHour.put(shard, byHour.getOrElse(shard, 0) + 1)
  }

  private def shardToCount(hour : Int) : mutable.Map[Int, Int] = {
    val existing = shardToCountByHour(hour)

    if (existing == null) {
      val created = new mutable.HashMap[Int, Int]()
      shardToCountByHour(hour) = created
      return created
    }

    existing
  }

  private object ErrorAggregator {
    // for log messages with ERROR level extract two groups: the hour (from the first datetime value) and the shard index value
    val REGEXP = """\w+ \d\d (\d\d):\d\d:\d\d [\w\.-]+ \w+ \d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d,\d\d\d \[[-\w]+\] ERROR \w+ - shard(\d+) .+""".r
  }
}