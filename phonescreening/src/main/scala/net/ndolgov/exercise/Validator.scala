import scala.collection.mutable

object Validator {
  def validate(lines: Array[Option[Any]]*): Boolean = {
    try {
      val rows: Array[Array[Option[Long]]] = lines.
        map((row: Array[Option[Any]]) => row.map((maybe: Option[Any]) => maybe.map(cell => cell.asInstanceOf[Long]))).
        toArray

      doValidate(rows, 9)

      for (i <- Seq(0, 3, 6)) {
        for (j <- Seq(0, 3, 6)) {
          val sub = rows.slice(i, i + 3).map(row => row.slice(j, j + 3))
          doValidate(sub, 3)
        }
      }

      true
    } catch {
      case _: Exception => false
    }
  }

  def doValidate(m: Array[Array[Option[Long]]], size: Int): Unit = {
      if (m.length != size) {
        throw new IllegalArgumentException(s"${m.length} != $size")
      }
      m.foreach(row => if (row.length != size) {
        throw new IllegalArgumentException(s"${row.length} != $size")
      })

      m.foreach(isValid)
      m.transpose.foreach(isValid)
  }

  private def isValid(array: Array[Option[Long]]): Boolean = {
    val seenSoFar = mutable.Set[Long]()

    array.map(maybeCell =>
      maybeCell.map(cell => {
        if (seenSoFar.contains(cell)) {
          throw new IllegalArgumentException(s"$cell is duplicated in ${array.mkString("|")}")
        }

        seenSoFar += cell
      })
    )

    true
  }
}