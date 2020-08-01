package com.thelumierguy.starfield.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.hardware.SensorEvent
import android.util.AttributeSet
import android.view.View
import com.thelumierguy.starfield.utils.ScreenStates
import com.thelumierguy.starfield.utils.lowPass
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class StarFieldView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attributeSet, defStyle) {

    private var enableTrails: Boolean = false

    private var translationValue = 0F

    private val starsArray by lazy {
        Array(800, init = {
            Star(measuredWidth, measuredHeight)
        })
    }

    private val multiplicationFactor = 10F

    var gravityValue = FloatArray(1)

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
                measuredWidth / 2F - translationValue,
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
        MainScope().launch {
            enableTrails = enableTrails != true
            if (enableTrails)
                translationValue = 0F
            delay(4000)
            enableTrails = false //disable trails after 4 seconds
        }
    }


    fun processSensorEvents(sensorEvent: SensorEvent) {
        translationValue = if (enableTrails) {
            0F
        } else {
            lowPass(sensorEvent.values, gravityValue)
            gravityValue[0] * multiplicationFactor
        }
    }

    fun processScreenState(screenStates: ScreenStates) {
        when (screenStates) {
            ScreenStates.APP_INIT,
            ScreenStates.GAME_MENU -> {
                if (isAttachedToWindow) {
                    enableTrails = false
                    starsArray.forEach {
                        it.speed = it.menuSpeed
                    }
                }
            }
            ScreenStates.START_GAME -> {
                starsArray.forEach {
                    it.speed = it.defaultSpeed
                }
            }
        }
    }
}


