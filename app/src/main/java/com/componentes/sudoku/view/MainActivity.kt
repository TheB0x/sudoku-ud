package com.componentes.sudoku.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.componentes.sudoku.databinding.ActivityMainBinding
import com.componentes.sudoku.viewmodel.PlaySudokuViewModel

class MainActivity : ComponentActivity(), BoardView.OnTouchListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: PlaySudokuViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.BoardView.registerListener(this)

        viewModel = ViewModelProvider(this)[PlaySudokuViewModel::class.java]
        viewModel.sudokuModel.selectedCellLiveData.observe(this, Observer {
            updateSelecteCellUI(it)
        })

    }

    override fun onCellTouched(row: Int, column: Int) {
        viewModel.sudokuModel.updateSelectedCell(row, column)
    }

    private fun updateSelecteCellUI(cell: Pair<Int, Int>?) = cell?.let {
        binding.BoardView.updateSelectedUI(cell.first, cell.second)
    }
}
