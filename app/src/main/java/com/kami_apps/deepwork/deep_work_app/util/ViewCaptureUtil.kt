package com.kami_apps.deepwork.deep_work_app.util

// ViewCaptureUtil.kt

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

fun View.captureToBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
}
