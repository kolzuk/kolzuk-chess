package chessengine.core.bitboards

import BitboardRepresentation._
import common.core.model._
import scala.collection._

object BitboardOperations {
  val FileA         = 0x0101010101010101L
  val FileH         = 0x8080808080808080L
  val Rank1         = 0x00000000000000FFL
  val Rank4         = 0x00000000FF000000L
  val Rank5         = 0x000000FF00000000L
  val Rank8         = 0xFF00000000000000L
  val EmptyBitboard = 0x0000000000000000L
  val NotFileA      = ~FileA
  val NotFileH      = ~FileH

  def northOne(b: Bitboard): Bitboard     = b << 8
  def northEastOne(b: Bitboard): Bitboard = (b << 9) & NotFileA
  def eastOne(b: Bitboard): Bitboard      = (b << 1) & NotFileA
  def southEastOne(b: Bitboard): Bitboard = (b >>> 7) & NotFileA
  def southOne(b: Bitboard): Bitboard     = b >>> 8
  def southWestOne(b: Bitboard): Bitboard = (b >>> 9) & NotFileH
  def westOne(b: Bitboard): Bitboard      = (b >>> 1) & NotFileH
  def northWestOne(b: Bitboard): Bitboard = (b << 7) & NotFileH

  private val DeBruijn64: Bitboard = 0x03f79d71b4cb0a89L

  private val DeBruijnBitScanForwardArray: Vector[Byte] = Vector(
    0,  1, 48,  2, 57, 49, 28,  3,
    61, 58, 50, 42, 38, 29, 17,  4,
    62, 55, 59, 36, 53, 51, 43, 22,
    45, 39, 33, 30, 24, 18, 12,  5,
    63, 47, 56, 27, 60, 41, 37, 16,
    54, 35, 52, 21, 44, 32, 23, 11,
    46, 26, 40, 15, 34, 20, 31, 10,
    25, 14, 19,  9, 13,  8,  7,  6
  )

  /** Return a index of the least significant bit
   * @param b bitboard to scan
   * @return index(0..63) of the least significant bit
   */
  def bitScanForward(b: Bitboard): Byte = {
    require(b != 0, "bitboard can't be zero")

    DeBruijnBitScanForwardArray((((b & -b) * DeBruijn64) >>> 58).toInt)
  }

  private val DeBruijnBitScanBackwardArray: Vector[Byte] = Vector(
    0, 47,  1, 56, 48, 27,  2, 60,
    57, 49, 41, 37, 28, 16,  3, 61,
    54, 58, 35, 52, 50, 42, 21, 44,
    38, 32, 29, 23, 17, 11,  4, 62,
    46, 55, 26, 59, 40, 36, 15, 53,
    34, 51, 20, 43, 31, 22, 10, 45,
    25, 39, 14, 33, 19, 30,  9, 24,
    13, 18,  8, 12,  7,  6,  5, 63
  )

  /** Return a index of the most significant bit
   * @param b bitboard to scan
   * @return index(0..63) of the least significant bit
   */
  def bitScanReverse(b: Bitboard): Int = {
    require(b != 0, "bitboard can't be zero")

    var bb = b
    bb |= bb >>> 1
    bb |= bb >>> 2
    bb |= bb >>> 4
    bb |= bb >>> 8
    bb |= bb >>> 16
    bb |= bb >>> 32

    DeBruijnBitScanBackwardArray(((bb * DeBruijn64) >>> 58).toInt)
  }

  // ---------- Pawns moves ----------
  def wSinglePushPawns(wPawns: Bitboard, empty: Bitboard): Bitboard = {
    val result = northOne(wPawns) & empty
    result
  }
  def wDoublePushPawns(wPawns: Bitboard, empty: Bitboard): Bitboard = {
    val singlePushes = wSinglePushPawns(wPawns, empty)
    northOne(singlePushes) & empty & Rank4
  }

  def bSinglePushPawns(bPawns: Bitboard, empty: Bitboard): Bitboard = southOne(bPawns) & empty
  def bDoublePushPawns(bPawns: Bitboard, empty: Bitboard): Bitboard = {
    val singlePushes = bSinglePushPawns(bPawns, empty)
    southOne(singlePushes) & empty & Rank5
  }

  val pawnsAttacksMap: Map[Color, Vector[Bitboard]] = {
    def pawnAttacks(square: Int, color: Color): Bitboard = {
      val bb: Bitboard = 1L << square

      color match {
        case Color.White => northEastOne(bb) | northWestOne(bb)
        case Color.Black => southEastOne(bb) | southWestOne(bb)
      }
    }

    var whitePawnAttacks: List[Bitboard] = List.empty
    var blackPawnAttacks: List[Bitboard] = List.empty

    for (sq <- 0 to 64) {
      whitePawnAttacks = pawnAttacks(sq, Color.White) :: whitePawnAttacks
      blackPawnAttacks = pawnAttacks(sq, Color.Black) :: blackPawnAttacks
    }

    Map(
      Color.White -> whitePawnAttacks.reverse.toVector,
      Color.Black -> blackPawnAttacks.reverse.toVector
    )
  }

  // ---------- Knight moves ----------
  val knightAttacksVector: Vector[Bitboard] = {
    var l: List[Bitboard] = List.empty
    for (i <- 63 to 0 by -1)
      l = knightAttacks(1L << i) :: l

    l.toVector
  }

