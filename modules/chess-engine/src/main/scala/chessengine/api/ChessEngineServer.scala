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

  type ChessEngineEnvironment = MoveGenerator & MoveSearcher & Evaluator & Server

  private val allEndpoints = Seq(allPossibleMovesEndpoint, bestMovesEndpoint, makeMoveEndpoint)
  private val openAPIDocumentation = OpenAPIGen.fromEndpoints(title = "ChessEngine API", version = "1.0", allEndpoints)
  private val swaggerDocumentationRoutes = SwaggerUI.routes("documentation", openAPIDocumentation)

  private val apiRoutes = Routes(allPossibleMovesRoute, bestMovesRoute, makeMoveRoute)
  private val comprehensiveRoutes = apiRoutes ++ swaggerDocumentationRoutes

  private val corsConfiguration = CorsConfig()
  private val routesWithCrossOriginSupport = Middleware.cors(corsConfiguration)(comprehensiveRoutes)
  def startServer: ZIO[ChessEngineEnvironment, Throwable, Unit] = Server.serve(routesWithCrossOriginSupport)
}