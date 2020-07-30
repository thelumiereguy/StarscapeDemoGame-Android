package com.thelumierguy.starfield

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.graphics.Canvas
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import com.thelumierguy.starfield.utils.lowPass
import com.thelumierguy.starfield.views.Star


class StarFieldView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attributeSet, defStyle) {

    private var enableTrails: Boolean = false

    var translationValue = 0F

    private val starsArray by lazy {
        Array(800, init = {
            Star(measuredWidth, measuredHeight)
        })
    }

    private val sensorManager by lazy {
        context.getSystemService(SENSOR_SERVICE) as SensorManager
    }

    private val gyroscopeSensor: Sensor by lazy {
        sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    var gravityValue = FloatArray(1)

    private val gyroscopeSensorListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {
            translationValue = if (enableTrails) {
                0F
            } else {
                lowPass(sensorEvent.values, gravityValue)
                gravityValue[0] * 40
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
    }

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        setBackgroundColor(Color.parseColor("#001122"))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isInEditMode) {
            return
        }
        canvas?.let {
            canvas.translate(
                measuredWidth / 2F + translationValue,
                measuredHeight / 2F
            )
            starsArray.forEach { star ->
                star.updateZPosition {
                    invalidate()
                }
                if (enableTrails) {
                    star.drawStarWithTrails(canvas)
                } else {
                    star.drawStar(canvas)
                }

            }
        }
    }

    fun setTrails() {
        enableTrails = enableTrails != true
        if (enableTrails)
            translationValue = 0F
        Handler().postDelayed({
            enableTrails = false //disable trails after 4 seconds
        }, 4000)
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        sensorManager.registerListener(
            gyroscopeSensorListener,
            gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        sensorManager.unregisterListener(gyroscopeSensorListener)
    }
}


