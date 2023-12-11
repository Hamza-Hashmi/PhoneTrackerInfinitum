package com.phone.tracker.locate.number.app.icons

import android.content.Context
import android.content.res.Resources
import androidx.annotation.DrawableRes
import kotlin.math.max
import kotlin.math.min

class ProvideIcons(context: Context) {
    private val MAX_VALUE = 200
    private  val RES_FORMAT = "icon%04d"
    private val RES_TYPE = "drawable"
    private val resource : Resources = context.resources
    private val packName : String = context.packageName

    @DrawableRes
    fun getIconForNumber(number: Int): Int =
        String.format(RES_FORMAT, max(0, min(MAX_VALUE, number)))
            .let { iconName ->
                resource.getIdentifier(iconName, RES_TYPE, packName)
            }



}
