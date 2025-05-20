package chessengine.core.search

import chessengine.core.evaluator.Evaluator
import chessengine.core.movegenerator.MoveGenerator
import common.core.model.{Board, Move}
import zio._

trait MoveSearcher {
  def search(board: Board, depth: Int): List[Move]
}

object MoveSearcher {
  val live: URLayer[Evaluator & MoveGenerator, MoveSearcher] = ZLayer {
    for {
      evaluator <- ZIO.service[Evaluator]
      moveGenerator <- ZIO.service[MoveGenerator]
    } yield new MoveSearcherLive(evaluator, moveGenerator)
  }
}
