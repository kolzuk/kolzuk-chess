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
    if (depth == 0) {
      1L
    } else {
      generateMovesAndCount(depth, board, isFirstCall, moveGenerator)
    }
  }

  private def generateMovesAndCount(depth: Int, board: Board, isFirstCall: Boolean, moveGenerator: MoveGenerator): Long = {
    val moves = moveGenerator.generateMoves(board)

    if (depth == 1) {
      countMoves(moves, depth, isFirstCall)
    } else {
      countMoves(moves, depth, isFirstCall).sum
    }
  }

  private def countMoves(moves: List[Move], depth: Int, isFirstCall: Boolean, moveGenerator: MoveGenerator): List[Long] = {
    moves.map { move =>
      val childMovesCount = perft(depth - 1, board.makeMove(move), isFirstCall = false)
      if (isFirstCall) {
        println(s"${move.show} $childMovesCount")
      }
      childMovesCount
    }
  }
}