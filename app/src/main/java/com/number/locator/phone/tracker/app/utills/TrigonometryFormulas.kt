package com.phone.tracker.locate.number.app.utills

import kotlin.math.cos
import kotlin.math.sin

object TrigonometryFormulas {

    fun sin(degree: Float): Float = sin(toRadians(degree)).toFloat()

    fun cos(degree: Float): Float = cos(toRadians(degree)).toFloat()

    private fun toRadians(degree : Float): Double = degree / 180 * Math.PI

}
