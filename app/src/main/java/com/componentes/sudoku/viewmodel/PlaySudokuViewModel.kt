package com.componentes.sudoku.viewmodel

import androidx.lifecycle.ViewModel
import com.componentes.sudoku.model.SudokuModel

class PlaySudokuViewModel: ViewModel() {
    val sudokuModel = SudokuModel()
}