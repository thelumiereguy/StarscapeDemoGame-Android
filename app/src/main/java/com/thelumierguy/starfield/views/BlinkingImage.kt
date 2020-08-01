package com.thelumierguy.starfield.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatImageView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BlinkingImage @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attributeSet, defStyle) {

    private val blinkAnimation by lazy {
        AlphaAnimation(0.0f, 1.0f).apply {
            duration = 800
            startOffset = 20
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    fun startBlinking() {
        MainScope().launch {
            delay(500)
            visibility = View.VISIBLE
            startAnimation(blinkAnimation)
        }
    }

    private fun stopBlinking() {
        blinkAnimation.cancel()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopBlinking()
    }
}
