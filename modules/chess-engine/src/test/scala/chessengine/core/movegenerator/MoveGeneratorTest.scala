package chessengine.core.movegenerator

import common.core.utils.ForsythEdwardsNotationUtils
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._

/** Будет тестироваться с помощью функции `perft`.
 *
 * @see <a href=https://www.chessprogramming.org/Perft_Results>Perft Results</a>
 */
class MoveGeneratorTest extends AnyFunSuite {
  import chessengine.core.movegenerator.MoveGenerator._

  implicit val moveGenerator: MoveGenerator = new MoveGeneratorLive

  /**
   * @see https://www.chessprogramming.org/Perft_Results#Position_1
   */
  test("MoveGenerator should work correct with initial position") {
    val board = ForsythEdwardsNotationUtils.toBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1").get

    perft(1, board) shouldBe 20
    perft(2, board) shouldBe 400
    perft(3, board) shouldBe 8902
    perft(4, board) shouldBe 197_281
    perft(5, board) shouldBe 4_865_609
    perft(6, board) shouldBe 119_060_324
  }

  /**
   * @see https://www.chessprogramming.org/Perft_Results#Position_2
   */
  test("MoveGenerator should work correct with Kiwipete position") {
    val board = ForsythEdwardsNotationUtils.toBoard("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 0").get

    perft(1, board) shouldBe 48
    perft(2, board) shouldBe 2039
    perft(3, board) shouldBe 97862
    perft(4, board) shouldBe 4_085_603
  }

  /**
   * @see https://www.chessprogramming.org/Perft_Results#Position_3
   */
  test("MoveGenerator should work correct third position") {
    val board = ForsythEdwardsNotationUtils.toBoard("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1").get

    perft(1, board) shouldBe 14
    perft(2, board) shouldBe 191
    perft(3, board) shouldBe 2812
    perft(4, board) shouldBe 43238
    perft(5, board) shouldBe 674624
  }

  /**
   * @see https://www.chessprogramming.org/Perft_Results#Position_4
   */
  test("MoveGenerator should work correct fourth position") {
    val board = ForsythEdwardsNotationUtils.toBoard("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1").get

    perft(1, board) shouldBe 6
    perft(2, board) shouldBe 264
    perft(3, board) shouldBe 9467
    perft(4, board) shouldBe 422333
    perft(5, board) shouldBe 15833292
  }

  /**
   * @see https://www.chessprogramming.org/Perft_Results#Position_5
   */
  test("MoveGenerator should work correct fifth position") {
    val board = ForsythEdwardsNotationUtils.toBoard("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8").get

    perft(1, board) shouldBe 44
    perft(2, board) shouldBe 1486
    perft(3, board) shouldBe 62379
    perft(4, board) shouldBe 2103487
    perft(5, board) shouldBe 89_941_194
  }

  /**
   * @see https://www.chessprogramming.org/Perft_Results#Position_6
   */
  test("MoveGenerator should work correct sixth position") {
    val board = ForsythEdwardsNotationUtils.toBoard("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10").get

    perft(0, board) shouldBe 1
    perft(1, board) shouldBe 46
    perft(2, board) shouldBe 2079
    perft(3, board) shouldBe 89890
    perft(4, board) shouldBe 3894594
    perft(5, board) shouldBe 164075551
  }
}
