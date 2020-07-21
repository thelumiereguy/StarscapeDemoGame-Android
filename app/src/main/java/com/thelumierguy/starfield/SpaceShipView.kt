package com.thelumierguy.starfield

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.roundToInt


class SpaceShipView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attributeSet, defStyle) {

    private var canvasCenterX: Float = 0F
    private var roundedSensorValue = 0F
    val bodyPaint = Paint().apply {
        color = Color.parseColor("#3E4D6C")
    }

    val wingsPaint = Paint().apply {
        color = Color.parseColor("#A7C5CD")
    }

    val wingsPaintOutline = Paint().apply {
        color = Color.parseColor("#3E4D6C")
        style = Paint.Style.STROKE
        strokeWidth = 8F
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }


    val jetPaint = Paint().apply {
        color = Color.parseColor("#E9924B")
        setShadowLayer(
            12F,
            0F, 0F,
            Color.parseColor("#FCF05A")
        )
    }

    fun boost() {
        jetPaint.color = Color.parseColor("#F2463B")
        jetInnerRadius = 30F
        Handler().postDelayed({
            jetPaint.color = Color.parseColor("#E9924B")
            jetInnerRadius = 20F
        }, 4000)
    }

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    val jetOuterRadius = 36F
    var jetInnerRadius = 20F

    val halfWidth by lazy { width / 2F }
    val halfHeight by lazy { height / 2F }
    var currentSpeed = 0F

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isInEditMode) {
            return
        }
        canvas?.let {
            canvas.rotate(
                currentSpeed,
                halfWidth,
                height.toFloat()
            )
            drawRhombus(it)
            it.drawLine(halfWidth, halfHeight, halfWidth, height / 2.6F, wingsPaintOutline)
            it.drawCircle(halfWidth, halfHeight, jetOuterRadius, bodyPaint)
            it.drawCircle(halfWidth, halfHeight, jetInnerRadius, jetPaint)
        }
    }

    private fun drawRhombus(canvas: Canvas) {
        val path = Path()

        path.moveTo(halfWidth, halfHeight - jetOuterRadius) // Top

        path.lineTo(halfWidth - (jetOuterRadius * 4), halfHeight + jetOuterRadius) // Left

        path.lineTo(halfWidth + (jetOuterRadius * 4), halfHeight + jetOuterRadius) // Right

        path.close()

        canvas.drawPath(path, wingsPaint)
        canvas.drawPath(path, wingsPaintOutline)

    }

    private val sensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private val gyroscopeSensor: Sensor by lazy {
        sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    var gravSensorVals = FloatArray(3)

    private val gyroscopeSensorListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {
//            canvasCenterX = lowPass(sensorEvent.values)
            roundedSensorValue = sensorEvent.values[1].toInt() % 10F
            canvasCenterX = map(roundedSensorValue, -10F, 10F, -90F, 90F).toInt().toFloat()
            when {
                currentSpeed < canvasCenterX -> {
                    currentSpeed+=0.5F
                }
                currentSpeed > canvasCenterX -> {
                    currentSpeed-=0.5F
                }
                else -> {

                }
            }
            invalidate()
            Log.d(
                "SensorEventListener",
                "$canvasCenterX $roundedSensorValue $currentSpeed ${sensorEvent.values[1]}"
            )
        }

        override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
    }

    private fun map(
        value: Float,
        startRangeMin: Float,
        startRangeMax: Float,
        endRangeMin: Float,
        endRangeMax: Float
    ): Float {
        return (value - startRangeMin) / (startRangeMax - startRangeMin) * (endRangeMax - endRangeMin) + endRangeMin;
    }

//    val ALPHA = 0.10F
//
//    private fun lowPass(
//        input: FloatArray
//    ): Float {
//        gravSensorVals[1] = ALPHA * input[1] + gravSensorVals[1] * 1.0F - ALPHA
//        return gravSensorVals[1]
//    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        sensorManager.registerListener(
            gyroscopeSensorListener,
            gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        sensorManager.unregisterListener(gyroscopeSensorListener)
    }
}