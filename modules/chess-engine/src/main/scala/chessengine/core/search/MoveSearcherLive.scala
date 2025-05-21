package chessengine.core.search

import chessengine.core.evaluator.Evaluator
import chessengine.core.movegenerator.MoveGenerator
import common.core.model.{Board, Move}

class MoveSearcherLive(evaluator: Evaluator, moveGenerator: MoveGenerator) extends MoveSearcher {
  private def orderMoves(board: Board, moves: List[Move]): List[Move] = {
    moves
      .zipWithIndex
      .map { case (move, index) =>
        if (board.makeMove(move).isKingChecked)
          (10000, index)
        else if (board(move.to).nonEmpty)
          (9000, index)
        else if (move.isPromotion)
          (8000, index)
        else
          (1, index)
      }
      .sorted
      .reverse
      .map { case (_, index) =>
        moves(index)
      }

  }

  private def alphaBetaMax(_alpha: Int, beta: Int, depthLeft: Int, board: Board): Int = {
    if (depthLeft == 0) return evaluator.evaluate(board)

    var alpha = _alpha
    var bestValue = Int.MinValue
    val allMoves = orderMoves(board, moveGenerator.generateMoves(board))
    for (move <- allMoves) {
      val newBoard = board.makeMove(move)
      val score = alphaBetaMin(alpha, beta, depthLeft - 1, newBoard)
      if (score > bestValue) {
        bestValue = score
        if (score > alpha)
          alpha = score
      }
      if (score >= beta)
        return score
    }

    bestValue
  }

  private def alphaBetaMin(alpha: Int, _beta: Int, depthLeft: Int, board: Board): Int = {
    if (depthLeft == 0) return -evaluator.evaluate(board)

    var beta = _beta
    var bestValue = Int.MaxValue
    val allMoves = moveGenerator.generateMoves(board)

    for (move <- allMoves) {
      val newBoard = board.makeMove(move)
      val score = alphaBetaMax(alpha, beta, depthLeft - 1, newBoard)
      if (score < bestValue) {
        bestValue = score
        if (score < beta)
          beta = score
      }
      if (score <= alpha)
        return score
    }

    bestValue
  }

  override def search(board: Board, depth: Int): List[Move] = {
    val moves = moveGenerator.generateMoves(board)
    var bestScore = Int.MinValue
    var bestMoves = List.empty[Move]

    for (move <- moves) {
      val newBoard = board.makeMove(move)
      val score = alphaBetaMin(Int.MinValue, Int.MaxValue, depth - 1, newBoard)
      if (score > bestScore) {
        bestScore = score
        bestMoves = List(move)
      } else if (score == bestScore) {
        bestMoves :+= move
      }
    }

    bestMoves
  }
}
