package common.core.utils

import common.core.model
import common.core.model.{Black, Board, Color, Figure, White}
import common.core.model.Board._
import common.core.model._

/** Forsyth–Edwards Notation (FEN) - is a standard notation for describing a particular
 * board position of a chess game.
 *
 * Example of starting position:
 *
 * {@code rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1}
 *
 * https://en.wikipedia.org/wiki/Forsyth–Edwards_Notation
 */
object ForsythEdwardsNotationUtils {
  // TODO
  def fromBoard(board: Board): String = {
    ???
  }

  def toBoard(fen: String): Option[Board] = {
    val data = fen.split("/| ").toVector

    if (data.length != 13)
      return None

    for {
      figurePositions <- parseFigurePosition(data.take(8))
      activeColor <- parseActiveColor(data(8))
      castlingAvailability <- parseCastlingAvailability(data(9))
      enPassantTargetSquare = parseEnPassantTargetSquare(data(10))
      halfMoveClock <- parseHalfMoveClock(data(11))
      fullMoveNumber <- parseFullMoveNumber(data(12))
    } yield model.Board(figurePositions, activeColor, castlingAvailability, enPassantTargetSquare, halfMoveClock, fullMoveNumber)
  }

  /**
   *
   * @param figurePositions
   * 8-length array. Each line representing rank.
   * @return
   * Option which may contain array of 8-length with corresponding squares.
   */
  private def parseFigurePosition(figurePositions: Vector[String]): Option[Vector[Square]] = {
    def parseLine(line: String): Option[List[Square]] = {
      var lineSquares: List[Square] = List.empty
      for (c <- line) {
        if (c.isDigit) {
          for (_ <- 0 until c.asDigit)
            lineSquares = None :: lineSquares
        } else {
          Figure.getPieceByChar(c) match {
            case Some(figure) => lineSquares = Some(figure) :: lineSquares
            case None         => return None
          }
        }
      }

      if (lineSquares.length != 8)
        return None

      Some(lineSquares)
    }

    var squares: List[Square] = List.empty
    for (rank <- 0 until 8) {
      val row = figurePositions(rank)

      squares = parseLine(row) match {
        case Some(lineSquares) => squares ::: lineSquares
        case None              => return None
      }
    }

    Some(squares.reverse.toVector)
  }

  private def parseActiveColor(activeColor: String): Option[Color] = activeColor match {
    case "w" => Some(White)
    case "b" => Some(Black)
    case _   => None
  }

  private def parseCastlingAvailability(castlingAvailability: String): Option[CastlingAvailability] = {
    val castlingAvailabilityRegex = "^[KQkq]{1,4}|-$".r

    if (!castlingAvailabilityRegex.matches(castlingAvailability))
      return None

    val K: Boolean = castlingAvailability.contains('K')
    val Q: Boolean = castlingAvailability.contains('Q')
    val k: Boolean = castlingAvailability.contains('k')
    val q: Boolean = castlingAvailability.contains('q')

    Some(CastlingAvailability(K, Q, k, q))
  }

  private def parseEnPassantTargetSquare(enPassantTargetSquare: String): Option[SquareIndex] =
    SquareOps.fromSquare(enPassantTargetSquare)

  private def parseHalfMoveClock(halfMoveClock: String): Option[Short] = halfMoveClock.toShortOption

  private def parseFullMoveNumber(fullMoveNumber: String): Option[Short] = fullMoveNumber.toShortOption

}
