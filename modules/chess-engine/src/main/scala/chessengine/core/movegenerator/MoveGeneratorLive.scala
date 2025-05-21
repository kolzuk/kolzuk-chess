package chessengine.core.movegenerator

import common.core.model.bitboards.BitboardRepresentation.Bitboard
import common.core.model.CastleType._
import common.core.model.MoveType._
import common.core.model._
import common.core.model.bitboards.{BitboardOperations, BitboardRepresentation}

class MoveGeneratorLive extends MoveGenerator {
  import BitboardOperations._

  override def generateMoves(board: Board): List[Move] = {
    val movingColor = board.activeColor
    implicit val bitboardRepresentation: BitboardRepresentation = BitboardRepresentation.fromBoard(board)
    implicit val implicitBoard: Board = board

    (0 until 64).flatMap { sq =>
      board.boardRepresentation(sq) match {
        case Some(figure) if figure.color == movingColor =>
          figure match {
            case Pawn(_)   => generatePawnMoves(sq, movingColor) ++
              generatePromotionMoves(sq, movingColor) ++
              generateEnPassantMoves(sq, movingColor)
            case Knight(_) => generateKnightMoves(sq, movingColor)
            case Bishop(_) => generateBishopMoves(sq, movingColor)
            case Rook(_)   => generateRookMoves(sq, movingColor)
            case Queen(_)  => generateQueenMoves(sq, movingColor)
            case King(_)   => generateKingMoves(sq, movingColor)
            case _ => List.empty
          }
        case _ => List.empty
      }
    }.toList ++ generateCastleMoves(board.activeColor)
  }

  private def generatePawnMoves(sq: Int, color: Color)(implicit br: BitboardRepresentation, board: Board): List[Move] = {
    val pawnAttacksBitboard = pawnsAttacksMap(color)(sq) & br.occupiedByOppositeColorBitboard(color)
    val pawnSinglePushBitboard = br.getPawnSinglePush(sq, color)
    val pawnDoublePushBitboard = br.getPawnDoublePush(sq, color)
    val pawnMoves: Bitboard = (pawnAttacksBitboard | pawnSinglePushBitboard | pawnDoublePushBitboard) &
      ~(BitboardOperations.Rank1 | BitboardOperations.Rank8)

    val semiLegalMoves: List[Move] = BitboardRepresentation.movesFrom(sq, pawnMoves)
    val legalMoves = BitboardRepresentation.getLegalMoves(board, semiLegalMoves, color)

    legalMoves
  }

  private def generatePromotionMoves(sq: Int, color: Color)(implicit br: BitboardRepresentation, board: Board): List[Move] = {
    if (color == Color.White && (sq < 48 || sq > 55)) return List.empty
    if (color == Color.Black && (sq < 8 || sq > 15)) return List.empty

    val pawnAttacksBitboard = pawnsAttacksMap(color)(sq) & br.occupiedByOppositeColorBitboard(color)
    val pawnSinglePushBitboard = br.getPawnSinglePush(sq, color)
    val pawnMoves: Bitboard = pawnAttacksBitboard | pawnSinglePushBitboard

    val semiLegalMoves: List[Move] =
      BitboardRepresentation.movesFrom(sq, pawnMoves, Promotion(Queen(color))) ++
        BitboardRepresentation.movesFrom(sq, pawnMoves, Promotion(Knight(color))) ++
        BitboardRepresentation.movesFrom(sq, pawnMoves, Promotion(Bishop(color))) ++
        BitboardRepresentation.movesFrom(sq, pawnMoves, Promotion(Rook(color)))

    val legalMoves = BitboardRepresentation.getLegalMoves(board, semiLegalMoves, color)

    legalMoves
  }

  private def generateEnPassantMoves(sq: Int, color: Color)(implicit br: BitboardRepresentation, board: Board): List[Move] = {
    if (board.enPassantTargetSquare.isEmpty)
      return List.empty

    val normalMoves: Bitboard = pawnsAttacksMap(color)(sq) &
      (1L << board.enPassantTargetSquare.get)

    val semiLegalMoves: List[Move] = BitboardRepresentation.movesFrom(sq, normalMoves, EnPassant)
    val legalMoves = BitboardRepresentation.getLegalMoves(board, semiLegalMoves, color)

    legalMoves
  }

