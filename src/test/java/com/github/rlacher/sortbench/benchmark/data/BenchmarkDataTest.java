/*
 * Copyright (c) 2025 Rene Lacher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.rlacher.sortbench.benchmark.data;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

/**
 * Unit tests for the {@link BenchmarkData} class.
 */
public class BenchmarkDataTest
{
    /**
     * Tests the constructor of the {@link BenchmarkData} class when the input data is null.
     */
    @Test
    void constructor_whenDataIsNull_thenThrowsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkData(null, BenchmarkData.DataType.RANDOM));
    }

    /**
     * Tests the constructor of the {@link BenchmarkData} class with valid input data.
     */
    @Test
    void constructor_whenParamsAreValid_thenInitFields()
    {
        int[] data = { 1, 2, 3 };
        BenchmarkData benchmarkData = new BenchmarkData(data, BenchmarkData.DataType.SORTED);

        assertArrayEquals(data, benchmarkData.getDataCopy());
        assertEquals(BenchmarkData.DataType.SORTED, benchmarkData.getType());
    }

    /**
     * Tests the constructor of the {@link BenchmarkData} class to ensure that the data field is a deep copy.
     */
    @Test
    void constructor_whenParamsAreValid_thenDataFieldIsDeepCopy()
    {
        int[] data = { 1, 2, 3 };
        BenchmarkData benchmarkData = new BenchmarkData(data, BenchmarkData.DataType.SORTED);

        data[0] = 42; // Modify original data
        assertNotEquals(data[0], benchmarkData.getDataCopy()[0]);
    }

    /**
     * Tests the constructor of the {@link BenchmarkData} class when the input data is an empty array.
     */
    @Test
    void constructor_whenDataIsEmptyArray_thenInitializesCorrectly()
    {
        int[] data = {};
        BenchmarkData benchmarkData = new BenchmarkData(data, BenchmarkData.DataType.RANDOM);

        assertArrayEquals(data, benchmarkData.getDataCopy());
        assertEquals(0, benchmarkData.getLength());
        assertEquals(BenchmarkData.DataType.RANDOM, benchmarkData.getType());
    }

    /**
     * Tests the {@link com.github.rlacher.sortbench.benchmark.data.BenchmarkData#getDataCopy()} method to ensure it returns a deep copy of the data.
     */
    @Test
    void getDataCopy_whenCalled_thenReturnedObjectIsDeepCopy()
    {
        int[] data = { 1, 2, 3 };
        BenchmarkData benchmarkData = new BenchmarkData(data, BenchmarkData.DataType.SORTED);

        int[] dataCopy = benchmarkData.getDataCopy();

        assertArrayEquals(dataCopy, data);
        assertNotSame(dataCopy, data);
    }

    /**
     * Tests the {@link com.github.rlacher.sortbench.benchmark.data.BenchmarkData#getLength()} method to ensure it returns the correct length of the data array.
     */
    @Test
    void getLength_whenCalled_thenReturnsCorrectLength()
    {
        int[] data = { 1, 2, 3 };
        BenchmarkData benchmarkData = new BenchmarkData(data, BenchmarkData.DataType.SORTED);

        assertEquals(data.length, benchmarkData.getLength());
    }

    /**
     * Tests the {@link com.github.rlacher.sortbench.benchmark.data.BenchmarkData#getType()} method to ensure it returns the correct type of the data.
     */
    @Test
    void getType_whenCalled_thenReturnsCorrectType()
    {
        int[] data = { 1, 2, 3 };
        BenchmarkData benchmarkData = new BenchmarkData(data, BenchmarkData.DataType.SORTED);

        assertEquals(BenchmarkData.DataType.SORTED, benchmarkData.getType());
    }

    /**
     * Tests the {@link com.github.rlacher.sortbench.benchmark.data.BenchmarkData#toString()} method to ensure it returns a string representation of the object.
     */
    @Test
    void toString_whenCalled_thenReturnsStringWithRelevantInfo()
    {
        int[] data = { 1337, 42 };
        BenchmarkData benchmarkData = new BenchmarkData(data, BenchmarkData.DataType.SORTED);
        String toStringOutput = benchmarkData.toString();

        assertTrue(toStringOutput.contains(BenchmarkData.DataType.SORTED.toString()),"toString() should contain the data type.");
        assertTrue(toStringOutput.contains(String.valueOf(data[0])),"toString() should contain the first data element.");
        assertTrue(toStringOutput.contains(String.valueOf(data[1])),"toString() should contain the second data element.");
        assertTrue(toStringOutput.contains(String.valueOf(data.length)),"toString() should contain the length of the data.");
    }

    /**
     * Tests the {@link com.github.rlacher.sortbench.benchmark.data.BenchmarkData#toString()} method when the data is empty.
     */
    @Test
    void toString_whenDataIsEmpty_thenReturnsStringWithEmptyData()
    {
        int[] data = {};
        BenchmarkData benchmarkData = new BenchmarkData(data, BenchmarkData.DataType.REVERSED);
        String toStringOutput = benchmarkData.toString();
        
        assertTrue(toStringOutput.contains(BenchmarkData.DataType.REVERSED.toString()));
        assertTrue(toStringOutput.contains("length=0"));
    }
}
