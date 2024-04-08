package com.componentes.sudoku.model

import kotlin.random.Random

object SudokuUtils {

    fun generateRandomSudokuBoard(): Array<IntArray> {
        val board = Array(9) { IntArray(9) }

        // Llenar la diagonal principal con números aleatorios del 1 al 9
        for (i in 0 until 9 step 3) {
            val subgrid = (1..9).shuffled()
            for (j in 0 until 3) {
                for (k in 0 until 3) {
                    board[i + j][i + k] = subgrid[j * 3 + k]
                }
            }
        }

        // Resolver el tablero
        solveSudoku(board)

        // Remover algunos valores para hacerlo un Sudoku jugable
        removeValues(board)

        return board
    }

    private fun solveSudoku(board: Array<IntArray>): Boolean {
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                if (board[i][j] == 0) {
                    for (num in 1..9) {
                        if (isValid(board, i, j, num)) {
                            board[i][j] = num
                            if (solveSudoku(board)) {
                                return true
                            }
                            board[i][j] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun isValid(board: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        for (i in 0 until 9) {
            if (board[row][i] == num || board[i][col] == num ||
                board[row - row % 3 + i / 3][col - col % 3 + i % 3] == num) {
                return false
            }
        }
        return true
    }

    private fun removeValues(board: Array<IntArray>) {
        // Determinar la cantidad de valores a eliminar (aproximadamente)
        val cellsToRemove = Random.nextInt(40, 55)

        // Eliminar valores aleatorios
        var removedCount = 0
        while (removedCount < cellsToRemove) {
            val row = Random.nextInt(9)
            val col = Random.nextInt(9)
            if (board[row][col] != 0) {
                board[row][col] = 0
                removedCount++
            }
        }
    }

    fun printBoard(board: Array<IntArray>) {
        for (row in board) {
            println(row.joinToString(" "))
        }
    }
}