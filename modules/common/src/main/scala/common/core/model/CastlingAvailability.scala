package common.core.model

import cats.Show

/**
 * Represents castling rights for both colors.
 *
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
}

object CastlingAvailability {
  private val CastlingAvailabilityRegex = "^[KQkq]{1,4}|-$".r

  /**
   * Castling availability instance creating from FEN notation
   * @example KQkq
   */
  def apply(castlingAvailability: String): CastlingAvailability = {
    require(CastlingAvailabilityRegex.matches(castlingAvailability),
      s"$castlingAvailability does not match with FEN notation")

    val K = castlingAvailability.contains('K')
    val Q = castlingAvailability.contains('Q')
    val k = castlingAvailability.contains('k')
    val q = castlingAvailability.contains('q')

    CastlingAvailability(K, Q, k, q)
  }

  implicit val ShowCastlingAvailability: Show[CastlingAvailability] = Show.show { ca =>
    val K = if (ca.whiteCanCastleKingSide) "K" else ""
    val Q = if (ca.whiteCanCastleQueenSide) "Q" else ""
    val k = if (ca.blackCanCastleKingSide) "k" else ""
    val q = if (ca.blackCanCastleQueenSide) "q" else ""

    val result = s"$K$Q$k$q"
    if (result.isEmpty) "-" else result
  }
}
