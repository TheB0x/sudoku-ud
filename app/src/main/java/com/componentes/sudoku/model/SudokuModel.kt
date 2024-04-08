package com.componentes.sudoku.model

import android.widget.TextView
import androidx.lifecycle.MutableLiveData

class SudokuModel {
    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsliveData = MutableLiveData<List<Cell>>()
    //val isTakingNotesLiveData = MutableLiveData<Boolean>()
    val highlightedKeysLiveData = MutableLiveData<Set<Int>>()
    val intentosLiveData = MutableLiveData<Int>()

    // Actualizar el LiveData cada vez que intentos cambie
    var intentos = 0
        set(value) {
            field = value
            // Actualizar el LiveData cada vez que intentos cambie
            intentosLiveData.postValue(value)
        }
    //val level = Difficulty()
    private var selectedRow = -1
    private var selectedColumn = -1
    private var isTakingNotes = false
    lateinit var board: Board

    /*
    init {
        // Generar y resolver el tablero solo una vez al inicio de la aplicación
        generateAndSetNewBoard()
        selectedCellLiveData.postValue(Pair(selectedRow, selectedColumn))

        val cells = List(9*9){
            i -> Cell(
                i/9,
                i%9,
                i%9
            )
        }

        cells[11].isStartingCell = true
        cells[21].isStartingCell = true

        cells[0].notes = mutableSetOf(1,2,3,4,5,6,7,8,9)
        board = Board(9, cells)
        isTakingNotesLiveData.postValue(isTakingNotes)
        selectedCellLiveData.postValue(Pair(selectedColumn,selectedRow))
        cellsliveData.postValue(board.cells)

    }

     */

    fun handleInput(number:Int):Boolean{
        if (selectedRow == -1 || selectedColumn == -1) return false
        val cell = board.getCell(selectedRow, selectedColumn)
        if (cell.isStartingCell) return false

        val isCorrect = isValidMove(selectedRow, selectedColumn, number)

        if (isCorrect) {
            if (isTakingNotes) {
                if (cell.notes.contains(number)) {
                    cell.notes.remove(number)
                } else {
                    cell.notes.add(number)
                }
                highlightedKeysLiveData.postValue(cell.notes)
            } else {
                cell.value = number
            }
            cellsliveData.postValue(board.cells)
        }

        return isCorrect
    }

    /*
    Permite verificar si el número ingresado es válido.
     */
    private fun isValidMove(row: Int, col: Int, num: Int): Boolean {
        val cell = board.getCell(row, col)
        // Si la celda es una celda inicial, no permitir cambios
        if (cell.isStartingCell) { return false }
        // Si el número ya está presente en la celda, permitir el movimiento
        if (cell.value == num) { return true }
        // Verificar si el número ya está presente en la fila, columna ni subcuadrado 3x3
        var isError = false
        if (isNumberInRow(row, num) || isNumberInColumn(col, num) || isNumberInSubgrid(row, col, num)) {
            isError = true
        }
        // Incrementar la cantidad de intentos si hay un error
        if (isError) { intentos++ }
        // Si el número no está presente en la fila, columna ni subcuadrado 3x3, permitir el movimiento
        return !isError
    }

    private fun isNumberInRow(row: Int, num: Int): Boolean {
        for (col in 0 until 9) {
            if (board.getCell(row, col).value == num) {
                return true
            }
        }
        return false
    }

    private fun isNumberInColumn(col: Int, num: Int): Boolean {
        for (row in 0 until 9) {
            if (board.getCell(row, col).value == num) {
                return true
            }
        }
        return false
    }

    private fun isNumberInSubgrid(row: Int, col: Int, num: Int): Boolean {
        val subGridRowStart = row - row % 3
        val subGridColStart = col - col % 3
        for (i in subGridRowStart until subGridRowStart + 3) {
            for (j in subGridColStart until subGridColStart + 3) {
                if (board.getCell(i, j).value == num) {
                    return true
                }
            }
        }
        return false
    }

    fun updateSelectedCell(row: Int, column: Int){
        val cell = board.getCell(row, column)
        if(!cell.isStartingCell) {
            selectedRow = row
            selectedColumn = column
            selectedCellLiveData.postValue(Pair(row, column))

            if (isTakingNotes){
                highlightedKeysLiveData.postValue(cell.notes)
            }
        }
    }

    /*
    fun changeNoteTakingState(){
        isTakingNotes = !isTakingNotes
        isTakingNotesLiveData.postValue(isTakingNotes)

        val curNote = if(isTakingNotes){
            board.getCell(selectedRow,selectedColumn).notes
        }else{
            setOf()
        }
        highlightedKeysLiveData.postValue(curNote)
    }
    */

    /*
    Esta función permite verificar si el ablero tiene celdas vacíos
     */
    fun isBoardComplete(cells: List<Cell>): Boolean {
        for (cell in cells) {
            if (cell.value == 0) {
                return false // Si alguna celda está vacía, el tablero no está completo
            }
        }
        return true
    }

    fun delete(){
        val cell = board.getCell(selectedRow, selectedColumn)
        if (cell.isStartingCell) {
            // No permitir borrar números en celdas iniciales
            return
        }
        if (isTakingNotes) {
            cell.notes.clear()
            highlightedKeysLiveData.postValue(setOf())
        } else {
            cell.value = 0
        }
        cellsliveData.postValue(board.cells)
    }

    fun generateAndSetNewBoard(level:Difficulty){
        // Cada ve que inicia un nuevo board, reinicia el contador
        intentos=0
        val newBoard = SudokuUtils.generateRandomSudokuBoard(level)
        val cells = mutableListOf<Cell>()

        // Convertir la matriz de Int a una lista de celdas
        for (rowIndex in newBoard.indices) {
            for (colIndex in newBoard[rowIndex].indices) {
                val value = newBoard[rowIndex][colIndex]
                val cell = Cell(rowIndex, colIndex, value, value != 0)
                cells.add(cell)
            }
        }

        // Actualizar las celdas y notificar a los observadores
        cellsliveData.postValue(cells)
        board = Board(9, cells)
    }

}