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
   *  strictly legal moves to count all the leaf nodes of a certain depth, which can
   *  be compared to predetermined values and used to isolate bugs.
   */
  def perft(depth: Int, board: Board, isFirstCall: Boolean = true)(implicit moveGenerator: MoveGenerator): Long = {
    @annotation.tailrec
    def perftTailRec(currentDepth: Int, currentBoard: Board, isFirstCall: Boolean, acc: Long): Long = {
      if (currentDepth == 0) {
        acc + 1
      } else {
        val moves = moveGenerator.generateMoves(currentBoard)
        val nextDepth = currentDepth - 1
        val moveCount = moves.map(m => {
          val count = perftTailRec(nextDepth, currentBoard.makeMove(m), isFirstCall = false, acc)
          if (isFirstCall) {
            println(s"${m.show} $count")
          }
          count
        }).sum

        if (isFirstCall) {
          println(s"\n$moveCount")
        }

        moveCount + acc
      }
    }

    perftTailRec(depth, board, isFirstCall, 0L)
  }
}