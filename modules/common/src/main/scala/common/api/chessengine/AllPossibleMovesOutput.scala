package common.api.chessengine

import zio.json._

final case class AllPossibleMovesOutput(moves: List[String])

object AllPossibleMovesOutput {

  implicit val codec: JsonCodec[AllPossibleMovesOutput] = DeriveJsonCodec.gen[AllPossibleMovesOutput]

}
