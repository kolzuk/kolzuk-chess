package common.api.chessengine

import zio.json.{DeriveJsonCodec, JsonCodec}

case class MakeMoveOutput(fen: String)

object MakeMoveOutput {

  implicit val codec: JsonCodec[MakeMoveOutput] = DeriveJsonCodec.gen[MakeMoveOutput]

}
