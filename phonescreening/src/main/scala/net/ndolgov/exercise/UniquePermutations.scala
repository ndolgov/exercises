package net.ndolgov.zlabs

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map
import scala.collection.mutable.HashSet
import scala.collection.mutable.Set
import scala.collection.mutable.ArrayBuffer
import scala.util.Sorting.quickSort

object UniquePermutations {
  def main(args: Array[String]){
    merge(
      ArrayBuffer(
        ArrayBuffer("ab", "ba", "aa"),
        ArrayBuffer("ac", "ca", "aa"),
        ArrayBuffer("ad", "da", "aa")),
      (set : Set[String]) => println(set.mkString("\n")))
 	}

  private def merge(columns : Seq[Seq[String]], listener: Set[String] => Unit): Unit = {
    if (columns.isEmpty)
      return

    val groupedByCanonicalOrder: Seq[Map[String, Set[String]]] = columns.map(groupByCanonicalOrder)
    for (map <- groupedByCanonicalOrder) {
      for (key <- map.keysIterator) {
        listener(mergeAndRemove(key, groupedByCanonicalOrder))
      }
    }
  }

  private def mergeAndRemove(key : String, groups : Seq[Map[String, Set[String]]]): Set[String] = {
    val merged : Set[String] = new HashSet[String]

    groups.foreach((group : Map[String, Set[String]]) => {
      group.remove(key) match {
         case Some(set) => merged ++= set
         case None => ()
      }
    })

    merged
  }

  private def groupByCanonicalOrder(column : Seq[String]): Map[String, Set[String]] = {
    val canonicalToPermutations = new HashMap[String, Set[String]]

    def insertOrUpdate(str : String): Unit = {
      val canonical = canonicalOrder(str)
      val knownPermutations = canonicalToPermutations.getOrElseUpdate(canonical, new HashSet[String])
      if (knownPermutations.isEmpty) {
        knownPermutations += str
      }
    }

    column.foreach(insertOrUpdate)

    canonicalToPermutations
  }

  private def canonicalOrder(str : String) : String = {
    val sorted = str.getBytes
    quickSort(sorted)
    new String(sorted)
  }
}