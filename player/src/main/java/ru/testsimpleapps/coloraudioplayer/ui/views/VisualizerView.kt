package ru.testsimpleapps.coloraudioplayer.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout

import java.util.Random

class VisualizerView : View {
    private var previousTime: Long = 0
    private var isPlaying = false
    private val random = Random(System.currentTimeMillis())

    private var bytes: ByteArray? = null
    private lateinit var points: FloatArray
    private val rect = Rect()
    private val forePaint = Paint()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        bytes = null
        forePaint.strokeWidth = 3f
        forePaint.isAntiAlias = true
        forePaint.color = Color.rgb(0, 255, 0)
        layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
    }

    fun updateVisualizer(bytes: ByteArray, isPlaying: Boolean) {
        this.bytes = bytes
        this.isPlaying = isPlaying
        invalidate()
    }


    /**
     * Draw visualizer with lines
     */
    private fun drawLineEqualizer(canvas: Canvas) {
        if (bytes == null) {
            return
        }

        if (System.currentTimeMillis() - previousTime > FREQUENCY && isPlaying) {
            if (points == null || points!!.size < bytes!!.size * 4) {
                points = FloatArray(bytes!!.size * 4)
            }
            rect.set(0, 0, width, height)
            val middle = rect.height() / 2
            for (i in 0 until bytes!!.size - 1) {
                points[i * 4] = (rect.width() * i / (bytes!!.size - 1)).toFloat()
                points[i * 4 + 1] = (middle + (bytes!![i] + 128).toByte() * middle / 128).toFloat()
                points[i * 4 + 2] = (rect.width() * (i + 1) / (bytes!!.size - 1)).toFloat()
                points[i * 4 + 3] = (middle + (bytes!![i + 1] + 128).toByte() * middle / 128).toFloat()
            }

            forePaint.color = Color.BLUE
            canvas.drawLines(points!!, forePaint)
            previousTime = System.currentTimeMillis()
        }
    }

    /**
     * Draw visualizer with points
     */
    private fun drawPointEqualizer(canvas: Canvas) {
        if (bytes == null) {
            return
        }

        if (System.currentTimeMillis() - previousTime > FREQUENCY && isPlaying) {
            rect.set(0, 0, width, height)
            val middle = rect.height().toFloat() / 2.0f
            var x = 0.0f
            var y = 0.0f
            var r = 0.0f
            for (i in 0 until bytes!!.size - 2) {
                if (i % 16 == 0) {
                    x = rect.width().toFloat() * i / bytes!!.size
                    y = rect.height().toFloat() * (bytes!![i] + 128) / 256
                    r = random.nextInt(rect.height() / 10).toFloat()

                    if (y < middle) {
                        y += 2 * r
                    } else {
                        y -= 2 * r
                    }

                    forePaint.color = Color.BLUE
                    canvas.drawCircle(x, y, r, forePaint)
                }
            }

            previousTime = System.currentTimeMillis()
        }
    }

    /**
     * Draw visualizer with rectangles
     */
    private fun drawRectEqualizer(canvas: Canvas) {
        if (bytes == null) {
            return
        }

        if (System.currentTimeMillis() - previousTime > FREQUENCY && isPlaying) {
            rect.set(0, 0, width, height)
            val numberDraw = bytes!!.size / (DEPENDENCE_RECT_COUNT * 2)
            val border = 2.0f
            val widthRect = rect.width().toFloat() / DEPENDENCE_RECT_COUNT.toFloat()
            val deltaAmplitude = rect.height().toFloat() / 256.0f
            val middle = rect.height().toFloat() / 2.0f
            var j = 0
            var y = 0.0f
            var average = 0.0f
            for (i in bytes!!.indices) {
                if ((i + 1) % numberDraw == 0) {
                    y = deltaAmplitude * average * 2.0f / numberDraw
                    average = 0.0f
                    forePaint.color = Color.BLUE
                    canvas.drawRect(j.toFloat() * widthRect + border, middle - y, j.toFloat() * widthRect + widthRect, middle + y, forePaint)
                    j++
                } else {
                    average += (128 - Math.abs(bytes!![i].toInt())).toFloat()
                }
            }
            previousTime = System.currentTimeMillis()
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //        switch (App.getInstance().getNumberVisualizer()) {
        //            case App.VISUALIZER_NONE:
        //                break;
        //            case App.VISUALIZER_LINE:
        //                drawLineEqualizer(canvas);
        //                break;
        //            case App.VISUALIZER_CIRCLE:
        //                drawPointEqualizer(canvas);
        //                break;
        //            case App.VISUALIZER_RECT:
        //                drawRectEqualizer(canvas);
        //                break;
        //        }
    }

    companion object {

        private val MAX_RADIUS = 10
        private val FREQUENCY = 50
        private val DEPENDENCE_RECT_COUNT = 32
    }
}
