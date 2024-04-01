package com.componentes.sudoku.model

import androidx.lifecycle.MutableLiveData

class SudokuModel {
    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()

    private var selectedRow = -1
    private var selectedColumn = -1
    var cellsliveData = MutableLiveData<List<Cell>>()

    private val board: Board

    init {
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
        board = Board(9, cells)

        selectedCellLiveData.postValue(Pair(selectedColumn,selectedRow))
        cellsliveData.postValue(board.cells)
    }

    fun handleInput(number:Int){
        if(selectedRow == -1 || selectedColumn == -1) return
        if(!board.getCell(selectedRow, selectedColumn).isStartingCell) return

        board.getCell(selectedRow,selectedColumn).value = number
        cellsliveData.postValue(board.cells)
    }

    fun updateSelectedCell(row: Int, column: Int){

        if(!board.getCell(row, column).isStartingCell) {
            selectedRow = row
            selectedColumn = column
            selectedCellLiveData.postValue(Pair(row, column))
        }
    }
}