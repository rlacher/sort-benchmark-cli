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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Unit tests for the BenchmarkData class.
class BenchmarkDataTest
{
    private int[] data;
    private BenchmarkData benchmarkData;

    @BeforeEach
    void setUp()
    {
        data = new int[] { 1, 2, 3 };
        benchmarkData = new BenchmarkData(data, BenchmarkData.DataType.SORTED);
    }

    @Test
    void constructor_nullData_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkData(null, BenchmarkData.DataType.RANDOM),
                     "Constructor with null data should throw IllegalArgumentException");
    }

    @Test
    void constructor_validParams_initFields()
    {
        final BenchmarkData dataObject = new BenchmarkData(data, BenchmarkData.DataType.REVERSED);

        assertArrayEquals(data, dataObject.getData(),
                          "Constructor with valid params should initialise data field");
        assertEquals(BenchmarkData.DataType.REVERSED, dataObject.getType(),
                     "Constructor with valid params should initialise type field");
    }

    @Test
    void constructor_validParams_deepCopiesData()
    {
        final BenchmarkData dataObject = new BenchmarkData(data, BenchmarkData.DataType.RANDOM);
        data[0] = 42; // Modify original data

        assertNotEquals(data[0], dataObject.getData()[0], "Constructor should create a deep copy of the data");
    }

    @Test
    void constructor_emptyData_initialisesCorrectly()
    {
        final int[] emptyData = {};
        final BenchmarkData dataObject = new BenchmarkData(emptyData, BenchmarkData.DataType.RANDOM);

        assertArrayEquals(emptyData, dataObject.getData(),
                          "Constructor with empty array should initialise data field");
        assertEquals(0, dataObject.getLength(),
                     "Constructor with empty array should set length to 0");
        assertEquals(BenchmarkData.DataType.RANDOM, dataObject.getType(),
                     "Constructor with empty array should initialise type field");
    }

    @Test
    void copyConstructor_otherIsNull_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkData(null),
                     "Copy constructor with null other should throw IllegalArgumentException");
    }

    @Test
    void copyConstructor_otherHasNullData_throwsIllegalArgumentException()
    {
        final BenchmarkData other = mock(BenchmarkData.class);
        when(other.getData()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkData(other),
                     "Copy constructor with other having null data should throw IllegalArgumentException");
    }

    @Test
    void copyConstructor_validOther_initFields()
    {
        final int[] originalData = { 10, 11, 12 };
        final BenchmarkData original = new BenchmarkData(originalData, BenchmarkData.DataType.SORTED);
        final BenchmarkData copy = new BenchmarkData(original);

        assertArrayEquals(original.getData(), copy.getData(), "Copy constructor should copy the data field");
        assertEquals(original.getLength(), copy.getLength(), "Copy constructor should copy the length field");
        assertEquals(original.getType(), copy.getType(), "Copy constructor should copy the type field");
    }

    @Test
    void getData_whenCalled_returnsSame()
    {
        assertSame(benchmarkData.getData(), benchmarkData.getData(), "getData should return the same instance of the data array");
    }

    @Test
    void getLength_whenCalled_returnsCorrectLength()
    {
        assertEquals(data.length, benchmarkData.getLength(), "getLength should return the correct length of the data array");
    }

    @Test
    void getType_whenCalled_returnsCorrectType()
    {
        assertEquals(BenchmarkData.DataType.SORTED, benchmarkData.getType(), "getType should return the correct type of the data");
    }

    @Test
    void toString_whenCalled_returnsStringWithRelevantInfo()
    {
        final int[] identifiableData = { 1337, 42 };
        final BenchmarkData dataObject = new BenchmarkData(identifiableData, BenchmarkData.DataType.SORTED);
        final String toStringOutput = dataObject.toString();

        assertTrue(toStringOutput.contains(BenchmarkData.DataType.SORTED.toString()),"toString should contain the data type.");
        assertTrue(toStringOutput.contains(String.valueOf(identifiableData[0])),"toString should contain the first data element.");
        assertTrue(toStringOutput.contains(String.valueOf(identifiableData[1])),"toString should contain the second data element.");
        assertTrue(toStringOutput.contains(String.valueOf(identifiableData.length)),"toString should contain the length of the data.");
    }

    @Test
    void toString_emptyData_returnsStringWithEmptyData()
    {
        final int[] emptyData = {};
        final BenchmarkData dataObject = new BenchmarkData(emptyData, BenchmarkData.DataType.REVERSED);
        final String toStringOutput = dataObject.toString();
        final String toStringOutputNoWhitespaces = toStringOutput.replaceAll("\\s+", "");

        assertTrue(toStringOutput.contains(BenchmarkData.DataType.REVERSED.toString()), "toString with empty data should contain the data type");
        assertTrue(toStringOutputNoWhitespaces.contains("length=0"), "toString with empty data should contain length=0");
    }
}