package com.example.ecgwidgetsviewer

import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class CoreTest {
    @Test
    fun circularBufferBasic() {
        val circularBuffer = CircularBuffer<Int>(10)
        assertNotNull(circularBuffer)
        assertEquals(10, circularBuffer.capacity())
        assertEquals(0, circularBuffer.size())
        assertTrue(circularBuffer.isEmpty)
    }

    @Test
    fun circularBufferWrite() {
         val circularBuffer = CircularBuffer<Int>(10)
        assertNotNull(circularBuffer)
        assertEquals(10, circularBuffer.capacity())
        assertEquals(0, circularBuffer.size())
        circularBuffer.writeRow(listOf(1, 4, 2, 5))
        assertEquals(4, circularBuffer.size())
        assertFalse(circularBuffer.isEmpty)
        circularBuffer.writeRow(listOf(3, 9, 8, 7, 9))
        assertFalse(circularBuffer.isFull)
        circularBuffer.write(0)
        assertTrue(circularBuffer.isFull)
    }

    @Test
    fun circularBufferReadWrite() {
        val circularBuffer = CircularBuffer<Int>(10)
        assertNotNull(circularBuffer)
        assertEquals(10, circularBuffer.capacity())
        assertEquals(0, circularBuffer.size())
        circularBuffer.writeRow(listOf(1, 4, 2, 5, 3, 9, 8, 7, 9))
        assertEquals(9, circularBuffer.size())
        assertFalse(circularBuffer.isEmpty)
        assertFalse(circularBuffer.isFull)
        val row = circularBuffer.readRow(5)
        assertEquals(listOf(1, 4, 2, 5, 3), row)
        assertEquals(4, circularBuffer.size())


    }

}