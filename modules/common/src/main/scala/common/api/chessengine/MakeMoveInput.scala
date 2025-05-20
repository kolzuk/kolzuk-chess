package common.api.chessengine

import zio.json.{DeriveJsonCodec, JsonCodec}

case class MakeMoveInput(fen: String, move: String)

object MakeMoveInput {

  implicit val codec: JsonCodec[MakeMoveInput] = DeriveJsonCodec.gen[MakeMoveInput]

}
