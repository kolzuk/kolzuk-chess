package chessengine.api.endpoints

import cats.implicits.toShow
import chessengine.api.ChessEngineServer.ChessEnv
import chessengine.core.search.MoveSearcher
import common.api.chessengine._
import common.core.utils.ForsythEdwardsNotationUtils
import zio.http._
import zio.http.codec._
import zio.http.endpoint.AuthType.None
import zio.http.endpoint._
import zio.schema.DeriveSchema.gen
import zio.{ZIO, ZNothing}

object BestMovesEndpoint {
  val bestMovesEndpoint: Endpoint[Unit, BestMovesInput, ZNothing, BestMovesOutput, None] =
    Endpoint((RoutePattern.POST / "best-move") ?? Doc.p("Route for querying best moves"))
      .in[BestMovesInput]("The fen describing of fe")
      .out[BestMovesOutput](Doc.p("The best move from fen position"))
      .tag("chess-engine")

  val bestMovesRoute: Route[ChessEnv, Nothing] = bestMovesEndpoint.implement(input => {
    val result: ZIO[ChessEnv, Throwable, BestMovesOutput] = for {
      moveSearcher <- ZIO.service[MoveSearcher]
      board <- ZIO.fromOption(ForsythEdwardsNotationUtils.toBoard(input.fen))
        .orElseFail(new IllegalArgumentException("Invalid FEN"))
      moves = moveSearcher.search(board, input.depth).map(_.show)
    } yield BestMovesOutput(moves)

    result
      .catchAll(_ => ZIO.succeed(BestMovesOutput(Nil)))
  })
}
