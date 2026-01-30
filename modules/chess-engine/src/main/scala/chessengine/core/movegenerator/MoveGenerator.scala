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
   * @param board The current state of the chess board.
   * @return A list of all possible legal moves.
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
   * @param depth The depth of the move generation tree to traverse.
   * @param board The current state of the chess board.
   * @param isFirstCall A flag indicating if this is the first call of the function.
   * @return The total count of all leaf nodes at the specified depth.
   * @throws IllegalArgumentException if the depth is less than 0.
   */
  def perft(depth: Int, board: Board, isFirstCall: Boolean = true)(implicit moveGenerator: MoveGenerator): Long =
    if (depth < 0) {
      throw new IllegalArgumentException("Depth must be non-negative")
    }
    else if (depth == 0) 1L
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
}