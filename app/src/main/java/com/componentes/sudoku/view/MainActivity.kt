package com.componentes.sudoku.view

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.componentes.sudoku.R
import com.componentes.sudoku.databinding.ActivityMainBinding
import com.componentes.sudoku.model.Board
import com.componentes.sudoku.model.Cell
import com.componentes.sudoku.viewmodel.PlaySudokuViewModel

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity(), BoardView.OnTouchListener {

    lateinit var oneButton : Button
    lateinit var twoButton : Button
    lateinit var threeButton : Button
    lateinit var fourButton : Button
    lateinit var fiveButton : Button
    lateinit var sixButton : Button
    lateinit var sevenButton : Button
    lateinit var eightButton : Button
    lateinit var nineButton : Button
    lateinit var notesButton : ImageButton
    lateinit var deleteButton : ImageButton


    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: PlaySudokuViewModel
    private lateinit var numberButtons : List<Button>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        oneButton = findViewById(R.id.OneBtn)
        twoButton = findViewById(R.id.TwoBtn)
        threeButton = findViewById(R.id.ThreeBtn)
        fourButton = findViewById(R.id.FourBtn)
        fiveButton = findViewById(R.id.FiveBtn)
        sixButton = findViewById(R.id.SixBtn)
        sevenButton = findViewById(R.id.SevenBtn)
        eightButton = findViewById(R.id.EightBtn)
        nineButton = findViewById(R.id.NineBtn)
        notesButton = findViewById(R.id.notesButton)
        deleteButton = findViewById(R.id.deleteButton)

        binding.BoardView.registerListener(this)

        viewModel = ViewModelProvider(this)[PlaySudokuViewModel::class.java]
        viewModel.sudokuModel.generateAndSolveBoard()
        viewModel.sudokuModel.selectedCellLiveData.observe(this, Observer {updateSelecteCellUI(it)})
        viewModel.sudokuModel.cellsliveData.observe(this,Observer{updateCells(it)})
        viewModel.sudokuModel.isTakingNotesLiveData.observe(this,Observer{ updateNoteTakingUI(it) } )
        viewModel.sudokuModel.highlightedKeysLiveData.observe(this,Observer{ updateHighlightedKeys(it) } )

        numberButtons = listOf(
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

        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener{
                viewModel.sudokuModel.handleInput(index+1)
            }
        }
        //notesButton.setOnClickListener { viewModel.sudokuModel.changeNoteTakingState() }
        deleteButton.setOnClickListener{ viewModel.sudokuModel.delete() }


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

    private fun updateNoteTakingUI(isNoteTaking: Boolean?) = isNoteTaking?.let {
        val color = if (it) ContextCompat.getColor(this, R.color.purple_700) else Color.LTGRAY
        notesButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        /*
        if (it){
            notesButton.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_700))
        }else{
            notesButton.setBackgroundColor(Color.LTGRAY)
        }

         */
    }

    private fun updateHighlightedKeys(set:Set<Int>?) = set?.let {
        numberButtons.forEachIndexed { index, button ->
            val color = if(set.contains(index+1)) ContextCompat.getColor(this,R.color.purple_700) else Color.LTGRAY
            //button.setBackgroundColor(color)
            button.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
    }
}
