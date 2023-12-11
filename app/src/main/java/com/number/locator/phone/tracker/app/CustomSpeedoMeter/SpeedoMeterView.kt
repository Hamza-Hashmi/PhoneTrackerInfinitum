package com.phone.tracker.locate.number.app.CustomSpeedoMeter


import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat

import com.phone.tracker.locate.number.app.R

import com.phone.tracker.locate.number.app.utills.TrigonometryFormulas
import kotlin.math.max
import kotlin.math.min

class SpeedoMeterView  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val END_OF_BIG_MARK = 0.95f
        private const val MaximumValue = 180f
        private const val MarkCount = 10

        private fun getAngle(progress: Float): Float =
            ANGLE_OF_ARC * progress + ANGLE_STARTING_POINT

        private const val AnimDuration = 1000L

        private const val Number_Start = 0.77f
        private const val ANGLE_OF_ARC = 360 * 0.6f
        private const val STROKE_FACTOR_SMALL_MARK = 0.005f
        private const val ANGLE_STARTING_POINT = 270 - ANGLE_OF_ARC / 2
        private const val Length_Of_Needle = 0.91f
        private const val Cap_Of_Needle = 0.03f
        private const val Start_Of_BIG_MARK = 0.88f

        private const val START_OF_SMALL_MARK = 0.92f
        private const val END_OF_SMALL_MARK = 0.95f



        private const val STROKE_FACTOR_BIG_MARK = 0.01f

        private const val FACTOR_NUMBER_SIZE = 0.09f

        private const val ARC_STROKE_WIDTH = 0.04f
        private const val WIDTH_OF_NEEDLE = 0.025f

    }
    private val rectArcc = RectF()
    private var radius = 0f
    private val paintArcObj = Paint()

    private var XacsisCentre = 0f
    private val paintLargeMarkObj = Paint()

    private var YaxisCentre = 0f
    private val paintNeedleObj = Paint()
    private val paintSmallObj = Paint()
    private val paintNumberObj = Paint()
    private var anim: ValueAnimator = ValueAnimator.ofFloat(0F, 0F)

    var value = 0f
        set(value) {
            val previousValue = field
            val newValue = max(0f, min(MaximumValue, value))

            anim.cancel()
            anim = ValueAnimator.ofFloat(previousValue, newValue)
            anim.duration = AnimDuration
            anim.addUpdateListener { valueAnimator ->
                field = valueAnimator!!.animatedValue as Float
                invalidate()
            }
            anim.start()
            invalidate()
        }

    var maxValue = MaximumValue
        set(maxValue) {
            field = maxValue
            reset()
        }

    var markCount = MarkCount
        set(markCount) {
            field = markCount
            reset()
        }

    private fun reset() {
        anim.cancel()
        value = 0f
        invalidate()
    }

    init {
        paintArcObj.color = getColor(R.color.black)
        paintArcObj.isAntiAlias = true
        paintArcObj.style = Paint.Style.STROKE

        paintNumberObj.color = getColor(R.color.white)
        paintNumberObj.isAntiAlias = true
        paintNumberObj.textAlign = Paint.Align.CENTER

        paintNeedleObj.color = getColor(R.color.black)
        paintNeedleObj.isAntiAlias = true
        paintNeedleObj.style = Paint.Style.STROKE
        paintNeedleObj.strokeCap = Paint.Cap.ROUND

        paintLargeMarkObj.color = getColor(R.color.black)
        paintLargeMarkObj.isAntiAlias = true
        paintLargeMarkObj.style = Paint.Style.STROKE

        paintSmallObj.color = getColor(R.color.white)
        paintSmallObj.isAntiAlias = true
        paintSmallObj.style = Paint.Style.STROKE
    }

    @ColorInt
    private fun getColor(colorRes: Int): Int =
        ContextCompat.getColor(context, colorRes)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val totalWidth = MeasureSpec.getSize(widthMeasureSpec)
        val totalHeight = MeasureSpec.getSize(heightMeasureSpec)

        val inset = paintArcObj.strokeWidth

        val availableWidth = totalWidth - 2 * inset
        val availableHeight = totalHeight - 2 * inset

        val openAngle = 360 - ANGLE_OF_ARC

        //by me
        val aspectRatio = (TrigonometryFormulas.cos((openAngle / 2)) + 1) / 2

        val width = if (availableWidth * aspectRatio > availableHeight) {
            (availableHeight / aspectRatio)
        } else {
            availableWidth
        }

        radius = width / 2f
        XacsisCentre = totalWidth / 2f
        YaxisCentre = inset + radius

        rectArcc.set(
            XacsisCentre - radius,
            YaxisCentre - radius,
            XacsisCentre + radius,
            YaxisCentre + radius
        )
        updatePaintSizes()

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    private fun updatePaintSizes() {
        paintArcObj.strokeWidth = radius * ARC_STROKE_WIDTH
        paintNumberObj.textSize = radius * FACTOR_NUMBER_SIZE
        paintNeedleObj.strokeWidth = radius * WIDTH_OF_NEEDLE
        paintLargeMarkObj.strokeWidth = radius * STROKE_FACTOR_BIG_MARK
        paintSmallObj.strokeWidth = radius * STROKE_FACTOR_SMALL_MARK

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw arc
        canvas.drawArc(rectArcc, ANGLE_STARTING_POINT, ANGLE_OF_ARC, false, paintArcObj)

        // Draw marks
        for (i in 0..(markCount - 1) * 2) {
            val progress = i.toFloat() / ((markCount - 1) * 2)
            if (i % 2 == 0) {
                drawLine(canvas, paintLargeMarkObj, progress, Start_Of_BIG_MARK, END_OF_BIG_MARK)
            } else {
                drawLine(canvas, paintSmallObj, progress, START_OF_SMALL_MARK, END_OF_SMALL_MARK)
            }
        }

        // Draw numbers
        for (i in 0 until markCount) {
            val progress = i.toFloat() / (markCount - 1)
            val value = (progress * maxValue).toInt()
            val angle = getAngle(progress)
            // by me
            val factorX = TrigonometryFormulas.cos(angle)
            val factorY = TrigonometryFormulas.sin(angle)
            canvas.drawText(
                value.toString(),
                XacsisCentre + factorX * radius * Number_Start,
                YaxisCentre + factorY * radius * Number_Start,
                paintNumberObj
            )
        }

        // Draw needle
        val progress = value / MaximumValue
        paintNeedleObj.style = Paint.Style.STROKE
        drawLine(canvas, paintNeedleObj, progress, 0f, Length_Of_Needle)
        paintNeedleObj.style = Paint.Style.FILL
        canvas.drawCircle(XacsisCentre, YaxisCentre, Cap_Of_Needle * radius, paintNeedleObj)
    }

    private fun drawLine(canvas: Canvas, paint: Paint, progress: Float, startFactor: Float, endFactor: Float) {
        val angle = getAngle(progress)

        //by me
        val factorX = TrigonometryFormulas.cos(angle)
        val factorY = TrigonometryFormulas.sin(angle)


        val start = startFactor * radius
        val end = endFactor * radius
        canvas.drawLine(
            XacsisCentre + factorX * start,
            YaxisCentre + factorY * start,
            XacsisCentre + factorX * end,
            YaxisCentre + factorY * end,
            paint
        )
    }



}
