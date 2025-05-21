package chessengine

import chessengine.core.evaluator.Evaluator
import chessengine.core.movegenerator.MoveGenerator
import chessengine.core.search.MoveSearcher
import zio.{ZIO, ZIOAppDefault}
import zio.http.Server

object Main extends ZIOAppDefault {

  override def run: ZIO[Any, Throwable, Nothing] =
    api.ChessEngineServer
      .run
      .provide(
        Evaluator.live,
        MoveGenerator.live,
        MoveSearcher.live,
        Server.defaultWithPort(80)
      )
}
