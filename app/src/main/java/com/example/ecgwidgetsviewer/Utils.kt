package com.example.ecgwidgetsviewer

import java.util.Collections
import java.util.Random

class Utils {
    val TAG: String
        get() = Utils::class.java.simpleName

    fun extractRangeData(rowData: List<Int?>, start: Int, number: Int): List<Int?> {
        var result: List<Int?> = ArrayList()
        if (rowData.isEmpty()) {
            return result
        }
        if (start < 0) {
            return result
        }
        if (number <= 0) {
            return result
        }
        val rowLength = rowData.size
        if (start >= rowLength) {
            return result
        }
        if (rowData.size <= start + number) {
            result = rowData.subList(start, rowLength)
            return result
        }
        result = rowData.subList(start, start + number)
        return result
    }

    fun getMinForFullBuffer(buffer: CircularBuffer<Int?>): Int {
        var result = 0
        val rowData = buffer.buffer()
        result = rowData[1]!!
        return result
    }

    fun dataSeriesOverlay(buffer: CircularBuffer<Int?>?): List<Int> {
        if (buffer == null || buffer.size() === 0) {
            return ArrayList()
        }
        val seriesSize =
            if (buffer.size() < buffer.capacity() - 1) buffer.size() else buffer.capacity() - 1
        val result: MutableList<Int> = ArrayList(Collections.nCopies(seriesSize, 0))
        for (i in 0 until seriesSize) {
            if (i < buffer.size()) {
                val value = buffer.getDirect(i)!!
                result[i] = value
            }
        }
        return result
    }

    fun dataSeriesNormal(storeWrapper: StoreWrapper): List<Int?> {
        storeWrapper.storeCircularBufferParams()
        val result: List<Int?> = storeWrapper.buffer().getData()
        storeWrapper.restoreCircularBufferParams()
        return result
    }

    fun randomInRange(min: Int, max: Int): Int {
        require(min < max) { "max must be greater than min" }
        val random = Random()
        return random.nextInt(max - min + 1) + min
    }
}

