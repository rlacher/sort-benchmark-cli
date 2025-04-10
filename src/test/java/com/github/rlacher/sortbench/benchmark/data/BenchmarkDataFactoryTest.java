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
import org.mockito.Mockito;

// Unit tests for the BenchmarkDataFactoryTest class.
class BenchmarkDataFactoryTest
{
    private Random defaultRandom;

    // Helper method to test if the generated data is of the expected type.
    private void createData_givenPositiveLength_shouldReturnDataOfExpectedType(IntFunction<BenchmarkData> factoryMethod, BenchmarkData.DataType expectedType)
    {
        final int length = 10;
        BenchmarkData generatedData = factoryMethod.apply(length);
        assertNotNull(generatedData, "Benchmark data object should not be null");
        assertEquals(expectedType, generatedData.getType(), "Data type should be " + expectedType);
    }

     // Helper method to test if the generated data is of the correct length.
    private void createData_givenPositiveLength_shouldReturnDataOfExpectedLength(IntFunction<BenchmarkData> factoryMethod, final int length)
    {
        BenchmarkData generatedData = factoryMethod.apply(length);
        assertNotNull(generatedData, "Benchmark data object should not be null");
        assertEquals(length, generatedData.getLength(), "Length should be " + length);
    }

    // Helper method to test if an IllegalArgumentException is thrown for non-positive lengths.
    private void createData_givenNonPositiveLength_shouldThrowIllegalArgumentException(IntFunction<BenchmarkData> factoryMethod)
    {
        assertAll(() -> assertThrows(IllegalArgumentException.class, () -> factoryMethod.apply(0), "Creating data with zero length should throw IllegalArgumentException"),
                  () -> assertThrows(IllegalArgumentException.class, () -> factoryMethod.apply(-3), "Creating data with negative length should throw IllegalArgumentException"));
    }

    // Helper method to test if differente instances are created when the factory method is called twice.
    private void createData_whenCalledTwice_thenReturnsDifferentInstances(IntFunction<BenchmarkData> factoryMethod)
    {
        final int length = 10;
        BenchmarkData generatedData1 = factoryMethod.apply(length);
        BenchmarkData generatedData2 = factoryMethod.apply(length);
 
        assertNotNull(generatedData1, "First generated data should not be null");
        assertNotNull(generatedData2, "Second generated data should not be null");  
        assertNotSame(generatedData1, generatedData2, "Each call should return a different instance");
    }

    // Sets up the default random instance before each test.
    @BeforeEach
    void setUp()
    {
        defaultRandom = new Random();
        BenchmarkDataFactory.setRandom(defaultRandom);
    }

    // Tests if a BenchmarkData object is created with the expected length given a positive length.
   @Test
   void createSortedData_givenPositiveLength_shouldReturnDataOfExpectedLength()
   {
       createData_givenPositiveLength_shouldReturnDataOfExpectedLength(BenchmarkDataFactory::createSortedData, 10);
   }

    // Tests if a BenchmarkData object is created with the expected length given a positive length.
    @Test
    void createRandomData_givenPositiveLength_shouldReturnDataOfExpectedLength()
    {
        createData_givenPositiveLength_shouldReturnDataOfExpectedLength(BenchmarkDataFactory::createRandomData, 10);
    }

    // Tests if a BenchmarkData object is created with the expected length given a positive length.
    @Test
    void createReversedData_givenPositiveLength_shouldReturnDataOfExpectedLength()
    {
        createData_givenPositiveLength_shouldReturnDataOfExpectedLength(BenchmarkDataFactory::createReversedData, 10);
    }

    // Tests if a BenchmarkData object is created with the expected length given a length greater or equal to 2.
    @Test
    void createPartiallySortedData_givenPositiveLength_shouldReturnDataOfExpectedLength()
    {
        createData_givenPositiveLength_shouldReturnDataOfExpectedLength(BenchmarkDataFactory::createPartiallySortedData, 10);
    }

