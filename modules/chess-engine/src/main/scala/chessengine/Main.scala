package chessengine

import chessengine.core.movegenerator.MoveGenerator._
import chessengine.core.movegenerator.{MoveGenerator, MoveGeneratorImpl}
import common.core.model.{Board, Move}
import common.core.utils.ForsythEdwardsNotationUtils

object Main {
  def main(args: Array[String]): Unit = {
    val depth: String = args(0)
    val fen: String = args(1)
    val board = ForsythEdwardsNotationUtils.toBoard(fen)
      .getOrElse(throw new IllegalStateException("Can't parse FEN notation"))

    val newBoard: Board = {
      if (args.length > 2) {
        args(2).split(" ").foldLeft(board) { (b, uci) =>
          b.makeMove(Move.parseFromUCI(uci, b))
        }
      } else {
        board
      }
    }

    implicit val moveGenerator: MoveGenerator = new MoveGeneratorImpl
    perft(depth.toInt, newBoard)
  }
}
