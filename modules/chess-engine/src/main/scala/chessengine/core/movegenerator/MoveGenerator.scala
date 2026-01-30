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
   * @param depth the depth of the move generation tree to traverse
   * @param board the current state of the board
   * @param isFirstCall a flag indicating whether this is the first call
   * @return the total count of moves at the specified depth
   */
  def perft(depth: Int, board: Board, isFirstCall: Boolean = true)(implicit moveGenerator: MoveGenerator): Long =
    if (depth == 0) 1L
    else {
      // Generate all possible moves from the current board state
      val moves = moveGenerator.generateMoves(board)

      // If this is the first call, print the move and the count of moves at the next depth
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
      } else {
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

  // Add comments explaining the purpose of the function and its parameters
  /**
   * This function is used for debugging purposes to traverse the move generation tree and count the number of moves at each depth.
   * It helps in identifying performance issues and bugs in the move generation logic.
   *
   * @param depth the depth of the move generation tree to traverse
   * @param board the current state of the board
   * @param isFirstCall a flag indicating whether this is the first call
   * @param moveGenerator the move generator to use for generating moves
   * @return the total count of moves at the specified depth
   */
}