  private def generateKnightMoves(sq: Int, color: Color)(implicit br: BitboardRepresentation, board: Board): List[Move] = {
    val movedBitboard = knightAttacksVector(sq) & ~br.occupiedByColorBitboard(color)
    val semiLegalMoves: List[Move] = BitboardRepresentation.movesFrom(sq, movedBitboard)
    val legalMoves: List[Move] = BitboardRepresentation.getLegalMoves(board, semiLegalMoves, color)

    legalMoves
  }

  private def generateBishopMoves(sq: Int, color: Color)(implicit br: BitboardRepresentation, board: Board): List[Move] = {
    val movedBitboard = bishopAttacks(sq, br.occupied) & ~br.occupiedByColorBitboard(color)
    val semiLegalMoves: List[Move] = BitboardRepresentation.movesFrom(sq, movedBitboard)
    BitboardRepresentation.getLegalMoves(board, semiLegalMoves, color)
  }

  private def generateRookMoves(sq: Int, color: Color)(implicit br: BitboardRepresentation, board: Board): List[Move] = {
    val movedBitboard = rookAttacks(sq, br.occupied) & ~br.occupiedByColorBitboard(color)
    val semiLegalMoves: List[Move] = BitboardRepresentation.movesFrom(sq, movedBitboard)
    BitboardRepresentation.getLegalMoves(board, semiLegalMoves, color)
  }

  private def generateQueenMoves(sq: Int, color: Color)(implicit br: BitboardRepresentation, board: Board): List[Move] = {
    val movedBitboard = queenAttacks(sq, br.occupied) & ~br.occupiedByColorBitboard(color)
    val semiLegalMoves: List[Move] = BitboardRepresentation.movesFrom(sq, movedBitboard)
    BitboardRepresentation.getLegalMoves(board, semiLegalMoves, color)
  }

  private def generateKingMoves(sq: Int, color: Color)(implicit br: BitboardRepresentation, board: Board): List[Move] = {
    val movedBitboard = kingAttacksVector(sq) & ~br.occupiedByColorBitboard(color)
    val semiLegalMoves: List[Move] = BitboardRepresentation.movesFrom(sq, movedBitboard)
    BitboardRepresentation.getLegalMoves(board, semiLegalMoves, color)
  }

  private val f1g1: Bitboard   = 0x0000000000000060L
  private val b1c1d1: Bitboard = 0x000000000000000EL
  private val f8g8: Bitboard   = 0x6000000000000000L
  private val b8c8d8: Bitboard = 0x0E00000000000000L
  private def generateCastleMoves(color: Color)(implicit br: BitboardRepresentation, board: Board): List[Move] = {
    var moves = List.empty[Move]
    color match {
      case Color.White =>
        if (
          ((f1g1 & br.empty) == f1g1) &&
          !br.isSquareAttacked(4, color) &&
          !br.isSquareAttacked(5, color) &&
          !br.isSquareAttacked(6, color) &&
          board.castlingAvailability.whiteCanCastleKingSide
        ) moves = Move(4, 6, Castle(WhiteKingSide)) :: moves

        if (
          ((b1c1d1 & br.empty) == b1c1d1) &&
          !br.isSquareAttacked(4, color) &&
          !br.isSquareAttacked(3, color) &&
          !br.isSquareAttacked(2, color) &&
          board.castlingAvailability.whiteCanCastleQueenSide
        ) moves = Move(4, 2, Castle(WhiteQueenSide)) :: moves
      case Color.Black =>
        if (
          ((f8g8 & br.empty) == f8g8) &&
          !br.isSquareAttacked(60, color) &&
          !br.isSquareAttacked(61, color) &&
          !br.isSquareAttacked(62, color) &&
          board.castlingAvailability.blackCanCastleKingSide
        ) moves = Move(60, 62, Castle(BlackKingSide)) :: moves

        if (
          ((b8c8d8 & br.empty) == b8c8d8) &&
          !br.isSquareAttacked(60, color) &&
          !br.isSquareAttacked(59, color) &&
          !br.isSquareAttacked(58, color) &&
          board.castlingAvailability.blackCanCastleQueenSide
        ) moves = Move(60, 58, Castle(BlackQueenSide)) :: moves
    }

    moves
  }
}
