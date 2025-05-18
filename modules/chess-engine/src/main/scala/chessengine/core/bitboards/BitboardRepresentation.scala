package chessengine.core.bitboards

import BitboardRepresentation._
import common.core.model._
import BitboardOperations._
import common.core.model.MoveType.Normal

case class BitboardRepresentation(bitboards: Vector[Bitboard]) {
  require(bitboards.length == 12, "The number of bitboards should be 12!")

  private val whitePawnBitboard: Bitboard   = bitboards(0)
  private val whiteKnightBitboard: Bitboard = bitboards(1)
  private val whiteBishopBitboard: Bitboard = bitboards(2)
  private val whiteRookBitboard: Bitboard   = bitboards(3)
  private val whiteQueenBitboard: Bitboard  = bitboards(4)
  private val whiteKingBitboard: Bitboard   = bitboards(5)
  private val blackPawnBitboard: Bitboard   = bitboards(6)
  private val blackKnightBitboard: Bitboard = bitboards(7)
  private val blackBishopBitboard: Bitboard = bitboards(8)
  private val blackRookBitboard: Bitboard   = bitboards(9)
  private val blackQueenBitboard: Bitboard  = bitboards(10)
  private val blackKingBitboard: Bitboard   = bitboards(11)

  private def pawnBitboard(color: Color): Bitboard = color match {
    case White => whitePawnBitboard
    case Black => blackPawnBitboard
  }
  private def knightBitboard(color: Color): Bitboard = color match {
    case White => whiteKnightBitboard
    case Black => blackKnightBitboard
  }
  private def bishopBitboard(color: Color): Bitboard = color match {
    case White => whiteBishopBitboard
    case Black => blackBishopBitboard
  }
  private def rookBitboard(color: Color): Bitboard = color match {
    case White => whiteRookBitboard
    case Black => blackRookBitboard
  }
  private def queenBitboard(color: Color): Bitboard = color match {
    case White => whiteQueenBitboard
    case Black => blackQueenBitboard
  }
  private def kingBitboard(color: Color): Bitboard = color match {
    case White => whiteKingBitboard
    case Black => blackKingBitboard
  }

  private def occupiedByWhiteBitboard: Bitboard =
    bitboards.take(6).fold(BitboardOperations.EmptyBitboard)(_ | _)
  private def occupiedByBlackBitboard: Bitboard =
    bitboards.takeRight(6).fold(BitboardOperations.EmptyBitboard)(_ | _)
  def occupiedByColorBitboard(color: Color): Bitboard = color match {
    case White => occupiedByWhiteBitboard
    case Black => occupiedByBlackBitboard
  }
  def occupiedByOppositeColorBitboard(color: Color): Bitboard = color match {
    case White => occupiedByBlackBitboard
    case Black => occupiedByWhiteBitboard
  }
  def occupied: Bitboard = occupiedByWhiteBitboard | occupiedByBlackBitboard
  def empty: Bitboard    = ~occupied

  def isKingChecked(color: Color): Boolean = {
    val opColor = color.opponentColor

    val squareOfKing        = bitScanForward(kingBitboard(color))

    val opPawns: Bitboard   = pawnBitboard(opColor)
    val opKnights: Bitboard = knightBitboard(opColor)
    val opRooks: Bitboard   = rookBitboard(opColor)
    val opBishops: Bitboard = bishopBitboard(opColor)
    val opQueen: Bitboard   = queenBitboard(opColor)

    (
      (pawnsAttacksMap(color)(squareOfKing)      & opPawns)
        | (knightAttacksVector(squareOfKing)     & opKnights)
        | (bishopAttacks(squareOfKing, occupied) & opBishops)
        | (rookAttacks(squareOfKing, occupied)   & opRooks)
        | (queenAttacks(squareOfKing, occupied)  & opQueen)
      ) != 0
  }

  def isSquareAttacked(sq: Int, color: Color): Boolean = {
    val opColor = color.opponentColor

    val opPawns: Bitboard   = pawnBitboard(opColor)
    val opKnights: Bitboard = knightBitboard(opColor)
    val opRooks: Bitboard   = rookBitboard(opColor)
    val opBishops: Bitboard = bishopBitboard(opColor)
    val opQueen: Bitboard   = queenBitboard(opColor)

    val result = (pawnsAttacksMap(color)(sq) & opPawns) |
      (knightAttacksVector(sq)     & opKnights) |
      (bishopAttacks(sq, occupied) & opBishops) |
      (rookAttacks(sq, occupied)   & opRooks) |
      (queenAttacks(sq, occupied)  & opQueen)


    result != 0
  }

  def getPawnSinglePush(sq: Int, color: Color): Bitboard = {
    val b = 1L << sq
    color match {
      case White => wSinglePushPawns(whitePawnBitboard, empty) & northOne(b)
      case Black => bSinglePushPawns(blackPawnBitboard, empty) & southOne(b)
    }
  }

  def getPawnDoublePush(sq: Int, color: Color): Bitboard = {
    val b = 1L << sq
    color match {
      case White => wDoublePushPawns(whitePawnBitboard, empty) & northOne(northOne(b))
      case Black => bDoublePushPawns(blackPawnBitboard, empty) & southOne(southOne(b))
    }
  }
}

