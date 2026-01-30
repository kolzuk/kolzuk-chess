package chessengine.core.movegenerator

import cats.implicits.toShow
import common.core.model.{Board, Move}
import zio._

/** `MoveGenerator` provides a generation of all possible legal moves from
 * the current state of board.
 */
trait MoveGenerator {
  def generateMoves(board: Board): List[Move]
}

object MoveGenerator {

  val live: ULayer[MoveGenerator] = ZLayer.succeed(new MoveGeneratorLive)

  /** Perf(omance) t(est) - a debugging function to walk the move generation tree of
   * strictly legal moves to count all the leaf nodes of a certain depth, which can
   * be compared to predetermined values and used to isolate bugs.
   */
  def perft(depth: Int, board: Board, isFirstCall: Boolean = true)(implicit moveGenerator: MoveGenerator): Long = {
    @annotation.tailrec
    def perftRec(currentDepth: Int, currentBoard: Board, acc: Long, isFirstCall: Boolean): Long = {
      if (currentDepth == 0) {
        acc
      } else {
        val moves = moveGenerator.generateMoves(currentBoard)
        val moveCount = moves.length

        if (currentDepth == 1) {
          val moveCounts = moves.map(m => perftRec(currentDepth - 1, currentBoard.makeMove(m), 0, false))
          val totalMovesCount = moveCounts.sum + moveCount
          if (isFirstCall) {
            println(s"${currentDepth} ${m.show} $totalMovesCount")
          }
          if (isFirstCall) {
            println(s"\n$totalMovesCount")
          }
          totalMovesCount
        } else {
          val moveCounts = moves.map(m => perftRec(currentDepth - 1, currentBoard.makeMove(m), 0, false))
          val totalMovesCount = moveCounts.sum + moveCount
          if (isFirstCall) {
            println(s"${currentDepth} ${m.show} $totalMovesCount")
          }
          if (isFirstCall) {
            println(s"\n$totalMovesCount")
          }
          totalMovesCount
        }
      }
    }

    perftRec(depth, board, 0, isFirstCall)
  }
}