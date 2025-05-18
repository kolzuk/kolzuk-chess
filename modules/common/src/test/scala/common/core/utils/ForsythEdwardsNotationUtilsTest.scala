package common.core.utils

import common.core.model.Board._
import common.core.model._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._

class ForsythEdwardsNotationUtilsTest extends AnyFunSuite {

  test("An ForsythEdwardsNotation should work correct for the starting position") {
    val fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    val figures: Vector[Square] = Vector[Square](
      Some(Rook(White)), Some(Knight(White)), Some(Bishop(White)), Some(Queen(White)), Some(King(White)), Some(Bishop(White)), Some(Knight(White)), Some(Rook(White)),
      Some(Pawn(White)), Some(Pawn(White)), Some(Pawn(White)), Some(Pawn(White)), Some(Pawn(White)), Some(Pawn(White)), Some(Pawn(White)), Some(Pawn(White)),
      None, None, None, None, None, None, None, None,
      None, None, None, None, None, None, None, None,
      None, None, None, None, None, None, None, None,
      None, None, None, None, None, None, None, None,
      Some(Pawn(Black)), Some(Pawn(Black)), Some(Pawn(Black)), Some(Pawn(Black)), Some(Pawn(Black)), Some(Pawn(Black)), Some(Pawn(Black)), Some(Pawn(Black)),
      Some(Rook(Black)), Some(Knight(Black)), Some(Bishop(Black)), Some(Queen(Black)), Some(King(Black)), Some(Bishop(Black)), Some(Knight(Black)), Some(Rook(Black)),
    )
    val activeColor: Color = White
    val castlingAvailability: CastlingAvailability = CastlingAvailability()
    val enPassantTargetSquare: Option[Int] = None
    val halfMoveClock: Int = 0
    val fullMoveNumber: Int = 1
    val expectedResult = Board(
      figures,
      activeColor,
      castlingAvailability,
      enPassantTargetSquare,
      halfMoveClock,
      fullMoveNumber
    )

    val result = ForsythEdwardsNotationUtils.toBoard(fen).get
    result shouldBe expectedResult
  }

  test("An ForsythEdwardsNotation should work correct after e2-e4 move") {
    val fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1"
    val figures: Vector[Square] = Vector[Square](
      Some(Rook(White)), Some(Knight(White)), Some(Bishop(White)), Some(Queen(White)), Some(King(White)), Some(Bishop(White)), Some(Knight(White)), Some(Rook(White)),
      Some(Pawn(White)), Some(Pawn(White)), Some(Pawn(White)), Some(Pawn(White)), None, Some(Pawn(White)), Some(Pawn(White)), Some(Pawn(White)),
      None, None, None, None, None, None, None, None,
      None, None, None, None, Some(Pawn(White)), None, None, None,
      None, None, None, None, None, None, None, None,
      None, None, None, None, None, None, None, None,
      Some(Pawn(Black)), Some(Pawn(Black)), Some(Pawn(Black)), Some(Pawn(Black)), Some(Pawn(Black)), Some(Pawn(Black)), Some(Pawn(Black)), Some(Pawn(Black)),
      Some(Rook(Black)), Some(Knight(Black)), Some(Bishop(Black)), Some(Queen(Black)), Some(King(Black)), Some(Bishop(Black)), Some(Knight(Black)), Some(Rook(Black)),
    )
    val activeColor: Color = Black
    val castlingAvailability: CastlingAvailability = CastlingAvailability()
    val enPassantTargetSquare: Option[Int] = None
    val halfMoveClock: Short = 0
    val fullMoveNumber: Short = 1
    val expectedResult = Board(
      figures,
      activeColor,
      castlingAvailability,
      enPassantTargetSquare,
      halfMoveClock,
      fullMoveNumber
    )

    val result = ForsythEdwardsNotationUtils.toBoard(fen).get
    result shouldBe expectedResult
  }
}