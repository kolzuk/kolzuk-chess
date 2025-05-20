package common.api.chessengine

import zio.json.{DeriveJsonCodec, JsonCodec}

final case class BestMovesOutput(moves: List[String])

object BestMovesOutput {

  implicit val codec: JsonCodec[BestMovesOutput] = DeriveJsonCodec.gen[BestMovesOutput]

}
