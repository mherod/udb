package com.myunidays.udb.adb.model

data class UiNodeBounds(
    val lowX: Int,
    val lowY: Int,
    val highX: Int,
    val highY: Int,
) : IUiNodeBounds {
    override val centreX: Int get() = (lowX + highX) / 2
    override val centreY: Int get() = (lowY + highY) / 2
}
