package com.thelumierguy.starfield.utils

const val ALPHA = 0.05F

/**
 * Low Pass filter which smoothes out the data
 * We will only be using the
 */
fun lowPass(
    input: FloatArray,
    output:FloatArray
) {
    output[0] = ALPHA * input[1] + output[0] * 1.0f - ALPHA
}