package common.core.model

/**
 * Represents castling rights for both colors.
 * @see <a href=https://www.chessprogramming.org/Castling_Rights>Castling Rights</a>
 */
case class CastlingAvailability(
  whiteCanCastleKingSide: Boolean = true,
  whiteCanCastleQueenSide: Boolean = true,
  blackCanCastleKingSide: Boolean = true,
  blackCanCastleQueenSide: Boolean = true
) {
  def withoutWhiteCastle: CastlingAvailability = copy(
    whiteCanCastleKingSide = false,
    whiteCanCastleQueenSide = false
  )

  def withoutBlackCastle: CastlingAvailability = copy(
    blackCanCastleKingSide = false,
    blackCanCastleQueenSide = false
  )

  def toIterable: Iterable[Boolean] =
    Iterable(whiteCanCastleKingSide, whiteCanCastleQueenSide, blackCanCastleKingSide, blackCanCastleQueenSide)
}

object CastlingAvailability {
  def noCastle: CastlingAvailability = CastlingAvailability(
    whiteCanCastleKingSide = false,
    whiteCanCastleQueenSide = false,
    blackCanCastleKingSide = false,
    blackCanCastleQueenSide = false
  )
}
