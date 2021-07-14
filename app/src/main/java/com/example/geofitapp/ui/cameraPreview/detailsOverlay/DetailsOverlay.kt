package com.example.geofitapp.ui.cameraPreview.detailsOverlay

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.geofitapp.R

class DetailsOverlay constructor(context: Context?, attributeSet: AttributeSet?) :
    View(context, attributeSet) {
    private var rep: String? = null
    private var side: String? = null
    private var pace: String? = null
    val path = Path()
//    private val rect = RectF(0f, 0f, width.toFloat(), 300f);

    private val paint: Paint = Paint()
    private val textPaint: Paint = Paint()
    private val textPaintSmall: Paint = Paint()
    private val repsText = "REPS"
    private val sideText = "SIDE"
    private val averagePaceText = "PACE"

    private val corners = floatArrayOf(
        80f, 80f,
        80f, 80f,
        0f, 0f,
        0f, 0f,
    )


    init {
        paint.color = ContextCompat.getColor(getContext(), R.color.transparentColor)
        paint.style = Paint.Style.FILL;
//        paint.maskFilter = BlurMaskFilter(16f, BlurMaskFilter.Blur.NORMAL)
        textPaint.color = Color.WHITE
        textPaintSmall.color = Color.WHITE
        textPaint.textSize = 130f
        textPaintSmall.textSize = 40f

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Pass it a list of RectF (rectBounds)

        if (rep == null) {
            return
        }

        val rect =
            RectF(0f, height.toFloat() - 300f, width.toFloat(), height.toFloat())


        val startX = 80f
        val rectHalfY = (height.toFloat() - rect.centerY()) / 2
        val rectHalfX = (width.toFloat() - rect.centerX()) / 2
        val y = rect.centerY() - rectHalfY
        path.addRoundRect(rect, corners, Path.Direction.CW)

        canvas.drawPath(path, paint)

        canvas.drawText(repsText, startX, y, textPaintSmall)
        canvas.drawText(rep!!, startX, y + rectHalfY * 2f, textPaint)

        canvas.drawText(sideText, rect.centerX() - rectHalfX *0.5f, y, textPaintSmall)
        canvas.drawText(side!!, rect.centerX() -rectHalfX *0.5f,y + rectHalfY * 2f, textPaint)

        canvas.drawText(averagePaceText,  rect.centerX() + rectHalfX, y, textPaintSmall)
        canvas.drawText(pace!!, rect.centerX() + rectHalfX, y + rectHalfY * 2f, textPaint)


    }

    fun addDetails(rep: String, pace: String, side: String? = null) {
        this.rep = rep
        this.side = side
        this.pace = pace
        invalidate()
    }

    fun resetDetails() {
        this.rep = null
        this.side = null
        this.pace = null
        invalidate()
    }
}