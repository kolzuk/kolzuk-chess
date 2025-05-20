package common.api.chessengine

import zio.json.{DeriveJsonCodec, JsonCodec}

final case class BestMovesInput(fen: String, depth: Int)

object BestMovesInput {

  implicit val codec: JsonCodec[BestMovesInput] = DeriveJsonCodec.gen[BestMovesInput]

}
