package chessengine.core.evaluator

import common.core.model._

class EvaluatorLive extends Evaluator {
  import EvaluatorLive._

  override def evaluate(board: Board): Int = {
    val whiteEval = countMaterial(board, White)
    val blackEval = countMaterial(board, Black)
    val evaluation = whiteEval - blackEval

    val perspective = if (board.activeColor == White) 1 else -1

    evaluation * perspective
  }
}

object EvaluatorLive {
  private val PawnWeight   = 100
  private val KnightWeight = 300
  private val BishopWeight = 300
  private val RookWeight   = 500
  private val QueenWeight  = 900

  def countMaterial(board: Board, evaluatingColor: Color): Int = {
    var material = 0
    for (sq <- 0 until 64) {
      board(sq) match {
        case Some(Pawn(color)) if color == evaluatingColor => material += PawnWeight
        case Some(Knight(color)) if color == evaluatingColor => material += KnightWeight
        case Some(Bishop(color)) if color == evaluatingColor => material += BishopWeight
        case Some(Rook(color)) if color == evaluatingColor => material += RookWeight
        case Some(Queen(color)) if color == evaluatingColor => material += QueenWeight
        case _ =>
      }
    }

    material
  }
}
