package common.core.model

import cats.Show

final case class Move(
  from: Int,
  to: Int,
  moveType: MoveType
)

object Move {
  import MoveType._
  import CastleType._

  implicit val showMove: Show[Move] = Show.show { move =>
    val from = s"${('a' + (move.from % 8)).toChar}${move.from / 8 + 1}"
    val to   = s"${('a' + (move.to % 8)).toChar}${move.to / 8 + 1}"
    s"$from$to"
  }

  def parseFromUCI(uci: String, board: Board): Move = {
    require(uci.length == 4 || uci.length == 5, s"Wrong format of UCI: \"$uci\"")
    val from = ((uci(1) - '0') - 1) * 8 + (uci(0) - 'a')
    val movingFigure = board(from)
      .getOrElse(throw new IllegalArgumentException("\"from\" square is empty"))
    val activeColor = board.activeColor
    val to   = ((uci(3) - '0') - 1) * 8 + (uci(2) - 'a')
    val promotion: Option[Char] = if (uci.length == 5) Some(uci(4)) else None

    if (promotion.nonEmpty) {
      val c = promotion.get.toLower
      if (c == 'q') return Move(from, to, Promotion(Queen(activeColor)))
      if (c == 'n') return Move(from, to, Promotion(Knight(activeColor)))
      if (c == 'r') return Move(from, to, Promotion(Rook(activeColor)))
      if (c == 'b') return Move(from, to, Promotion(Bishop(activeColor)))

      throw new IllegalArgumentException(s"Wrong format of UCI: \"$uci\"")
    }

    board.enPassantTargetSquare
      .foreach(sq => {
        if (sq == to && (movingFigure == Pawn(White) || movingFigure == Pawn(Black)))
          return Move(from, to, EnPassant)
      })

    if (movingFigure == King(White) || movingFigure == King(Black)) {
      (from, to) match {
        case (4, 6) => return Move(from, to, Castle(WhiteKingSide))
        case (4, 2) => return Move(from, to, Castle(WhiteQueenSide))
        case (60, 62) => return Move(from, to, Castle(BlackKingSide))
        case (60, 58) => return Move(from, to, Castle(BlackQueenSide))
        case _ => ()
      }
    }

    Move(from, to, Normal)
  }
}

sealed trait MoveType

object MoveType {
  case object Normal extends MoveType
  case object EnPassant extends MoveType
  case class Castle(castleType: CastleType) extends MoveType
  case class Promotion(promotion: Figure) extends MoveType
}

sealed trait CastleType

object CastleType {
  case object WhiteKingSide extends CastleType
  case object WhiteQueenSide extends CastleType
  case object BlackKingSide extends CastleType
  case object BlackQueenSide extends CastleType
}
