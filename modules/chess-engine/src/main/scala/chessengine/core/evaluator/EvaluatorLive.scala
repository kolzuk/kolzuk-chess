package chessengine.core.evaluator

import chessengine.core.movegenerator.MoveGenerator
import common.core.model._

class EvaluatorLive(moveGenerator: MoveGenerator) extends Evaluator {
  import EvaluatorLive._

  override def evaluate(board: Board): Int = {
    val whiteEval = countMaterial(board, Color.White)
    val blackEval = countMaterial(board, Color.Black)
    val materialEval = whiteEval - blackEval

//    val whiteMobility = moveGenerator.generateMoves(board.copy(activeColor = Color.White)).length
//    val blackMobility = moveGenerator.generateMoves(board.copy(activeColor = Color.Black)).length
//    val mobilityEval = (whiteMobility - blackMobility) * MobilityWeight

    val totalEval = materialEval

    val perspective = if (board.activeColor == Color.White) 1 else -1
    totalEval * perspective
  }
}

object EvaluatorLive {
  private val MobilityWeight = 10

  private def countMaterial(board: Board, evaluatingColor: Color): Int = {
    var material = 0
    for (sq <- 0 until 64) {
      board(sq) match {
        case Some(f) if f.color == evaluatingColor => material += f.weight
        case _ =>
      }
    }

    material
  }
}
