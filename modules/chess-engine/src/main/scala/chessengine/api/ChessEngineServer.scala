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

  private val allEndpoints: Seq[Endpoint] = Seq(allPossibleMovesEndpoint, bestMovesEndpoint, makeMoveEndpoint)
  private val openAPIDocumentation: OpenAPI = OpenAPIGen.fromEndpoints(title = "kolzuk-chess API", version = "1.0", allEndpoints)
  private val swaggerUI = SwaggerUI.routes("docs", openAPIDocumentation)

  private val apiRoutes: Routes = Routes(allPossibleMovesRoute, bestMovesRoute, makeMoveRoute)
  private val allRoutes: Routes = apiRoutes ++ swaggerUI

  private val corsConfiguration: CorsConfig = CorsConfig()
  private val routesWithCorsMiddleware: Routes = Middleware.cors(corsConfiguration)(allRoutes)

  def startServer: ZIO[ChessEnv, Throwable, Nothing] = Server.serve(routesWithCorsMiddleware)
}