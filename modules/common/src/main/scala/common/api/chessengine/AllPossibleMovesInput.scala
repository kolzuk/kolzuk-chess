package common.api.chessengine

import zio.json._

final case class AllPossibleMovesInput(fen: String)

object AllPossibleMovesInput {

  implicit val codec: JsonCodec[AllPossibleMovesInput] = DeriveJsonCodec.gen[AllPossibleMovesInput]

}
