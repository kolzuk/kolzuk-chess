package common.core.utils

import cats.implicits.toShow
import common.core.model
import common.core.model.Board._
import common.core.model._

/** Forsyth–Edwards Notation (FEN) - is a standard notation for describing a particular
 * board position of a chess game.
 *
 * Example of starting position:
 *
 * {@code rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1}
 *
 * @see <a href=https://en.wikipedia.org/wiki/Forsyth–Edwards_Notation>Forsyth–Edwards Notation</a>
 */
object ForsythEdwardsNotationUtils {
  def fromBoard(board: Board): String = {
    val lines = new Array[String](8)

    for (rank <- 0 until 8) {
      val line: StringBuilder = new StringBuilder
      var empty = 0
      for (file <- 0 until 8) {
        board(rank * 8 + file) match {
          case Some(figure) =>
            if (empty != 0) {
              line.append(empty)
              empty = 0
            }
            line.append(figure.charRepresentation)
          case None         => empty += 1
        }
      }
      if (empty != 0)
        line.append(empty)

      lines(rank) = line.toString()
    }

    val piecePlacement: String = lines.reverse.mkString("/")
    val color: Char = board.activeColor.char
    val ca: String = board.castlingAvailability.show
    val halfMoveClock: Int = board.halfMoveClock
    val enPassantTargetSquare: String = board.enPassantTargetSquare
      .map(SquareOps.toString)
      .getOrElse("-")
    val fullMoveNumber: Int = board.fullMoveNumber

    s"$piecePlacement $color $ca $enPassantTargetSquare $halfMoveClock $fullMoveNumber"
  }

  def toBoard(fen: String): Option[Board] = {
    val data = fen.split("/| ").toVector

    if (data.length != 13)
      return None

    for {
      figurePositions <- parseFigurePosition(data.take(8))
      activeColor = Color(data(8))
      castlingAvailability = CastlingAvailability(data(9))
      enPassantTargetSquare = parseEnPassantTargetSquare(data(10))
      halfMoveClock <- parseHalfMoveClock(data(11))
      fullMoveNumber <- parseFullMoveNumber(data(12))
    } yield model.Board(figurePositions, activeColor, castlingAvailability, enPassantTargetSquare, halfMoveClock, fullMoveNumber)
  }

  /**
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

  private def parseEnPassantTargetSquare(enPassantTargetSquare: String): Option[SquareIndex] =
    SquareOps.fromSquare(enPassantTargetSquare)

  private def parseHalfMoveClock(halfMoveClock: String): Option[Short] = halfMoveClock.toShortOption

  private def parseFullMoveNumber(fullMoveNumber: String): Option[Short] = fullMoveNumber.toShortOption

}
