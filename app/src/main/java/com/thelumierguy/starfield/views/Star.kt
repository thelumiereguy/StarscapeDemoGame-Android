package com.thelumierguy.starfield.views

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.random.Random

class Star(private val width: Int, private val height: Int) {

    var x = Random.nextInt(-width, width).toFloat()
    var y = Random.nextInt(-height, height).toFloat()
    var z = Random.nextInt(width).toFloat()

    var pz = z

    var speed = 10F

    var minSpeed = 10F
    var maxSpeed = 60F

    private val trailsColor by lazy {
        Color.rgb(
            Random.nextInt(0, 255),
            Random.nextInt(0, 255),
            Random.nextInt(0, 255)
        )
    }

    private val starPaint by lazy {
        Paint().apply {
            color = Color.WHITE
        }
    }

    private val starTrailsPaint by lazy {
        Paint().apply {
            color = trailsColor
            style = Paint.Style.STROKE
            strokeWidth = 6F
            strokeCap = Paint.Cap.ROUND
        }
    }

    fun updateZPosition(invalidate: () -> Unit) {
        z -= speed //translation speed
        invalidate()
        if (z < 0) {
            resetPosition()
        }
    }

    private fun resetPosition() {
        x = Random.nextInt(-width, width).toFloat()
        y = Random.nextInt(-height, height).toFloat()
        z = width.toFloat()
        pz = z
    }


    fun drawStar(canvas: Canvas) {
        if (speed > minSpeed)
            speed -= 0.5F
        //Map coordinates to location on canvas
        val sx = map(
            x / z,
            0F,
            1F,
            0F,
            width.toFloat()
        )
        val sy = map(
            y / z,
            0F,
            1F,
            0F,
            height.toFloat()
        )
        //Map radius according to distance from z to width
        val radius = map(z, 0F, width.toFloat(), 8F, 0F)
        canvas.drawCircle(sx, sy, radius, starPaint)
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

    fun drawStarWithTrails(canvas: Canvas) {
        if (speed < maxSpeed)
            speed += 10F
        //Map coordinates to location on canvas
        val sx = map(
            x / z,
            0F,
            1F,
            0F,
            width.toFloat()
        )
        val sy = map(
            y / z,
            0F,
            1F,
            0F,
            height.toFloat()
        )
        val px = map(x / pz, 0F, 1F, 0F, width.toFloat())
        val py = map(y / pz, 0F, 1F, 0F, height.toFloat())
        canvas.drawLine(px, py, sx, sy, starTrailsPaint)
    }
}