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
    def perftHelper(currentDepth: Int, currentBoard: Board, count: Long): Long = {
      if (currentDepth == 0) {
        count
      } else {
        val moves = moveGenerator.generateMoves(currentBoard)
        val nextDepthCount = moves.map { move =>
          perftHelper(currentDepth - 1, currentBoard.makeMove(move), 1)
        }.sum
        perftHelper(currentDepth - 1, currentBoard, nextDepthCount)
      }
    }

    if (depth == 0) {
      1L
    } else {
      val moves = moveGenerator.generateMoves(board)
      if (depth == 1) {
        val allMovesCount = moves.map { move =>
          val countOfMoves = perft(depth - 1, board.makeMove(move), isFirstCall = false)
          if (isFirstCall)
            println(s"${move.show} $countOfMoves")
          countOfMoves
        }.sum
        if (isFirstCall) {
          println(s"\n$allMovesCount")
        }
        moves.length
      } else {
        val allMovesCount = moves.map { move =>
          val countOfMoves = perft(depth - 1, board.makeMove(move), isFirstCall = false)
          if (isFirstCall)
            println(s"${move.show} $countOfMoves")
          countOfMoves
        }.sum

        if (isFirstCall) {
          println(s"\n$allMovesCount")
        }

        allMovesCount
      }
    }
  }
}