package com.componentes.sudoku.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import kotlin.math.min

class BoardView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    // initialized var
    private val size3x3 = 3
    private val size = 9

    private var cellSizePixels = 0F

    // -1 at start don't show in screen
    private var selectedRow    = -1
    private var selectedColumn = -1

    private var listener:BoardView.OnTouchListener? = null

    //Graphic lines
    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.DKGRAY
        strokeWidth = 4F
    }

    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#454545")
        strokeWidth = 2F
    }

    //Graphic Color
    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#6EAD3A")
    }

    private val conflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#D3ECC7")
    }
    // On functions
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizePixels, sizePixels)
    }

    // size divide 'tween 9 so it creates 9x9
    override fun onDraw(canvas: Canvas) {
        cellSizePixels = (width/size).toFloat()
        paintingCells(canvas)
        drawLines(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when(event.action){
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x, event.y)
                true
            }
            else -> false
        }
    }

    //handler
    private fun handleTouchEvent(x: Float, y: Float){
        val maySelectedColumn = (x / cellSizePixels).toInt()
        val maySelectedRow = (y / cellSizePixels).toInt()
        listener?.onCellTouched(maySelectedRow, maySelectedColumn)
    }

    // painting cells functions
    private fun paintingCells(canvas: Canvas){
        if (selectedColumn == -1 || selectedRow == -1) return

        for (rows in 0..size){
            for (columns in 0..size){
                if (rows == selectedRow && columns == selectedColumn){
                    // selected one
                    fillCell(canvas, rows, columns, selectedCellPaint)

                } else if (rows == selectedRow || columns == selectedColumn){
                    // rows 'n' columns aligned
                    fillCell(canvas, rows, columns, conflictingCellPaint)

                } else if (rows / size3x3 == selectedRow / size3x3
                                && columns / size3x3 == selectedColumn / size3x3){
                    // square 3x3 where is selected
                    fillCell(canvas, rows, columns, conflictingCellPaint)
                }
            }
        }
    }

    private fun fillCell(canvas: Canvas, row: Int, column: Int, paint: Paint){
        canvas.drawRect(
            column * cellSizePixels,
            row * cellSizePixels,
            (column + 1) * cellSizePixels,
            (row + 1) * cellSizePixels,
            paint
        )
    }

    private fun drawLines(canvas: Canvas){
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), thickLinePaint)

        for (lines in 1 until size){
            val paintToUse = when (lines%size3x3){
                0 -> thickLinePaint
                else -> thinLinePaint
            }
            // Vertical
            canvas.drawLine(
                lines * cellSizePixels,
                0F,
                lines * cellSizePixels,
                height.toFloat(),
                paintToUse
            )
            // Horizontal
            canvas.drawLine(
                0F,
                lines * cellSizePixels,
                width.toFloat(),
                lines * cellSizePixels,
                paintToUse
            )
        }
    }

    fun updateSelectedUI(row: Int, column: Int){
        selectedRow = row
        selectedColumn = column
        invalidate()
    }

    fun registerListener(listener: BoardView.OnTouchListener){
        this.listener = listener
    }


    // INTERFACE Control Listener
    interface OnTouchListener {
        fun onCellTouched(row: Int, column: Int)
    }
}