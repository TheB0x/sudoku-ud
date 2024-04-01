package com.componentes.sudoku.view

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.componentes.sudoku.R
import com.componentes.sudoku.databinding.ActivityMainBinding
import com.componentes.sudoku.model.Cell
import com.componentes.sudoku.viewmodel.PlaySudokuViewModel

class MainActivity : ComponentActivity(), BoardView.OnTouchListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: PlaySudokuViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val oneButton = findViewById<Button>(R.id.OneBtn)
        val twoButton = findViewById<Button>(R.id.TwoBtn)
        val threeButton = findViewById<Button>(R.id.ThreeBtn)
        val fourButton = findViewById<Button>(R.id.FourBtn)
        val fiveButton = findViewById<Button>(R.id.FiveBtn)
        val sixButton = findViewById<Button>(R.id.SixBtn)
        val sevenButton = findViewById<Button>(R.id.SevenBtn)
        val eightButton = findViewById<Button>(R.id.EightBtn)
        val nineButton = findViewById<Button>(R.id.NineBtn)

        binding.BoardView.registerListener(this)

        viewModel = ViewModelProvider(this)[PlaySudokuViewModel::class.java]
        viewModel.sudokuModel.selectedCellLiveData.observe(this, Observer {
            updateSelecteCellUI(it)
        })
        viewModel.sudokuModel.cellsliveData.observe(
            this,
            Observer{
                updateCells(it)
            }
        )

        val buttons = listOf(
            oneButton,
            twoButton,
            threeButton,
            fourButton,
            fiveButton,
            sixButton,
            sevenButton,
            eightButton,
            nineButton
        )

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener{
                viewModel.sudokuModel.handleInput(index+1)
            }
        }

    }

    override fun onCellTouched(row: Int, column: Int) {
        viewModel.sudokuModel.updateSelectedCell(row, column)
    }

    private fun updateSelecteCellUI(cell: Pair<Int, Int>?) = cell?.let {
        binding.BoardView.updateSelectedUI(cell.first, cell.second)
    }

    fun updateCells(cells: List<Cell>?) = cells?.let{
        binding.BoardView.updateCells(cells)
    }
}
