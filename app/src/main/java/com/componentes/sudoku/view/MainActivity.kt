package com.componentes.sudoku.view

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.componentes.sudoku.R
import com.componentes.sudoku.databinding.ActivityMainBinding
import com.componentes.sudoku.model.Cell
import com.componentes.sudoku.model.Difficulty
import com.componentes.sudoku.viewmodel.PlaySudokuViewModel

class MainActivity : ComponentActivity(), BoardView.OnTouchListener, RadioGroup.OnCheckedChangeListener {

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

    lateinit var radMed : RadioButton
    lateinit var radEasy : RadioButton
    lateinit var radHard : RadioButton
    lateinit var radGroup: RadioGroup

    lateinit var menuButton: Button

    lateinit var sound: MediaPlayer

    //need to be dinamic!!!
    lateinit var LEVEL : Difficulty

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
        binding.include.settingsIcon.setOnClickListener{
            menu()

        }
        menu()


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
                val sounded = viewModel.sudokuModel.handleInput(index+1)
                if(sounded){
                    makeSound(index+1)
                }
                // Validación de si ha ganado el juego por cada acción de un boton numérico
                val status = viewModel.sudokuModel.isBoardComplete(viewModel.sudokuModel.board.cells)
                if (ATTEMPS < ATTEMPS_LIMIT && status) {
                    showWinnerDialog()
                }
            }

        }
        //notesButton.setOnClickListener { viewModel.sudokuModel.changeNoteTakingState() }
        deleteButton.setOnClickListener{
            viewModel.sudokuModel.delete() }


        /*
        El observer esta a la escucha de cualquier cambio en la variable de intentos del
        sudokuViewModel
         */
        viewModel.sudokuModel.intentosLiveData.observe(this, Observer { intentos ->
            validateGameStatus(intentos)
        })


    }



    fun menu(){
        val builder = AlertDialog.Builder(this@MainActivity)
        val view = layoutInflater.inflate(R.layout.initial_menu, null)

        builder.setView(view)

        val floatingMenu = builder.create()

        floatingMenu.show()

        radGroup = view.findViewById(R.id.difficult_selector)
        radEasy = view.findViewById(R.id.rB_easy)
        radMed = view.findViewById(R.id.rB_medium)
        radHard = view.findViewById(R.id.rb_hard)
        radGroup.setOnCheckedChangeListener(this)

        menuButton = view.findViewById(R.id.btn_ok)
        menuButton.setOnClickListener{
            validateLevel(LEVEL)
            floatingMenu.hide()
        }

    }


    /*
    Se configura y muestra el cuadro de dialogo de Partida Ganada
     */
    fun showWinnerDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡Felicidades!")
            .setMessage("¡Has ganado la partida!")
            .setPositiveButton("OK") { _, _ ->
                menu()
                //viewModel.sudokuModel.generateAndSetNewBoard(LEVEL)
            }
            .setCancelable(false) // Evita que el usuario cierre el diálogo al tocar fuera de él
        val dialog = builder.create()
        dialog.show()
    }



    /*
    Se configura y muestra el cuadro de dialogo de Partida Perdida
     */

    private fun showGameOverDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡Perdiste!")
        builder.setMessage("Has superado los 5 intentos. ¿Quieres reiniciar el juego?")

        builder.setPositiveButton("Seguir") { _,_ ->
            // Reiniciar el juego
            menu()
            //viewModel.sudokuModel.generateAndSetNewBoard(LEVEL)
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

    // radiobutton configuration
    override fun onCheckedChanged(p0: RadioGroup?, idRadio: Int) {
        when (idRadio){
            radEasy?.id -> LEVEL = Difficulty.FACIL
            radMed?.id -> LEVEL = Difficulty.MEDIO
            radHard?.id -> LEVEL = Difficulty.DIFICIL
        }
    }

    /*
    Esta función recibe el nivel de dificultad
     */
    private fun validateLevel(level:Difficulty){
        viewModel.sudokuModel.generateAndSetNewBoard(level)
    }

    /*

    /*
    Esta función hace que vibre el celular cuando una no es correcto el número
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
        // Aquí obtiene el número seleccionado por el usuario
        val number = view.tag.toString().toInt()

        // Llamar a la función en SudokuModel para procesar el número seleccionado
        val isCorrectMove = viewModel.sudokuModel.handleInput(number)

        // Verificar si el movimiento es correcto o no
        if (!isCorrectMove) {
            vibrateDevice()
        }
    }

     */

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





    //
    //
    //
    fun makeSound(number: Int){

        /*val sound1 = MediaPlayer.create(this, R.raw.uno,)
        val sound2 = MediaPlayer.create(this, R.raw.dos,)
        val sound3 = MediaPlayer.create(this, R.raw.tres,)
        val sound4 = MediaPlayer.create(this, R.raw.cuatro,)
        val sound5 = MediaPlayer.create(this, R.raw.cinco,)
        val sound6 = MediaPlayer.create(this, R.raw.seis,)
        val sound7 = MediaPlayer.create(this, R.raw.siete,)
        val sound8 = MediaPlayer.create(this, R.raw.ocho,)
        val sound9 = MediaPlayer.create(this, R.raw.nueve,)*/


        when(number){
            1 -> {
                sound = MediaPlayer.create(this, R.raw.uno,)
                sound.start()
            }
            2 -> {
                sound = MediaPlayer.create(this, R.raw.dos,)
                sound.start()            }
            3 -> {
                sound = MediaPlayer.create(this, R.raw.tres,)
                sound.start()            }
            4 -> {
                sound = MediaPlayer.create(this, R.raw.cuatro,)
                sound.start()            }
            5 -> {
                sound = MediaPlayer.create(this, R.raw.cinco,)
                sound.start()            }
            6 -> {
                sound = MediaPlayer.create(this, R.raw.seis,)
                sound.start()            }
            7 -> {
                sound = MediaPlayer.create(this, R.raw.siete,)
                sound.start()            }
            8 -> {
                sound = MediaPlayer.create(this, R.raw.ocho,)
                sound.start()            }
            9 -> {
                sound = MediaPlayer.create(this, R.raw.nueve,)
                sound.start()            }
        }

    }

}
