package com.example.geofitapp.posedetection.poseDetector.jointAngles

import com.google.common.primitives.Floats
import com.google.mlkit.vision.common.PointF3D
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sqrt


/**
 * Utility methods for operations on [PointF3D].
 */
object Utils {
    fun add(a: PointF3D, b: PointF3D): PointF3D {
        return PointF3D.from(a.x + b.x, a.y + b.y, a.z + b.z)
    }

    fun subtract(b: PointF3D, a: PointF3D): PointF3D {
        return PointF3D.from(a.x - b.x, a.y - b.y, a.z - b.z)
    }

    fun multiply(a: PointF3D, multiple: Float): PointF3D {
        return PointF3D.from(a.x * multiple, a.y * multiple, a.z * multiple)
    }

    fun multiply(a: PointF3D, multiple: PointF3D): PointF3D {
        return PointF3D.from(
            a.x * multiple.x, a.y * multiple.y, a.z * multiple.z
        )
    }

    fun average(a: PointF3D, b: PointF3D): PointF3D {
        return PointF3D.from(
            (a.x + b.x) * 0.5f, (a.y + b.y) * 0.5f, (a.z + b.z) * 0.5f
        )
    }

    fun l2Norm2D(point: PointF3D): Float {
        return hypot(point.x.toDouble(), point.y.toDouble()).toFloat()
    }

    fun normVector(vector: List<Double>): List<Double> {
        val length = sqrt(vector[0].pow(2) + vector[1].pow(2) + vector[2].pow(2))
        return listOf(vector[0] / length, vector[1] / length, vector[2] / length)
    }

    fun maxAbs(point: PointF3D): Float {
        return Floats.max(Math.abs(point.x), Math.abs(point.y), Math.abs(point.z))
    }

    fun sumAbs(point: PointF3D): Float {
        return Math.abs(point.x) + Math.abs(point.y) + Math.abs(point.z)
    }

    fun addAll(pointsList: MutableList<PointF3D>, p: PointF3D) {
        val iterator = pointsList.listIterator()
        while (iterator.hasNext()) {
            iterator.set(add(iterator.next(), p))
        }
    }

    fun subtractAll(p: PointF3D, pointsList: MutableList<PointF3D>) {
        val iterator = pointsList.listIterator()
        while (iterator.hasNext()) {
            iterator.set(subtract(p, iterator.next()))
        }
    }

    fun multiplyAll(pointsList: MutableList<PointF3D>, multiple: Float) {
        val iterator = pointsList.listIterator()
        while (iterator.hasNext()) {
            iterator.set(multiply(iterator.next(), multiple))
        }
    }

    fun multiplyAll(pointsList: MutableList<PointF3D>, multiple: PointF3D) {
        val iterator = pointsList.listIterator()
        while (iterator.hasNext()) {
            iterator.set(multiply(iterator.next(), multiple))
        }

    }

    fun medfilt(dataList: MutableList<Double>, k: Int): MutableList<Double> {
        val list = mutableListOf<Double>()
        val halfK = k / 2 // 2
        lateinit var window: MutableList<Double>
        var start = -1
        var end = -1

        //[2,3,80,6,3,7,8,9]
        for (i in dataList.indices) { // i = 0
            // how many 0 to pad on the left is halfK - i
            if (i < halfK) { //halfK = 2
                window = DoubleArray(halfK - i) { 0.0 }.toMutableList() // [0,0]
                start = 0
                end = i + halfK    // 1
            } else {
                start = i - halfK     // 2
                end = i + halfK       // 4
            }

            if (end >= dataList.size) {
                val numsLeft = dataList.size - i - 1
                val last = dataList.size - 1
                window.addAll(dataList.slice(start..last))
                window.addAll(DoubleArray(halfK - numsLeft) { 0.0 }.toList())
            } else {
                window.addAll(dataList.slice(start..end)) // [2,3,80]
            }
            window.sort()
            val median = window[window.size / 2]
            list.add(median)
            window.clear()

        }
        return list
    }


}
