package chessengine.core.evaluator

import common.core.model.Board
import zio.{ULayer, ZLayer}

trait Evaluator {
  def evaluate(board: Board): Int
}

object Evaluator {
  val live: ULayer[Evaluator] = ZLayer.succeed(new EvaluatorLive)
}
