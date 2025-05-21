package chessengine.core.evaluator

import chessengine.core.movegenerator.MoveGenerator
import common.core.model.Board
import zio.{URLayer, ZIO, ZLayer}

trait Evaluator {
  def evaluate(board: Board): Int
}

object Evaluator {
  val live: URLayer[MoveGenerator, Evaluator] = ZLayer {
    for {
      moveGenerator <- ZIO.service[MoveGenerator]
    } yield new EvaluatorLive(moveGenerator)
  }
}
