package chessengine.api

import chessengine.api.endpoints._
import chessengine.core.evaluator.Evaluator
import chessengine.core.movegenerator.MoveGenerator
import chessengine.core.search.MoveSearcher
import zio._
import zio.http.Middleware.CorsConfig
import zio.http._
import zio.http.endpoint.openapi._
import zio.http.codec.PathCodec._

object ChessEngineServer {
  import AllPossibleMovesEndpoint._
  import BestMovesEndpoint._
  import MakeMoveEndpoint._

  type ChessEnv = MoveGenerator & MoveSearcher & Evaluator & Server

  private val allEndpoints = Seq(allPossibleMovesEndpoint, bestMovesEndpoint, makeMoveEndpoint)
  private val openAPIGen = OpenAPIGen.fromEndpoints(title = "kolzuk-chess API", version = "1.0", allEndpoints)
  private val openAPI = openAPIGen.value
  private val swaggerRoutes = SwaggerUI.routes("docs", openAPI)

  private val apiRoutes = Routes(allPossibleMovesRoute, bestMovesRoute, makeMoveRoute)
  private val routes = apiRoutes ++ swaggerRoutes

  private val corsConfig = CorsConfig()
  private val routesWithCors = Middleware.cors(corsConfig)(routes)
  def run: ZIO[ChessEnv, Throwable, Nothing] = Server.serve(routesWithCors)
}