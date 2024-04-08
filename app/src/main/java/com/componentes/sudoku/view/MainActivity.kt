package com.componentes.sudoku.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.componentes.sudoku.R
import com.componentes.sudoku.databinding.ActivityMainBinding
import com.componentes.sudoku.model.Cell
import com.componentes.sudoku.model.Difficulty
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
    //lateinit var notesButton : ImageButton
    lateinit var deleteButton : ImageButton
    lateinit var txtIntentos : TextView

    private var LEVEL = Difficulty.FACIL
    private var ATTEMPS_LIMIT = 5
    private var ATTEMPS = 0




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
        //notesButton = findViewById(R.id.notesButton)
        deleteButton = findViewById(R.id.deleteButton)
        txtIntentos = findViewById(R.id.txtIntentos)

        binding.BoardView.registerListener(this)

        viewModel = ViewModelProvider(this)[PlaySudokuViewModel::class.java]
        // Valida el nivel de dificultad
        validateLevel(LEVEL)

        viewModel.sudokuModel.selectedCellLiveData.observe(this, Observer {updateSelecteCellUI(it)})
        viewModel.sudokuModel.cellsliveData.observe(this,Observer{updateCells(it)})
        //viewModel.sudokuModel.isTakingNotesLiveData.observe(this,Observer{ updateNoteTakingUI(it) } )
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
                val status = viewModel.sudokuModel.isBoardComplete(viewModel.sudokuModel.board.cells)
                if (ATTEMPS < ATTEMPS_LIMIT && status) {
                    showWinnerDialog()
                    //Toast.makeText(this,"Ha ganado - ${intentos} and $status",Toast.LENGTH_SHORT).show()
                }
            }

        }
        //notesButton.setOnClickListener { viewModel.sudokuModel.changeNoteTakingState() }
        deleteButton.setOnClickListener{ viewModel.sudokuModel.delete() }


        /*
        El observer esta a la escucha de cualquier cambio en la variable de intentos del
        sudokuViewModel
         */
        viewModel.sudokuModel.intentosLiveData.observe(this, Observer { intentos ->
            validateGameStatus(intentos)
        })
    }
    fun showWinnerDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡Felicidades!")
            .setMessage("¡Has ganado la partida!")
            .setPositiveButton("OK") { _, _ ->
                viewModel.sudokuModel.generateAndSetNewBoard(LEVEL)
            }
            .setCancelable(false) // Evita que el usuario cierre el diálogo al tocar fuera de él
        val dialog = builder.create()
        dialog.show()
    }


    private fun showGameOverDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡Perdiste!")
        builder.setMessage("Has superado los 5 intentos. ¿Quieres reiniciar el juego?")

        builder.setPositiveButton("Seguir") { _,_ ->
            // Reiniciar el juego
            viewModel.sudokuModel.generateAndSetNewBoard(LEVEL)
        }
        builder.setCancelable(false)
        builder.show()
    }


    /*
    Tiene la finalidad de actualizar el TextView
     */
    private fun validateGameStatus(intentos: Int) {
        txtIntentos.text = "$intentos/$ATTEMPS_LIMIT"
        ATTEMPS = intentos
        if (intentos >= ATTEMPS_LIMIT) {
            showGameOverDialog()
        }
    }

    /*
    Esta función recibe el nivel de dificultad
     */
    private fun validateLevel(level:Difficulty){
        viewModel.sudokuModel.generateAndSetNewBoard(level)
    }

    /*
    Esta función habilita que cuando
     */
    private fun vibrateDevice() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
    }
    fun onNumberButtonClicked(view: View) {
        // Aquí obtienes el número seleccionado por el usuario
        val number = view.tag.toString().toInt()

        // Llamar a la función en SudokuModel para procesar el número seleccionado
        val isCorrectMove = viewModel.sudokuModel.handleInput(number)

        // Verificar si el movimiento es correcto o no
        if (!isCorrectMove) {
            // El movimiento es incorrecto, vibrar el dispositivo y cambiar el color del tablero
            vibrateDevice()
            //changeBoardColor()
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

    /*
    private fun updateNoteTakingUI(isNoteTaking: Boolean?) = isNoteTaking?.let {
        val color = if (it) ContextCompat.getColor(this, R.color.purple_700) else Color.LTGRAY
        notesButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        if (it){
            notesButton.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_700))
        }else{
            notesButton.setBackgroundColor(Color.LTGRAY)
        }
    }
    */

    private fun updateHighlightedKeys(set:Set<Int>?) = set?.let {
        numberButtons.forEachIndexed { index, button ->
            val color = if(set.contains(index+1)) ContextCompat.getColor(this,R.color.purple_700) else Color.LTGRAY
            //button.setBackgroundColor(color)
            button.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
    }
}
