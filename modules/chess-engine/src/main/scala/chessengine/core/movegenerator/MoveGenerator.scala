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
  }

  /**
   * This method is intended to simplify the perft calculation by breaking down the recursive steps into more readable chunks.
   */
  private def calculatePerftRecursive(moves: List[Move], depth: Int, board: Board, isFirstCall: Boolean)(implicit moveGenerator: MoveGenerator): Long = {
    if (depth == 0) 1L
    else {
      val moveCounts = moves.map(move => {
        val newBoard = board.makeMove(move)
        calculatePerftRecursive(moveGenerator.generateMoves(newBoard), depth - 1, newBoard, isFirstCall = false)
      })

      val totalCount = moveCounts.sum
      if (isFirstCall) {
        println(s"\nTotal perft count: $totalCount")
      }
      totalCount
    }
  }
}