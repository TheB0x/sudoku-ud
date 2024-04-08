package com.componentes.sudoku.model

class SudokuSolver(private val board: Board) {

    fun solve(): Board {
        for (row in 0 until board.size) {
            for (col in 0 until board.size) {
                if (board.getCell(row, col).value == 0) {
                    for (num in 1..9) {
                        if (isValid(row, col, num)) {
                            board.getCell(row, col).value = num
                            //board.getCell(row, col).value = 0
                        }
                    }
                }
            }
        }
        return board
    }

    private fun isValid(row: Int, col: Int, num: Int): Boolean {
        for (i in 0 until board.size) {
            if (board.getCell(row, i).value == num || board.getCell(i, col).value == num) {
                return false
            }
        }

        val subGridRowStart = row - row % 3
        val subGridColStart = col - col % 3
        for (i in subGridRowStart until subGridRowStart + 3) {
            for (j in subGridColStart until subGridColStart + 3) {
                if (board.getCell(i, j).value == num) {
                    return false
                }
            }
        }
        return true
    }
}