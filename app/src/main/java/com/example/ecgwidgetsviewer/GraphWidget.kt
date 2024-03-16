package com.example.ecgwidgetsviewer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Size
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

class GraphWidget(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    private var startStop = false
    private var currentCounter = 0
    private var storeWrapper: StoreWrapper? = null
    private val obtain: Obtain
    private val paintLine: Paint
    private val paintLineAfter: Paint
    private val paintRectPrev: Paint
    private val paintCircle: Paint
    private val canvasColor: Int
    private val markerRadius = 12
    private val path: Path
    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null
    private var shiftH = 0
    private var size: Size? = null

    init {
        canvasColor = ContextCompat.getColor(context!!,  R.color.canvas_2)
        val canvasPrevColor =
            ContextCompat.getColor(context,  R.color.canvas_1)
        obtain = Obtain(this, 24)
        paintLine = Paint()
        paintLine.color = Color.BLACK
        paintLine.style = Paint.Style.STROKE
        paintLine.strokeWidth = 2f
        paintLineAfter = Paint()
        paintLineAfter.color = Color.GRAY
        paintLineAfter.style = Paint.Style.STROKE
        paintLineAfter.strokeWidth = 2f
        paintRectPrev = Paint()
        paintRectPrev.color = canvasPrevColor
        paintRectPrev.style = Paint.Style.FILL
        paintCircle = Paint()
        paintCircle.color = Color.RED // Set color as per your requirement
        paintCircle.style = Paint.Style.FILL
        path = Path()
    }

    fun setMode(seriesLength: Int, mode: GraphMode?) {
        storeWrapper = StoreWrapper(seriesLength, 5, mode!!)
    }

    fun setMode(mode: GraphMode?) {
        storeWrapper!!.setMode(mode!!)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap!!)
        size = Size(w, h)
        shiftH = h / 6
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(canvasColor)
        if (currentCounter > 0) {
            storeWrapper!!.prepareDrawing(size!!, shiftH.toDouble())
            drawProcedure(size, canvas)
        }
        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
        canvas.drawPath(path, paintLine)
    }

    fun drawProcedure(size: Size?, canvas: Canvas) {
        if (storeWrapper!!.mode() === GraphMode.overlay) {
            drawOverlayGraph(canvas, size)
        } else {
            drawFlowingGraph(canvas)
        }
    }

    fun drawFlowingGraph(canvas: Canvas) {
        canvas.drawPath(storeWrapper!!.pathBefore!!, paintLine)
        canvas.drawPath(storeWrapper!!.pathAfter!!, paintLine)
        if (!storeWrapper!!.isFull()) {
            canvas.drawCircle(
                storeWrapper!!.point!!.x.toFloat(),
                storeWrapper!!.point!!.y.toFloat(), markerRadius.toFloat(), paintCircle
            )
        }
    }

    fun drawOverlayGraph(canvas: Canvas, size: Size?) {
        canvas.drawRect(
            storeWrapper!!.point!!.x.toFloat(),
            0f,
            size!!.width.toFloat(),
            size.height.toFloat(),
            paintRectPrev
        )
        canvas.drawPath(storeWrapper!!.pathBefore!!, paintLine)
        canvas.drawPath(storeWrapper!!.pathAfter!!, paintLineAfter)
        canvas.drawCircle(
            storeWrapper!!.point!!.x.toFloat(),
            storeWrapper!!.point!!.y.toFloat(), markerRadius.toFloat(), paintCircle
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Handle tap event
        if (event.action == MotionEvent.ACTION_UP) {
            startStop = !startStop
            if (startStop) {
                obtain.start()
            } else {
                obtain.stop()
            }
        }
        return true
    }

    fun stop() {
        if (startStop) {
            startStop = false
            obtain.stop()
        }
    }

    fun start() {
        if (!startStop) {
            startStop = true
            obtain.start()
        }
    }

    fun clearCanvas() {
        canvas!!.drawColor(Color.WHITE) // Clear canvas by filling it with white color
        invalidate() // Redraw the view
    }

    fun update(counter: Int) {
        currentCounter = counter
        storeWrapper!!.updateBuffer(currentCounter)
        obtain.setState(storeWrapper!!.drawingFrequency())
        postInvalidate() // Redraw the view
    }
}

