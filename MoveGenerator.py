# MoveGenerator.py

class MoveGenerator:
    """
    Класс MoveGenerator предназначен для генерации возможных ходов в игре.
    """

    def __init__(self, board_state):
        """
        Инициализирует объект MoveGenerator с заданным состоянием доски.

        :param board_state: текущее состояние доски, представленное в виде списка или другого удобного формата.
        """
        self.board_state = board_state

    def generate_moves(self):
        """
        Генерирует все возможные ходы на текущей доске.

        :return: список возможных ходов в формате (from_position, to_position).
        """
        # Здесь должен быть код для генерации ходов
        # ...
        return []

    def is_valid_move(self, move):
        """
        Проверяет, является ли указанный ход допустимым на текущей доске.

        :param move: ход в формате (from_position, to_position).
        :return: True, если ход допустим, False - иначе.
        """
        # Здесь должен быть код для проверки допустимости хода
        # ...
        return False