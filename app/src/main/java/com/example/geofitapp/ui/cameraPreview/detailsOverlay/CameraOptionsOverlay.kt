package com.example.geofitapp.ui.cameraPreview.detailsOverlay

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.geofitapp.R


class CameraOptionsOverlay constructor(context: Context?, attributeSet: AttributeSet?) :
    View(context, attributeSet) {
    private val optionsPath = Path()
    private val paint: Paint = Paint()
    private val iconPaint: Paint = Paint()


    private val optionsCorners = floatArrayOf(
        80f, 80f,
        80f, 80f,
        80f, 80f,
        80f, 80f,
    )

    init {
        paint.color = ContextCompat.getColor(getContext(), R.color.transparentColor)
        paint.style = Paint.Style.FILL;
        iconPaint.color = Color.WHITE
    }



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCameraOptionsPart(canvas)
    }

    private fun drawCameraOptionsPart(canvas: Canvas) {
        Log.i("CameraDiv","view width=$width height=$height" )
        // draw camera options on top right
        val optionsRect = RectF(
            0f,
            0f,
            width.toFloat(),
            height.toFloat()
        )
        optionsPath.addRoundRect(optionsRect, optionsCorners, Path.Direction.CW)
        canvas.drawPath(optionsPath, paint)
    }
}
