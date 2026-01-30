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
    def perftHelper(currentDepth: Int, currentBoard: Board, acc: Long): Long = {
      if (currentDepth == 0) {
        acc + 1
      } else {
        val moves = moveGenerator.generateMoves(currentBoard)
        moves.flatMap { move =>
          val nextBoard = currentBoard.makeMove(move)
          perftHelper(currentDepth - 1, nextBoard, acc)
        }.sum
      }
    }

    if (depth == 0) {
      1L
    } else {
      val result = perftHelper(depth, board, 0L)
      if (isFirstCall) {
        println(s"\n$result")
      }
      result
    }
  }

  /**
   * A helper function to recursively calculate the total number of moves at a given depth.
   */
  private def perftHelper(depth: Int, board: Board, acc: Long): Long = {
    val moves = moveGenerator.generateMoves(board)
    if (depth == 1) {
      moves.map(m => {
        val countOfMoves = perft(depth - 1, board.makeMove(m), isFirstCall = false)
        if (isFirstCall)
          println(s"${m.show} $countOfMoves")
        countOfMoves
      }).sum
    } else {
      moves.flatMap(m => {
        val countOfMoves = perft(depth - 1, board.makeMove(m), isFirstCall = false)
        if (isFirstCall)
          println(s"${m.show} $countOfMoves")
        countOfMoves
      }).sum
    }
  }
}