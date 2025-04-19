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
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;

import com.github.rlacher.sortbench.benchmark.data.BenchmarkData;

// Unit tests for the BenchmarkDataFactoryTest class.
class BenchmarkDataFactoryTest
{
    private int dataLength;
    private Random defaultRandom;

    // Sets up the default random instance and data length before each test.
    @BeforeEach
    void setUp()
    {
        defaultRandom = new Random();
        BenchmarkDataFactory.setRandom(defaultRandom);
        dataLength = 10;
    }

    @ParameterizedTest
    @EnumSource(BenchmarkData.DataType.class)
    void createData_validParameters_returnsDataOfExpectedType(BenchmarkData.DataType dataType)
    {
        BenchmarkData generatedData = BenchmarkDataFactory.createData(dataType, dataLength);
        assertEquals(dataType, generatedData.getType(), "Data type of returned instance should be " + dataType);
    }

    @ParameterizedTest
    @EnumSource(BenchmarkData.DataType.class)
    void createData_validParameters_returnsDataOfExpectedLength(BenchmarkData.DataType dataType)
    {
        BenchmarkData generatedData = BenchmarkDataFactory.createData(dataType, dataLength);
        assertEquals(dataLength, generatedData.getLength(), "Length of returned instance should be " + dataLength);
    }

    @ParameterizedTest
    @EnumSource(BenchmarkData.DataType.class)
    void createData_nonPositiveDataLength_throwsIllegalArgumentException(BenchmarkData.DataType dataType)
    {
        assertAll(() -> assertThrows(IllegalArgumentException.class, () -> BenchmarkDataFactory.createData(dataType, -3), "Creating data with zero length should throw IllegalArgumentException"),
                  () -> assertThrows(IllegalArgumentException.class, () -> BenchmarkDataFactory.createData(dataType, 0), "Creating data with negative length should throw IllegalArgumentException"));
    }

    @ParameterizedTest
    @EnumSource(BenchmarkData.DataType.class)
    void createData_calledTwice_returnsDifferentInstances(BenchmarkData.DataType dataType)
    {
        BenchmarkData generatedData1 = BenchmarkDataFactory.createData(dataType, dataLength);
        BenchmarkData generatedData2 = BenchmarkDataFactory.createData(dataType, dataLength); 

        assertNotNull(generatedData1, "Returned benchmark data object should not be null");
        assertNotNull(generatedData2, "Returned benchmark data object should not be null");

        assertNotSame(generatedData1, generatedData2, "Each call should return a different instance");
    }

    @Test
    void createData_calledTwiceDataTypeRandom_returnsDifferentData()
    {
        BenchmarkData.DataType randomDataType = BenchmarkData.DataType.RANDOM;

        BenchmarkData generatedData1 = BenchmarkDataFactory.createData(randomDataType, dataLength);
        BenchmarkData generatedData2 = BenchmarkDataFactory.createData(randomDataType, dataLength);

        assertNotNull(generatedData1, "createData should not return null");
        assertNotNull(generatedData2, "createData should not return null");

        int[] data1 = generatedData1.getData();
        int[] data2 = generatedData2.getData();

        assertNotNull(data1, "getData should not return null");
        assertNotNull(data2, "getData should not return null");

        assertNotEquals(data1, data2, "Each call should return different data");
    }

    @Test
    void createData_dataTypeSorted_returnsSortedData()
    {
        BenchmarkData generatedData = BenchmarkDataFactory.createData(BenchmarkData.DataType.SORTED, dataLength);

        assertNotNull(generatedData, "createData should not return null");

        int[] data = generatedData.getData();

        assertNotNull(data, "getData should not return null");

        int[] sortedData = generatedData.getData();
        Arrays.sort(sortedData);

        assertArrayEquals(sortedData, data, "Generated data should be sorted");
    }

    @ParameterizedTest
    @EnumSource(BenchmarkData.DataType.class)
    void createData_dataLengthOne_returnDataOfLengthOne(BenchmarkData.DataType dataType)
    {
        dataLength = 1;
        BenchmarkData generatedData = BenchmarkDataFactory.createData(dataType, dataLength);

        assertNotNull(generatedData, "createData should not return null");
        assertEquals(dataLength, generatedData.getLength(), "createData should return data of length 1");
    }

    @ParameterizedTest
    @EnumSource(BenchmarkData.DataType.class)
    void createData_dataLengthTwo_returnDataOfLengthTwo(BenchmarkData.DataType dataType)
    {
        dataLength = 2;
        BenchmarkData generatedData = BenchmarkDataFactory.createData(dataType, dataLength);

        assertNotNull(generatedData, "createData should not return null");
        assertEquals(dataLength, generatedData.getLength(), "createData should return data of length 2");
    }

    @Test
    void createData_dataTypePartiallySortedRandomTrue_returnsHalfSortedRandom()
    {
        Random randomMock = Mockito.mock(Random.class);
        when(randomMock.nextBoolean()).thenReturn(true);
        dataLength = 10;
        final int[] randomInts = {7, -5, 20, 2, -15};
        when(randomMock.ints(dataLength / 2, 0, dataLength)).thenReturn(IntStream.of(randomInts));
        BenchmarkDataFactory.setRandom(randomMock);

        BenchmarkData generatedData = BenchmarkDataFactory.createData(BenchmarkData.DataType.PARTIALLY_SORTED, dataLength);
        assertNotNull(generatedData, "createData should not return null");

        int[] data = generatedData.getData();
        assertNotNull(data, "getData should not return null");

        final boolean firstHalfSorted = IntStream.range(0, dataLength / 2 - 1)
            .allMatch(i -> data[i] <= data[i + 1]);

        assertTrue(firstHalfSorted, "First half should be sorted");

        int [] dataSecondHalf = Arrays.copyOfRange(data, dataLength / 2, dataLength);
        assertArrayEquals(randomInts, dataSecondHalf, "Second half must match the specified random integers");
    }

    @Test
    void createData_dataTypePartiallySortedRandomFalse_returnsHalfRandomSorted()
    {
        Random randomMock = Mockito.mock(Random.class);
        when(randomMock.nextBoolean()).thenReturn(false);
        dataLength = 10;
        final int[] randomInts = {7, -5, 20, 2, -15};
        when(randomMock.ints(dataLength / 2, 0, dataLength)).thenReturn(IntStream.of(randomInts));
        BenchmarkDataFactory.setRandom(randomMock);

        BenchmarkData generatedData = BenchmarkDataFactory.createData(BenchmarkData.DataType.PARTIALLY_SORTED, dataLength);
        assertNotNull(generatedData, "createData should not return null");

        int[] data = generatedData.getData();
        assertNotNull(data, "getData should not return null");

        final boolean secondHalfSorted = IntStream.range(dataLength / 2, dataLength - 1)
            .allMatch(i -> data[i] <= data[i + 1]);

        int [] dataFirstHalf = Arrays.copyOfRange(data, 0, dataLength / 2);
        assertArrayEquals(randomInts, dataFirstHalf, "First half must match the specified random integers");

        assertTrue(secondHalfSorted, "Second half should be sorted");
    }

    @Test
    void setRandom_nullArgument_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> BenchmarkDataFactory.setRandom(null), "setRandom should throw IllegalArgumentException when argument is null");
    }
}
