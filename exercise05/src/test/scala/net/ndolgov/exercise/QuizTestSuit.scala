package net.ndolgov.exercise

import java.io.File

import org.scalatest.{Assertions, FlatSpec}

import scala.io.{BufferedSource, Source}

final class QuizTestSuit extends FlatSpec with Assertions {
  "A file with one error" should "increment error counter once" in {
    val counters: Array[String] = report("target/classes/testfile1.log")

    assert(counters.length == 1)
    for (i <- counters.indices ) {
    }
    assert(counters(0).equals("0,8,1"))
  }

  "A file with multiple error" should "increment error counters for every shard" in {
    val counters: Array[String] = report("target/classes/testfile2.log")

    assert(counters.length == 3)
    for (i <- counters.indices ) {
    }
    assert(counters(0).equals("1,8,1"))
    assert(counters(1).equals("9,8,2"))
    assert(counters(2).equals("23,7,1"))
  }

  private def report(path : String): Array[String] = {
    val aggregator = new ErrorAggregator
    val file: BufferedSource = Source.fromFile(new File(path))

    try {
      for (line <- file.getLines()) {
        aggregator.onLogLine(line)
      }
    } finally {
      file.close()
    }

    aggregator.aggregatedCounters().toArray[String]
  }
}