package com.myunidays.udb.adb.model

data class UiNode(
    override val text: String,
    override val resId: String,
    override val clickable: Boolean,
    override val clazz: String,
    override val pkg: String,
    override val contentDescription: String,
    val boundsString: String,
) : IUiNode {
    override val bounds: IUiNodeBounds by lazy {
        val boundsInts = boundsString.split("\\D".toRegex())
            .mapNotNull { it.toIntOrNull() }
        require(boundsInts.size == 4)
        UiNodeBounds(
            lowX = boundsInts[0],
            lowY = boundsInts[1],
            highX = boundsInts[2],
            highY = boundsInts[3],
        )
    }
}
