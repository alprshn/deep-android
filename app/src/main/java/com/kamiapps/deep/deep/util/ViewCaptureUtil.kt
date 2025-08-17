package com.kamiapps.deep.deep.util

// ViewCaptureUtil.kt

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View

fun View.captureToBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    // SHADOW FIX: Arka planı temizle (şeffaf yap)
    canvas.drawColor(Color.TRANSPARENT)
    draw(canvas)
    return bitmap
}
