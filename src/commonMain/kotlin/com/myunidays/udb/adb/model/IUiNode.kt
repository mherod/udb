package com.myunidays.udb.adb.model

interface IUiNode {
    val text: String
    val resId: String
    val clickable: Boolean
    val clazz: String
    val pkg: String
    val contentDescription: String
    val bounds: IUiNodeBounds
}
