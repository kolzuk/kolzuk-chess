package chessengine.core.movegenerator

import cats.implicits.toShow
import common.core.model.{Board, Move}
import zio._

/** `MoveGenerator` provides a generation of all possible legal moves from
 * the current state of board.
 */
trait MoveGenerator {
  /**
   * Generates all possible legal moves from the current state of the board.
   *
   * @param board the current state of the board
   * @return a list of all possible legal moves
   */
  def generateMoves(board: Board): List[Move]
}

object MoveGenerator {

  val live: ULayer[MoveGenerator] = ZLayer.succeed(new MoveGeneratorLive)

  /**
   * Perf(omance) t(est) - a debugging function to walk the move generation tree of
   * strictly legal moves to count all the leaf nodes of a certain depth, which can
   * be compared to predetermined values and used to isolate bugs.
   *
   * @param depth the depth of the move generation tree
   * @param board the current state of the board
   * @param isFirstCall indicates if this is the first call of the function
   * @return the total count of leaf nodes at the specified depth
   * @throws IllegalArgumentException if depth is less than 0
   */
  def perft(depth: Int, board: Board, isFirstCall: Boolean = true)(implicit moveGenerator: MoveGenerator): Long =
    if (depth == 0) 1L
    else {
      val moves = moveGenerator.generateMoves(board)
      if (depth == 1) {
        val allMovesCount = moves.map(m => {
          val countOfMoves = perft(depth - 1, board.makeMove(m), isFirstCall = false)
          if (isFirstCall)
            println(s"${m.show} $countOfMoves")
          countOfMoves
        }).sum
        if (isFirstCall) {
          println(s"\n$allMovesCount")
        }
        moves.length
      }
      else {
        val allMovesCount = moves.map(m => {
          val countOfMoves = perft(depth - 1, board.makeMove(m), isFirstCall = false)
          if (isFirstCall)
            println(s"${m.show} $countOfMoves")
          countOfMoves
        }).sum

        if (isFirstCall) {
          println(s"\n$allMovesCount")
        }

        allMovesCount
      }
    }

  /**
   * Checks if the provided depth is valid.
   *
   * @param depth the depth to check
   * @throws IllegalArgumentException if depth is less than 0
   */
  private def validateDepth(depth: Int): Unit =
    if (depth < 0)
      throw new IllegalArgumentException(s"Depth must be non-negative, but was $depth")
}