  /** @see <a href=https://www.chessprogramming.org/Knight_Pattern>Knight Pattern</a>
   */
  def knightAttacks(knights: Bitboard): Bitboard = {
    var (west, east) = (EmptyBitboard, EmptyBitboard)
    var attacks = EmptyBitboard

    east = eastOne(knights)
    west = westOne(knights)
    attacks = (east | west) << 16
    attacks |= (east | west) >>> 16

    east = eastOne(east)
    west = westOne(west)
    attacks |= (east | west) << 8
    attacks |= (east | west) >>> 8

    attacks
  }

  // ---------- King moves ----------
  val kingAttacksVector: Vector[Bitboard] = {
    var l: List[Bitboard] = List.empty
    for (i <- 63 to 0 by -1)
      l = kingAttacks(1L << i) :: l

    l.toVector
  }

  /**
   * @see <a href=https://www.chessprogramming.org/King_Pattern>King pattern</a>
   */
  private def kingAttacks(king: Bitboard): Bitboard = {
    var attacks = eastOne(king) | westOne(king)
    val kingHorizontalSquares = king | attacks
    attacks |= northOne(kingHorizontalSquares) | southOne(kingHorizontalSquares)

    attacks
  }

  private val North = 0
  private val NorthEast = 1
  private val East = 2
  private val SouthEast = 3
  private val South = 4
  private val SouthWest = 5
  private val West = 6
  private val NorthWest = 7

  /**
   * `Rays` - предвычисленный массив всех битбордов лучшей для каждой клетки и каждого
   * из 8 базовых направлений.
   *
   * Используется для генерации ходов слайдеров (ладья, слон, ферзь).
   */
  private val Rays: Vector[Vector[Bitboard]] = {
    def getAllSlidingMoves(f: Bitboard => Bitboard): Vector[Bitboard] = {
      var l: List[Bitboard] = List.empty
      for (sq <- 63 to 0 by -1) {
        var steps = f(1L << sq)
        for (_ <- 0 until 8)
          steps |= f(steps)
        l = steps :: l
      }
      l.toVector
    }

    val north     = getAllSlidingMoves(northOne)
    val northEast = getAllSlidingMoves(northEastOne)
    val east      = getAllSlidingMoves(eastOne)
    val southEast = getAllSlidingMoves(southEastOne)
    val south     = getAllSlidingMoves(southOne)
    val southWest = getAllSlidingMoves(southWestOne)
    val west      = getAllSlidingMoves(westOne)
    val northWest = getAllSlidingMoves(northWestOne)

    Vector(north, northEast, east, southEast, south, southWest, west, northWest)
  }

  // ---------- Bishop moves ----------
  /**
   * @see <a href=https://rhysre.net/fast-chess-move-generation-with-magic-bitboards.html>Bishop attacks</a>
   */
  def bishopAttacks(square: Int, blockers: Bitboard): Bitboard = {
    var attacks: Bitboard = 0L

    attacks |= Rays(NorthWest)(square)
    if ((Rays(NorthWest)(square) & blockers) != 0) {
      val blockerIndex = bitScanForward(Rays(NorthWest)(square) & blockers)
      attacks &= ~Rays(NorthWest)(blockerIndex)
    }

    attacks |= Rays(NorthEast)(square)
    if ((Rays(NorthEast)(square) & blockers) != 0) {
      val blockerIndex = bitScanForward(Rays(NorthEast)(square) & blockers)
      attacks &= ~Rays(NorthEast)(blockerIndex)
    }

    attacks |= Rays(SouthEast)(square)
    if ((Rays(SouthEast)(square) & blockers) != 0) {
      val blockerIndex = bitScanReverse(Rays(SouthEast)(square) & blockers)
      attacks &= ~Rays(SouthEast)(blockerIndex)
    }

    attacks |= Rays(SouthWest)(square)
    if ((Rays(SouthWest)(square) & blockers) != 0) {
      val blockerIndex = bitScanReverse(Rays(SouthWest)(square) & blockers)
      attacks &= ~Rays(SouthWest)(blockerIndex)
    }

    attacks
  }

  // ---------- Rook moves ----------
  def rookAttacks(square: Int, blockers: Bitboard): Bitboard = {
    var attacks: Bitboard = 0L

    attacks |= Rays(North)(square)
    if ((Rays(North)(square) & blockers) != 0) {
      val blockerIndex = bitScanForward(Rays(North)(square) & blockers)
      attacks &= ~Rays(North)(blockerIndex)
    }

    attacks |= Rays(East)(square)
    if ((Rays(East)(square) & blockers) != 0) {
      val blockerIndex = bitScanForward(Rays(East)(square) & blockers)
      attacks &= ~Rays(East)(blockerIndex)
    }

    attacks |= Rays(South)(square)
    if ((Rays(South)(square) & blockers) != 0) {
      val blockerIndex = bitScanReverse(Rays(South)(square) & blockers)
      attacks &= ~Rays(South)(blockerIndex)
    }

    attacks |= Rays(West)(square)
    if ((Rays(West)(square) & blockers) != 0) {
      val blockerIndex = bitScanReverse(Rays(West)(square) & blockers)
      attacks &= ~Rays(West)(blockerIndex)
    }

    attacks
  }

  // ---------- Queen moves ----------
  def queenAttacks(square: Int, blockers: Bitboard): Bitboard =
    bishopAttacks(square, blockers) | rookAttacks(square, blockers)
}
