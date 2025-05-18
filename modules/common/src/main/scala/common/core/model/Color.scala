package common.core.model

sealed trait Color {
  val opponentColor: Color
}

case object White extends Color {
  override val opponentColor: Color = Black
}

case object Black extends Color {
  override val opponentColor: Color = White
}
