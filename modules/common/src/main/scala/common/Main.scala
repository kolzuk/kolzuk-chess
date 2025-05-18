package common

import common.core.utils.ForsythEdwardsNotationUtils

object Main{
  def main(args: Array[String]): Unit = {
    val board = ForsythEdwardsNotationUtils.toBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1").get
  }
}