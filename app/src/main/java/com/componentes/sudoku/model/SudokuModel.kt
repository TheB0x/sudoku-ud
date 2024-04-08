package com.componentes.sudoku.model

import androidx.lifecycle.MutableLiveData
import kotlin.random.Random

class SudokuModel {
    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsliveData = MutableLiveData<List<Cell>>()
    val isTakingNotesLiveData = MutableLiveData<Boolean>()
    val highlightedKeysLiveData = MutableLiveData<Set<Int>>()

    private var selectedRow = -1
    private var selectedColumn = -1
    private var isTakingNotes = false


    private var board: Board

    init {
        // Generar y resolver el tablero solo una vez al inicio de la aplicación
        //generateAndSetNewBoard()

        val newBoard = SudokuUtils.generateRandomSudokuBoard()
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


        //selectedCellLiveData.postValue(Pair(selectedRow, selectedColumn))
        /*
        val cells = List(9*9){
            i -> Cell(
                i/9,
                i%9,
                i%9
            )
        }


        cells[11].isStartingCell = true
        cells[21].isStartingCell = true
        */
        //cells[0].notes = mutableSetOf(1,2,3,4,5,6,7,8,9)
        //board = Board(9, cells)
        //isTakingNotesLiveData.postValue(isTakingNotes)
        //selectedCellLiveData.postValue(Pair(selectedColumn,selectedRow))
        //cellsliveData.postValue(board.cells)

    }

    fun handleInput(number:Int){
        if(selectedRow == -1 || selectedColumn == -1) return
        val cell = board.getCell(selectedRow, selectedColumn)
        if(cell.isStartingCell) return

        if (isTakingNotes){
            if (cell.notes.contains(number)){
                cell.notes.remove(number)
            }else{
                cell.notes.add(number)
            }
            highlightedKeysLiveData.postValue(cell.notes)
        }else {
            cell.value = number
        }
        cellsliveData.postValue(board.cells)
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


    fun delete(){
        /*
        val cell = board.getCell(selectedRow, selectedColumn)
        if(isTakingNotes){
            cell.notes.clear()
            highlightedKeysLiveData.postValue(setOf())
        }else{
            cell.value = 0
        }
        cellsliveData.postValue(board.cells)
        */
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

    fun generateAndSetNewBoard(){
        val newBoard = SudokuUtils.generateRandomSudokuBoard()
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