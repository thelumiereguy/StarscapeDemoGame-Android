package com.thelumierguy.starfield.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.hardware.SensorEvent
import android.util.AttributeSet
import android.view.View
import com.thelumierguy.starfield.utils.lowPass
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SpaceShipView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attributeSet, defStyle) {

    private var rotationValue = 0F

    private val bodyPaint = Paint().apply {
        color = Color.parseColor("#3E4D6C")
    }

    private val wingsPaint = Paint().apply {
        color = Color.parseColor("#A7C5CD")
    }

    private val wingsPaintOutline = Paint().apply {
        color = Color.parseColor("#3E4D6C")
        style = Paint.Style.STROKE
        strokeWidth = 8F
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }


    private val jetPaint = Paint().apply {
        color = Color.parseColor("#E9924B")
        setShadowLayer(
            12F,
            0F, 0F,
            Color.parseColor("#FCF05A")
        )
    }

    private val multiplicationFactor = 2F

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    private val jetOuterRadius = 36F
    private var jetInnerRadius = 20F

    private val halfWidth by lazy { width / 2F }
    private val halfHeight by lazy { height / 2F }

    private var gravityValue = FloatArray(1)


    fun processSensorEvents(sensorEvent: SensorEvent) {
        lowPass(sensorEvent.values, gravityValue)
        magnifyValue()
        invertGravityValue()
        invalidate()
    }

    /**
     * Invert the gravityValue, so that the ship will pop out of the other side
     * Ideally it should be 20F, but partially drawing ship on both sides, causes it to blink
     */
    private fun invertGravityValue() {
        when {
            rotationValue < -90 -> {
                gravityValue[0] += 90F
            }
            rotationValue > 90 -> {
                gravityValue[0] -= 90F
            }
            else -> {
            }
        }
    }

    private fun magnifyValue() {
        rotationValue = multiplicationFactor * gravityValue[0]
    }

    fun boost() {
        MainScope().launch {
            jetPaint.color = Color.parseColor("#F2463B")
            jetInnerRadius = 30F
            delay(4000)
            jetPaint.color = Color.parseColor("#E9924B")
            jetInnerRadius = 20F
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isInEditMode) {
            return
        }
        canvas?.let {
            canvas.rotate(
                rotationValue,
                halfWidth,
                height.toFloat()
            )
            drawShipOutline(it)
            drawShipAntenna(it)
            drawShipExhaust(it)
        }
    }

    private fun drawShipExhaust(it: Canvas) {
        it.drawCircle(halfWidth, halfHeight, jetOuterRadius, bodyPaint)
        it.drawCircle(halfWidth, halfHeight, jetInnerRadius, jetPaint)
    }

    private fun drawShipAntenna(it: Canvas) {
        it.drawLine(halfWidth, halfHeight, halfWidth, height / 2.6F, wingsPaintOutline)
    }


    private fun drawShipOutline(canvas: Canvas) {
        val path = Path()

        path.moveTo(halfWidth, halfHeight - jetOuterRadius) // Top

        path.lineTo(halfWidth - (jetOuterRadius * 4), halfHeight + jetOuterRadius) // Left

        path.lineTo(halfWidth + (jetOuterRadius * 4), halfHeight + jetOuterRadius) // Right

        path.close()

        canvas.drawPath(path, wingsPaint)
        canvas.drawPath(path, wingsPaintOutline)

    }
}