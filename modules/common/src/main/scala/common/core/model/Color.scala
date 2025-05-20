package common.core.model

sealed trait Color {
  val opponentColor: Color
  val char: Char
}

object Color {
  case object White extends Color {
    override val opponentColor: Color = Black
    override val char: Char = 'w'
  }

  case object Black extends Color {
    override val opponentColor: Color = White
    override val char: Char = 'b'
  }

  def apply(str: String): Color = str match {
    case "w" => White
    case "b" => Black
    case _   => throw new IllegalArgumentException(s"$str must be 'w' or 'b'!")
  }
}