    // Tests if a BenchmarkData object is created with the expected type if the length is positive.
   @Test
   void createSortedData_givenPositiveLength_shouldReturnDataOfExpectedType()
   {
       createData_givenPositiveLength_shouldReturnDataOfExpectedType(BenchmarkDataFactory::createSortedData, BenchmarkData.DataType.SORTED);
   }

    // Tests if a BenchmarkData object is created with the expected type if the length is positive.
    @Test
    void createRandomData_givenPositiveLength_shouldReturnDataOfExpectedType()
    {
        createData_givenPositiveLength_shouldReturnDataOfExpectedType(BenchmarkDataFactory::createRandomData, BenchmarkData.DataType.RANDOM);
    }

    // Tests if a BenchmarkData object is created with the expected type if the length is positive.
    @Test
    void createReversedData_givenPositiveLength_shouldReturnDataOfExpectedType()
    {
        createData_givenPositiveLength_shouldReturnDataOfExpectedType(BenchmarkDataFactory::createReversedData, BenchmarkData.DataType.REVERSED);
    }

    // Tests if a BenchmarkData object is created with the expected type if the length is positive.
    @Test
    void createPartiallySortedData_givenPositiveLength_shouldReturnDataOfExpectedType()
    {
        createData_givenPositiveLength_shouldReturnDataOfExpectedType(BenchmarkDataFactory::createPartiallySortedData, BenchmarkData.DataType.PARTIALLY_SORTED);
    }

    // Tests if an IllegalArgumentException is thrown for non-positive lengths.
    @Test
    void createSortedData_givenNonPositiveLength_shouldThrowIllegalArgumentException()
    {
        createData_givenNonPositiveLength_shouldThrowIllegalArgumentException(BenchmarkDataFactory::createSortedData);
    }

    // Tests if an IllegalArgumentException is thrown for non-positive lengths.
    @Test
    void createRandomData_givenNonPositiveLength_shouldThrowIllegalArgumentException()
    {
        createData_givenNonPositiveLength_shouldThrowIllegalArgumentException(BenchmarkDataFactory::createRandomData);
    }

    // Tests if an IllegalArgumentException is thrown for non-positive lengths.
    @Test
    void createReversedData_givenNonPositiveLength_shouldThrowIllegalArgumentException()
    {
        createData_givenNonPositiveLength_shouldThrowIllegalArgumentException(BenchmarkDataFactory::createReversedData);
    }

    // Tests if an IllegalArgumentException is thrown for non-positive lengths.
    @Test
    void createPartiallySortedData_givenNonPositiveLength_shouldThrowIllegalArgumentException()
    {
        createData_givenNonPositiveLength_shouldThrowIllegalArgumentException(BenchmarkDataFactory::createPartiallySortedData);
    }

    // Tests if different instances are created when createSortedData() is called twice.
    @Test
    void createSortedData_whenCalledTwice_thenReturnsDifferentInstances()
    {
    createData_whenCalledTwice_thenReturnsDifferentInstances(BenchmarkDataFactory::createSortedData);
    }

    // Tests if different data is generated when createRandomData() is called twice.
    @Test
    void createRandomData_whenCalledTwice_thenReturnsDifferentData()
    {
        final int length = 10;
        BenchmarkData generatedData1 = BenchmarkDataFactory.createRandomData(length);
        BenchmarkData generatedData2 = BenchmarkDataFactory.createRandomData(length);

        int[] data1 = generatedData1.getDataCopy();
        int[] data2 = generatedData2.getDataCopy();

        assertNotEquals(data1, data2, "Each call should return different data");
    }

    // Tests if the generated data is sorted when the length is positive.
    @Test
    void createSortedData_givenPositiveLength_shouldReturnSortedData()
    {
        final int length = 10;
        BenchmarkData generatedData = BenchmarkDataFactory.createSortedData(length);

        assertNotNull(generatedData, "Returned benchmark data object should not be null");

        int[] data = generatedData.getDataCopy();

        assertNotNull(data, "Data array should not be null");

        int[] sortedData = generatedData.getDataCopy();
        Arrays.sort(sortedData);

        assertArrayEquals(sortedData, data, "Generated data should be sorted");
    }

