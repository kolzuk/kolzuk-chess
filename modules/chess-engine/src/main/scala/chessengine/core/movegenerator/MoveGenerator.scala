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
   * @param board The current state of the board.
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
   * @param board The current state of the board.
   * @param isFirstCall A flag indicating whether this is the first call to the function.
   * @return The total count of leaf nodes at the specified depth.
   */
  def perft(depth: Int, board: Board, isFirstCall: Boolean = true)(implicit moveGenerator: MoveGenerator): Long =
    if (depth == 0) 1L
    else {
      // Generate all possible moves from the current board state
      val moves = moveGenerator.generateMoves(board)

      if (depth == 1) {
        // Count the total number of moves at this depth
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
      } else {
        // Count the total number of moves at this depth
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

  // Add comments to explain the purpose of each block of code
  // This function is used for debugging purposes to trace the move generation process
  // It counts the number of leaf nodes at each depth and can be used to identify bugs
}