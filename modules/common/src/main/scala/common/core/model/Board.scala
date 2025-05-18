package common.core.model

import Board._

/**
 * Immutable class that represents a chess game board in exact 1:1 corresponds with FEN notation.
 * Each field directly maps to a FEN field.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Forsyth–Edwards_Notation">Forsyth–Edwards Notation (FEN)</a>
 */
case class Board(
  boardRepresentation: Vector[Square],
  activeColor: Color,
  castlingAvailability: CastlingAvailability,
  enPassantTargetSquare: Option[Int],
  halfMoveClock: Int,
  fullMoveNumber: Int
) {
  private def makeNormalMove(move: Move): Board = {
    val movingFigure: Figure = boardRepresentation(move.from)
      .getOrElse(throw new IllegalStateException(s"move.from cell doesn't have a figure"))
    val capturedFigureOpt: Square = boardRepresentation(move.to)

    val newBoard = boardRepresentation
      .updated(move.from, None)
      .updated(move.to, Some(movingFigure))

    val newCastlingAvailability: CastlingAvailability = {
      var whiteCanCastleKingSide: Boolean = castlingAvailability.whiteCanCastleKingSide
      var whiteCanCastleQueenSide: Boolean = castlingAvailability.whiteCanCastleQueenSide
      var blackCanCastleKingSide: Boolean = castlingAvailability.blackCanCastleKingSide
      var blackCanCastleQueenSide: Boolean = castlingAvailability.blackCanCastleQueenSide

      movingFigure match {
        case King(White) =>
          whiteCanCastleKingSide = false
          whiteCanCastleQueenSide = false
        case Rook(White) =>
          if (whiteCanCastleKingSide && move.from == 7) whiteCanCastleKingSide = false
          if (whiteCanCastleQueenSide && move.from == 0) whiteCanCastleQueenSide = false
        case King(Black) =>
          blackCanCastleKingSide = false
          blackCanCastleQueenSide = false
        case Rook(Black) =>
          if (blackCanCastleKingSide && move.from == 63) blackCanCastleKingSide = false
          if (blackCanCastleQueenSide && move.from == 56) blackCanCastleQueenSide = false
        case _ =>
      }

      capturedFigureOpt match {
        case Some(figure) =>
          figure match {
            case Rook(White) =>
              if (whiteCanCastleKingSide && move.to == 7) whiteCanCastleKingSide = false
              if (whiteCanCastleQueenSide && move.to == 0) whiteCanCastleQueenSide = false
            case Rook(Black) =>
              if (blackCanCastleKingSide && move.to == 63) blackCanCastleKingSide = false
              if (blackCanCastleQueenSide && move.to == 56) blackCanCastleQueenSide = false
            case _ => ()
          }
        case _ => ()
      }

      CastlingAvailability(whiteCanCastleKingSide, whiteCanCastleQueenSide, blackCanCastleKingSide, blackCanCastleQueenSide)
    }

    val newEnPassantTargetSquare: Option[Int] = movingFigure match {
      case Pawn(White) if move.to == move.from + 16 => Some(move.from + 8)
      case Pawn(Black) if move.to == move.from - 16 => Some(move.from - 8)
      case _ => None
    }

    val newHalfMoveClock = if (capturedFigureOpt.nonEmpty
      || movingFigure == Pawn(White)
      || movingFigure == Pawn(Black)) 0 else halfMoveClock + 1

    activeColor match {
      case White => copy(newBoard, Black, newCastlingAvailability,
        newEnPassantTargetSquare, newHalfMoveClock, fullMoveNumber)
      case Black => copy(newBoard, White, newCastlingAvailability,
        newEnPassantTargetSquare, newHalfMoveClock, fullMoveNumber + 1)
    }
  }

  private def makeEnPassantMove(move: Move): Board = {
    val capturedPawnSquare = if (activeColor == White) move.to - 8 else move.to + 8
    val newBoard = boardRepresentation
      .updated(move.from, None)
      .updated(move.to, Some(Pawn(activeColor)))
      .updated(capturedPawnSquare, None)

    activeColor match {
      case White => copy(newBoard, Black, castlingAvailability, None, 0, fullMoveNumber)
      case Black => copy(newBoard, White, castlingAvailability, None, 0, fullMoveNumber + 1)
    }
  }

  private def makeCastleMove(castleType: CastleType): Board = castleType match {
    case CastleType.WhiteKingSide =>
      val newBoard: Vector[Square] = boardRepresentation
        .updated(4, None)
        .updated(7, None)
        .updated(6, Some(King(White)))
        .updated(5, Some(Rook(White)))

      copy(newBoard, Black, castlingAvailability.withoutWhiteCastle, None, halfMoveClock + 1, fullMoveNumber)
    case CastleType.WhiteQueenSide =>
      val newBoard: Vector[Square] = boardRepresentation
        .updated(4, None)
        .updated(0, None)
        .updated(2, Some(King(White)))
        .updated(3, Some(Rook(White)))

      copy(newBoard, Black, castlingAvailability.withoutWhiteCastle, None, halfMoveClock + 1, fullMoveNumber)
    case CastleType.BlackKingSide =>
      val newBoard: Vector[Square] = boardRepresentation
        .updated(60, None)
        .updated(63, None)
        .updated(62, Some(King(Black)))
        .updated(61, Some(Rook(Black)))

      copy(newBoard, White, castlingAvailability.withoutBlackCastle, None, halfMoveClock + 1, fullMoveNumber + 1)
    case CastleType.BlackQueenSide =>
      val newBoard: Vector[Square] = boardRepresentation
        .updated(60, None)
        .updated(56, None)
        .updated(58, Some(King(Black)))
        .updated(59, Some(Rook(Black)))

      copy(newBoard, White, castlingAvailability.withoutBlackCastle, None, halfMoveClock + 1, fullMoveNumber + 1)
  }

  private def makePromotionMove(move: Move, promotion: Figure): Board = {
    val newBoard = boardRepresentation
      .updated(move.from, None)
      .updated(move.to, Some(promotion))

    val newCastlingAvailability: CastlingAvailability = move.to match {
      case 7 => castlingAvailability.copy(whiteCanCastleKingSide = false)
      case 0 => castlingAvailability.copy(whiteCanCastleQueenSide = false)
      case 56 => castlingAvailability.copy(blackCanCastleKingSide = false)
      case 63 => castlingAvailability.copy(blackCanCastleQueenSide = false)
      case _ => castlingAvailability
    }

    activeColor match {
      case White => copy(newBoard, Black, newCastlingAvailability, None, 0, fullMoveNumber)
      case Black => copy(newBoard, White, newCastlingAvailability, None, 0, fullMoveNumber + 1)
    }
  }

  def makeMove(move: Move): Board = move.moveType match {
    case MoveType.Normal => makeNormalMove(move)
    case MoveType.EnPassant => makeEnPassantMove(move)
    case MoveType.Castle(castleType: CastleType) => makeCastleMove(castleType)
    case MoveType.Promotion(promotion: Figure) => makePromotionMove(move, promotion)
  }

  def apply(sq: Int): Square = boardRepresentation.apply(sq)
}

object Board {

  type SquareIndex = Int
  type Square = Option[Figure]
}
