package common.core.model

sealed trait Figure {
  val charRepresentation: Char
  val color: Color
}

object Figure {
  def getPieceCharByColor(color: Color, whiteChar: Char, blackChar: Char): Char = color match {
    case White => whiteChar
    case Black => blackChar
  }

  def getPieceByChar(figure: Char): Option[Figure] = figure match {
    case 'p' => Some(Pawn(Black))
    case 'P' => Some(Pawn(White))
    case 'n' => Some(Knight(Black))
    case 'N' => Some(Knight(White))
    case 'b' => Some(Bishop(Black))
    case 'B' => Some(Bishop(White))
    case 'r' => Some(Rook(Black))
    case 'R' => Some(Rook(White))
    case 'q' => Some(Queen(Black))
    case 'Q' => Some(Queen(White))
    case 'k' => Some(King(Black))
    case 'K' => Some(King(White))
    case _   => None
  }
}

case class Pawn(color: Color) extends Figure {
  override val charRepresentation: Char = Figure.getPieceCharByColor(color, 'P', 'p')
}

case class Knight(color: Color) extends Figure {
  override val charRepresentation: Char = Figure.getPieceCharByColor(color, 'N', 'n')
}

case class Bishop(color: Color) extends Figure {
  override val charRepresentation: Char = Figure.getPieceCharByColor(color, 'B', 'b')
}

case class Rook(color: Color) extends Figure {
  override val charRepresentation: Char = Figure.getPieceCharByColor(color, 'R', 'r')
}

case class Queen(color: Color) extends Figure {
  override val charRepresentation: Char = Figure.getPieceCharByColor(color, 'Q', 'q')
}

case class King(color: Color) extends Figure {
  override val charRepresentation: Char = Figure.getPieceCharByColor(color, 'K', 'k')
}