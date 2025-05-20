package common.core.model

sealed trait Figure {
  val charRepresentation: Char
  val color: Color
}

object Figure {
  def getPieceCharByColor(color: Color, whiteChar: Char, blackChar: Char): Char = color match {
    case Color.White => whiteChar
    case Color.Black => blackChar
  }

  def getPieceByChar(figure: Char): Option[Figure] = figure match {
    case 'p' => Some(Pawn(Color.Black))
    case 'P' => Some(Pawn(Color.White))
    case 'n' => Some(Knight(Color.Black))
    case 'N' => Some(Knight(Color.White))
    case 'b' => Some(Bishop(Color.Black))
    case 'B' => Some(Bishop(Color.White))
    case 'r' => Some(Rook(Color.Black))
    case 'R' => Some(Rook(Color.White))
    case 'q' => Some(Queen(Color.Black))
    case 'Q' => Some(Queen(Color.White))
    case 'k' => Some(King(Color.Black))
    case 'K' => Some(King(Color.White))
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