package com.componentes.sudoku.model

import androidx.lifecycle.MutableLiveData

class SudokuModel {
    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsliveData = MutableLiveData<List<Cell>>()
    val isTakingNotesLiveData = MutableLiveData<Boolean>()
    val highlightedKeysLiveData = MutableLiveData<Set<Int>>()

    private var selectedRow = -1
    private var selectedColumn = -1
    private var isTakingNotes = false


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
        /*
        cells[11].isStartingCell = true
        cells[21].isStartingCell = true
        */
        cells[0].notes = mutableSetOf(1,2,3,4,5,6,7,8,9)
        board = Board(9, cells)
        isTakingNotesLiveData.postValue(isTakingNotes)

        selectedCellLiveData.postValue(Pair(selectedColumn,selectedRow))
        cellsliveData.postValue(board.cells)
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
            setOf<Int>()
        }
        highlightedKeysLiveData.postValue(curNote)

    }


    fun delete(){
        val cell = board.getCell(selectedRow, selectedColumn)
        if(isTakingNotes){
            cell.notes.clear()
            highlightedKeysLiveData.postValue(setOf())
        }else{
            cell.value = 0
        }
        cellsliveData.postValue(board.cells)
    }
}