    // Tests if a BenchmarkData object is created in the edge case of length 1.
    @Test
    void createSortedData_whenLengthIsOne_shouldReturnDataOfLengthOne()
    {
        final int length = 1;
        BenchmarkData generatedData = BenchmarkDataFactory.createSortedData(length);

        assertNotNull(generatedData, "Returned benchmark data object should not be null");
        assertEquals(length, generatedData.getLength(), "Length should be 1");
    }

    // Tests if a IllegalArgumentException is thrown in the edge case of length 1.
    @Test
    void createPartiallySortedData_whenLengthIsOne_shouldThrowIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> BenchmarkDataFactory.createPartiallySortedData(1), "Creating partially sorted data with length 1 should throw IllegalArgumentException");
    }

    // Tests if a BenchmarkData object is created in the edge case of length 2.
    @Test
    void createPartiallySortedData_whenLengthIsTwo_shouldReturnDataOfLengthTwo()
    {
        final int length = 2;
        BenchmarkData generatedData = BenchmarkDataFactory.createPartiallySortedData(length);
 
        assertNotNull(generatedData, "Returned benchmark data object should not be null");
        assertEquals(length, generatedData.getLength(), "Length should be 2");
    }

    // Tests if the generated data is half sorted half random for a given positive length.
    @Test
    void createPartiallySortedData_randomTrue_returnsHalfSortedRandom()
    {
        Random randomMock = Mockito.mock(Random.class);
        when(randomMock.nextBoolean()).thenReturn(true);
        final int length = 10;
        final int[] randomInts = {7, -5, 20, 2, -15};
        when(randomMock.ints(length / 2)).thenReturn(IntStream.of(randomInts));
        BenchmarkDataFactory.setRandom(randomMock);

        BenchmarkData generatedData = BenchmarkDataFactory.createPartiallySortedData(length);
        assertNotNull(generatedData, "Returned benchmark data object must not be null");

        int[] data = generatedData.getDataCopy();
        assertNotNull(data, "Data array must not be null");

        final boolean firstHalfSorted = IntStream.range(0, length / 2 - 1)
            .allMatch(i -> data[i] <= data[i + 1]);

        assertTrue(firstHalfSorted, "First half should be sorted");

        int [] dataSecondHalf = Arrays.copyOfRange(data, length / 2, length);
        assertArrayEquals(randomInts, dataSecondHalf, "Second half must match the specified random integers");
    }

    // Tests if the generated data is half sorted half random for a given positive length.
    @Test
    void createPartiallySortedData_randomFalse_returnsHalfRandomSorted()
    {
        Random randomMock = Mockito.mock(Random.class);
        when(randomMock.nextBoolean()).thenReturn(false);
        final int length = 10;
        final int[] randomInts = {7, -5, 20, 2, -15};
        when(randomMock.ints(length / 2)).thenReturn(IntStream.of(randomInts));
        BenchmarkDataFactory.setRandom(randomMock);

        BenchmarkData generatedData = BenchmarkDataFactory.createPartiallySortedData(length);
        assertNotNull(generatedData, "Returned benchmark data object must not be null");

        int[] data = generatedData.getDataCopy();
        assertNotNull(data, "Data array must not be null");

        final boolean secondHalfSorted = IntStream.range(length / 2, length - 1)
            .allMatch(i -> data[i] <= data[i + 1]);

        int [] dataFirstHalf = Arrays.copyOfRange(data, 0, length / 2);
        assertArrayEquals(randomInts, dataFirstHalf, "First half must match the specified random integers");

        assertTrue(secondHalfSorted, "Second half should be sorted");
    }

    // Tests if setRandom() throws an IllegalArgumentException when a null random instance is passed.
    @Test
    void setRandom_givenNull_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> BenchmarkDataFactory.setRandom(null), "setRandom() should throw IllegalArgumentException when random is null");
    }
}
