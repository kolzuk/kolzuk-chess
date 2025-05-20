package common.core.utils

import common.core.model.Board._
import common.core.model._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._

class ForsythEdwardsNotationUtilsTest extends AnyFunSuite {
  import Color._

  test("ForsythEdwardsNotation should work correct for the starting position") {
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

  test("ForsythEdwardsNotation should work correct after e2-e4 move") {
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

  test("ForsythEdwardsNotation should be the same after .toBoard and .toBoard") {
    val fens = Seq(
      "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
      "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 0",
      "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1",
      "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",
      "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8",
      "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10"
    )

    val boards = fens.map(ForsythEdwardsNotationUtils.toBoard)

    boards.forall(_.nonEmpty) shouldBe true

    boards
      .map(_.get)
      .map(ForsythEdwardsNotationUtils.fromBoard) should contain theSameElementsAs fens
  }
}