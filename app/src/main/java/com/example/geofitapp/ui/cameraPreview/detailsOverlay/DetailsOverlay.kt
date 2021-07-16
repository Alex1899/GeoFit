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


class DetailsOverlay constructor(context: Context?, attributeSet: AttributeSet?) :
    View(context, attributeSet) {
    private var rep: String? = null
    private var side: String? = null
    private var pace: String? = null
    val path = Path()
    val optionsPath = Path()
//    private val rect = RectF(0f, 0f, width.toFloat(), 300f);

    private val paint: Paint = Paint()
    private val textPaint: Paint = Paint()
    private val textPaintSmall: Paint = Paint()
    private val iconPaint: Paint = Paint()
    private val repsText = "REPS"
    private val sideText = "SIDE"
    private val averagePaceText = "PACE"


    private val corners = floatArrayOf(
        80f, 80f,
        80f, 80f,
        0f, 0f,
        0f, 0f,
    )

    private val optionsCorners = floatArrayOf(
        80f, 80f,
        80f, 80f,
        80f, 80f,
        80f, 80f,
    )


    init {
        paint.color = ContextCompat.getColor(getContext(), R.color.transparentColor)
        paint.style = Paint.Style.FILL;
//        paint.maskFilter = BlurMaskFilter(16f, BlurMaskFilter.Blur.NORMAL)
        textPaint.color = Color.WHITE
        textPaintSmall.color = Color.WHITE
        textPaint.textSize = 90f
        textPaintSmall.textSize = 35f
        textPaint.textAlign = Paint.Align.CENTER;
        textPaintSmall.textAlign = Paint.Align.CENTER;
        iconPaint.color = Color.WHITE




    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Pass it a list of RectF (rectBounds)

        if (rep == null) {
            return
        }

        val rect =
            RectF(0f, height.toFloat() - 200f, width.toFloat(), height.toFloat())

        val rectHalfY = (height.toFloat() - rect.centerY()) / 2
        val rectHalfX = (width.toFloat() - rect.centerX()) / 2
        Log.i("Overlay", "rect half =$rectHalfX")
        Log.i("Overlay", "rect center =${rect.centerX()}")


        val y = rect.centerY() - rectHalfY
        path.addRoundRect(rect, corners, Path.Direction.CW)

        canvas.drawPath(path, paint)

        // draw camera options on top right
        val optionsRect = RectF(width.toFloat() - 150f, 30f, width.toFloat() - 30f, height.toFloat() - height.toFloat() * 0.8f)
        optionsPath.addRoundRect(optionsRect, optionsCorners, Path.Direction.CW)
        canvas.drawPath(optionsPath, paint)

        val optionsDivCenterX = optionsRect.centerX()
        val optionsDivCentery = optionsRect.centerY()


        val cameraIcon = AppCompatResources.getDrawable(context, R.drawable.ic_rotate_camera)!!.toBitmap()

        canvas.drawBitmap(cameraIcon, optionsDivCenterX - (cameraIcon.width /2), optionsDivCentery * 0.25f, iconPaint )
        // draw bottom part
        canvas.drawText(repsText, rectHalfX * 0.5f, y, textPaintSmall)
        canvas.drawText(rep!!, rectHalfX * 0.5f, y + rectHalfY * 2f, textPaint)

        canvas.drawText(sideText, rectHalfX * 1.5f, y, textPaintSmall)
        canvas.drawText(side!!, rectHalfX * 1.5f,y + rectHalfY * 2f, textPaint)

        canvas.drawText(averagePaceText,  rectHalfX * 2.5f, y, textPaintSmall)
        canvas.drawText("${pace!!}s", rectHalfX * 2.5f, y + rectHalfY * 2f, textPaint)

        canvas.drawText("ERRORS",  rectHalfX * 3.5f, y, textPaintSmall)
        canvas.drawText("0", rectHalfX * 3.5f, y + rectHalfY * 2f, textPaint)

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