package chessengine.api.endpoints

import cats.implicits.toShow
import chessengine.api.ChessEngineServer.ChessEnv
import chessengine.core.movegenerator.MoveGenerator
import common.api.chessengine._
import common.core.utils.ForsythEdwardsNotationUtils
import zio.http.codec.Doc
import zio.http.endpoint.AuthType.None
import zio.http.endpoint.Endpoint
import zio.http.{Route, RoutePattern}
import zio.schema.DeriveSchema.gen
import zio.{ZIO, ZNothing}

object AllPossibleMovesEndpoint {
  val allPossibleMovesEndpoint: Endpoint[Unit, AllPossibleMovesInput, ZNothing, AllPossibleMovesOutput, None] =
    Endpoint((RoutePattern.POST / "all-possible-moves") ?? Doc.p("Route for querying all possible moves"))
      .in[AllPossibleMovesInput]("The fen describing of fe")
      .out[AllPossibleMovesOutput](Doc.p("The best move from fen position"))
      .tag("chess-engine")

  val allPossibleMovesRoute: Route[ChessEnv, Nothing] = allPossibleMovesEndpoint
    .implement(input => {
      val result: ZIO[ChessEnv, Throwable, AllPossibleMovesOutput] = for {
        moveGenerator <- ZIO.service[MoveGenerator]
        board <- ZIO.fromOption(ForsythEdwardsNotationUtils.toBoard(input.fen))
          .orElseFail(new IllegalArgumentException("Invalid FEN"))
        moves = moveGenerator.generateMoves(board).map(_.show)
      } yield AllPossibleMovesOutput(moves)

      result
        .tapError(err => ZIO.logError(s"Error: $err"))
        .catchAll(_ => ZIO.succeed(AllPossibleMovesOutput(Nil)))
    })
}
