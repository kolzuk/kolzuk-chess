package common.core.utils

import common.core.model.Board._

object SquareOps {
  def fromSquare(square: String): Option[SquareIndex] = {
    val squareRegex = "^[a-h][1-8]$".r

    if (!squareRegex.matches(square)) {
      return None
    }

    val rank: SquareIndex = ((square(0) - 'a') * 8).shortValue
    val file: SquareIndex = square(1).toShort

    Some((rank + file).shortValue)
  }
}