object BitboardRepresentation {
  type Bitboard = Long

  def fromBoard(board: Board): BitboardRepresentation = {
    var wPawn   = BitboardOperations.EmptyBitboard
    var wKnight = BitboardOperations.EmptyBitboard
    var wBishop = BitboardOperations.EmptyBitboard
    var wRook   = BitboardOperations.EmptyBitboard
    var wQueen  = BitboardOperations.EmptyBitboard
    var wKing   = BitboardOperations.EmptyBitboard

    var bPawn   = BitboardOperations.EmptyBitboard
    var bKnight = BitboardOperations.EmptyBitboard
    var bBishop = BitboardOperations.EmptyBitboard
    var bRook   = BitboardOperations.EmptyBitboard
    var bQueen  = BitboardOperations.EmptyBitboard
    var bKing   = BitboardOperations.EmptyBitboard

    for (i <- 0 until 64) {
      board.boardRepresentation(i) match {
        case Some(Pawn(White))   => wPawn   |= 1L << i
        case Some(Knight(White)) => wKnight |= 1L << i
        case Some(Bishop(White)) => wBishop |= 1L << i
        case Some(Rook(White))   => wRook   |= 1L << i
        case Some(Queen(White))  => wQueen  |= 1L << i
        case Some(King(White))   => wKing   |= 1L << i

        case Some(Pawn(Black))   => bPawn   |= 1L << i
        case Some(Knight(Black)) => bKnight |= 1L << i
        case Some(Bishop(Black)) => bBishop |= 1L << i
        case Some(Rook(Black))   => bRook   |= 1L << i
        case Some(Queen(Black))  => bQueen  |= 1L << i
        case Some(King(Black))   => bKing   |= 1L << i
        case None                =>
      }
    }

    new BitboardRepresentation(
      Vector(
        wPawn, wKnight, wBishop, wRook, wQueen, wKing,
        bPawn, bKnight, bBishop, bRook, bQueen, bKing
      )
    )
  }

  /**
   * Function that get from semilegal moves legal moves.
   * @param semiLegalMoves - semilegal moves.
   * @return list of legal moves
   */
  def getLegalMoves(board: Board, semiLegalMoves: List[Move], color: Color): List[Move] = {
    semiLegalMoves
      .map(board.makeMove)
      .map(fromBoard)
      .map(_.isKingChecked(color))
      .zip(semiLegalMoves)
      .collect { case (false, move) => move }
  }

  def movesFrom(sq: Int, movesBitboard: Bitboard, moveType: MoveType = Normal): List[Move] = {
    var moves: List[Move] = List.empty
    var bb = movesBitboard

    while (bb != 0) {
      val lsb = bitScanForward(bb)
      bb &= ~(1L << lsb)
      moves = Move(sq, lsb, moveType) :: moves
    }

    moves
  }
}
