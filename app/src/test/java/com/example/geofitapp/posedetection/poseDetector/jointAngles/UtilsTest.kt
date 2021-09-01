package com.example.geofitapp.posedetection.poseDetector.jointAngles

import android.graphics.Point
import com.bumptech.glide.util.Util
import com.google.mlkit.vision.common.PointF3D
import junit.framework.TestCase
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`

class UtilsTest : TestCase() {

    fun testSubtract() {
        val point3d = Utils.subtract(
            PointF3D.from(326.70038f, 451.2146f, -101.66235f),
            PointF3D.from(359.73572f, 179.80215f, 0.48715097f)
        )
        assertThat(point3d, `is`(PointF3D.from(33.03534f, -271.41245f, 102.149506f)))

    }

    fun testMultiply() {
        val point3d =
            Utils.multiply(PointF3D.from(-0.019971544f, -0.39950112f, -0.4089003f), 100f)
        assertThat(point3d, `is`(PointF3D.from(-1.9971544f, -39.95011f, -40.89003f)))

    }

    fun testAverage() {
        val p = Utils.average(
            PointF3D.from(326.70038f, 451.2146f, -101.66235f),
            PointF3D.from(359.73572f, 179.80215f, 0.48715097f)
        )
        assertThat(p, `is` ( PointF3D.from(343.21805f, 315.50836f, -50.5876f)))
    }

    fun testSubtractAll() {
        val pList = mutableListOf(
            PointF3D.from(43.086456f, 222.60898f, 108.77771f),
            PointF3D.from(37.014984f, -234.16087f, 111.55297f)
        )

        Utils.subtractAll(
            PointF3D.from(
                346.2964f,
                444.98108f,
                -112.64911f
            ), pList
        )

        assertThat(
            pList, `is`(
                mutableListOf(
                    PointF3D.from(-303.20993f, -222.3721f, 221.42682f),
                    PointF3D.from(-309.2814f, -679.14197f, 224.20209f)
                )
            )
        )
    }

    fun testMultiplyAll() {
        val pList = mutableListOf(
            PointF3D.from(43.086456f, 222.60898f, 108.77771f),
            PointF3D.from(37.014984f, -234.16087f, 111.55297f)
        )

        Utils.multiplyAll(
            pList, 100f
        )

        assertThat(
            pList, `is`(
                mutableListOf(
                    PointF3D.from(4308.6455f, 22260.898f, 10877.771f),
                    PointF3D.from(3701.4985f, -23416.088f, 11155.297f)
                )
            )
        )

    }

    fun testMedfilt() {
        val list = Utils.medfilt(mutableListOf(1.0,3.0,4.0,5.0,7.0,2.0,6.0,9.0,4.0,3.0,6.0,8.0), 3)
        assertThat(list, `is`(mutableListOf(1.0, 3.0, 4.0, 5.0, 5.0, 6.0, 6.0, 6.0, 4.0, 4.0, 6.0, 6.0)))
    }
}