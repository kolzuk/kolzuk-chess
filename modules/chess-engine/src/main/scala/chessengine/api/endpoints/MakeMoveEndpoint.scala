package chessengine.api.endpoints

import chessengine.api.ChessEngineServer.ChessEnv
import common.api.chessengine._
import common.core.model.Move
import common.core.utils.ForsythEdwardsNotationUtils
import zio.{ZIO, ZNothing}
import zio.http.{Route, RoutePattern}
import zio.http.codec.Doc
import zio.http.endpoint.AuthType.None
import zio.http.endpoint.Endpoint
import zio.schema.DeriveSchema.gen

object MakeMoveEndpoint {
  val makeMoveEndpoint: Endpoint[Unit, MakeMoveInput, ZNothing, MakeMoveOutput, None] =
    Endpoint((RoutePattern.POST / "make-move") ?? Doc.p("Route that apply move to FEN"))
      .in[MakeMoveInput]
      .out[MakeMoveOutput]
      .tag("chess-engine")

  val makeMoveRoute: Route[ChessEnv, Nothing] = makeMoveEndpoint.implement(input => {
    val result: ZIO[ChessEnv, Throwable, MakeMoveOutput] = for {
      board <- ZIO.fromOption(ForsythEdwardsNotationUtils.toBoard(input.fen))
        .orElseFail(new IllegalArgumentException("Invalid FEN"))
      move = Move.parseFromUCI(input.move, board)
      newBoard = board.makeMove(move)
    } yield MakeMoveOutput(ForsythEdwardsNotationUtils.fromBoard(newBoard))

    result
      .catchAll(_ => ZIO.succeed(MakeMoveOutput("error")))
  })
}
