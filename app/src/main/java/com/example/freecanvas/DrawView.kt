package com.example.freecanvas

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

//used as a view not as an activity
class DrawView(context: Context, attrs: AttributeSet):View (context,attrs){

    private var drawPath:CustomPath?=null
    private var canvasMap:Bitmap?=null
    private var paintType:Paint?=null
    private var canvasPaint:Paint?=null
    private var brushSize:Float=10.toFloat()
    private var colour=Color.BLACK
    private var canvas:Canvas?=null
    private var paths=ArrayList<CustomPath>()
    private val undoPaths=ArrayList<CustomPath>()

    init{
        InitialSetup()
    }

    private fun InitialSetup(){
        paintType= Paint()
        drawPath=CustomPath(colour,brushSize)
        paintType!!.color=colour
        paintType!!.style=Paint.Style.STROKE
        paintType!!.strokeJoin=Paint.Join.ROUND
        paintType!!.strokeCap=Paint.Cap.ROUND
        canvasPaint=Paint(Paint.DITHER_FLAG)
        //brushSize=20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasMap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        if (!drawPath!!.isEmpty) {
            canvas = Canvas(canvasMap!!)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(canvasMap!!, 0f,0f, canvasPaint)
        for(path in paths){
            paintType!!.strokeWidth=path!!.thickness
            paintType!!.color=path!!.color
            canvas.drawPath(path!!,paintType!!)
        }
        if (!drawPath!!.isEmpty) {
            paintType!!.strokeWidth=drawPath!!.thickness
            paintType!!.color=paintType!!.color
            canvas.drawPath(drawPath!!, paintType!!)
        }

    }

    fun onUndo(){
        if(paths.size>0){
            undoPaths.add(paths.removeAt(paths.size-1))
            invalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX=event?.x;
        val touchY=event?.y

        when(event?.action){
            MotionEvent.ACTION_DOWN ->{
                drawPath!!.color=colour
                drawPath!!.thickness=brushSize
                drawPath!!.reset()
                drawPath!!.moveTo(touchX!!,touchY!!)
            }
            MotionEvent.ACTION_MOVE->{
                drawPath!!.lineTo(touchX!!,touchY!!)
            }
            MotionEvent.ACTION_UP->{
                paths.add(drawPath!!)
                drawPath=CustomPath(colour,brushSize)
            }
            else ->return false
        }
        invalidate()
        return true

    }

    fun setBrushSize(newSize: Float){
        brushSize=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,newSize,resources.displayMetrics)
        paintType!!.strokeWidth=brushSize
    }

    fun setColour(newColor: Int){
        colour=newColor
    }

    internal inner class CustomPath(var color: Int, var thickness: Float) : Path(){

    }
}