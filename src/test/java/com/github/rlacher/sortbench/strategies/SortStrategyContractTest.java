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

package com.github.rlacher.sortbench.strategies;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import com.github.rlacher.sortbench.benchmark.Benchmarker;
import com.github.rlacher.sortbench.strategies.implementations.*;

/*
 * Test class for all concrete implementations of the SortStrategy interface.
 * 
 * This test class shares test logic utilising parameterised test methods.
 */
public class SortStrategyContractTest
{
    // Helper method to provide a stream of SortStrategy implementation instances for parameterised testing.
    static Stream<SortStrategy> sortStrategies()
    {
        Benchmarker mockBenchmarker = Mockito.mock(Benchmarker.class);

        return Stream.of
        (
            new BubbleSortStrategy(mockBenchmarker),
            new MergeSortStrategy(mockBenchmarker)
        );
    }

    // Tests the constructor of the BubbleSortStrategy class when the benchmarker is null.
    @Test
    void bubbleSortConstructor_givenNullArgument_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new BubbleSortStrategy(null), "Constructor should throw IllegalArgumentException when benchmarker is null");
    }

    // Tests the constructor of the MergeSortStrategy class when the benchmarker is null.
    @Test
    void mergeSortConstructor_givenNullArgument_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new MergeSortStrategy(null), "Constructor should throw IllegalArgumentException when benchmarker is null");
    }

    /*
     * Tests the sort() method given an empty array.
     * 
     * This test is parameterised to run with all concrete implementations of the SortStrategy interface.
     */
    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_givenEmptyArray_returnsEmptyArray(SortStrategy strategy)
    {
        int[] array = {};
        strategy.sort(array);
        final int[] expectedArray = {};
        assertArrayEquals(expectedArray, array, "Sorting an empty array should return an empty array");
    }

    // Tests the sort() method given an array with one element.
    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_givenOneElement_returnsSameArray(SortStrategy strategy)
    {
        int[] array = { 1 };
        strategy.sort(array);
        final int[] expectedArray = { 1 };
        assertArrayEquals(expectedArray, array, "Sorting an array with one element should return the same array");
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_givenTwoElementsInOrder_returnsSameArray(SortStrategy strategy)
    {
        int[] array = { 1, 2 };
        strategy.sort(array);
        final int[] expectedArray = { 1, 2 };
        assertArrayEquals(expectedArray, array, "Sorting an array with two elements in order should return the same array");
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_givenTwoElementsOutOfOrder_returnsSortedArray(SortStrategy strategy)
    {
        int[] array = { 2, 1 };
        strategy.sort(array);
        final int[] expectedArray = { 1, 2 };
        assertArrayEquals(expectedArray, array, "Sorting an array with two elements out of order should return the sorted array");
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_givenRandomElements_returnsSortedArray(SortStrategy strategy)
    {
        int[] array = { 1, 7, 6, 4, 3 };
        strategy.sort(array);
        final int[] expectedArray = { 1, 3, 4, 6, 7 };
        assertArrayEquals(expectedArray, array, "Sorting an array with three elements in order should return the same array");
    }

    @ParameterizedTest
    @MethodSource("sortStrategies") 
    void sort_givenSameElements_returnsSameArray(SortStrategy strategy)
    {
        int[] array = { 1, 1, 1 };
        strategy.sort(array);
        final int[] expectedArray = { 1, 1, 1 };
        assertArrayEquals(expectedArray, array, "Sorting an array with the same elements should return the same array");
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_givenExtremeValues_returnSortedArray(SortStrategy strategy)
    {
        int[] array = { Integer.MAX_VALUE, Integer.MIN_VALUE, 0 };
        strategy.sort(array);
        final int[] expectedArray = { Integer.MIN_VALUE, 0, Integer.MAX_VALUE };
        assertArrayEquals(expectedArray, array, "Sorting an array with extreme values should return the sorted array");
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_givenNegativeValues_returnsSortedArray(SortStrategy strategy)
    {
        int[] array = { -1, -7, -6, -4, -3 };
        strategy.sort(array);
        final int[] expectedArray = { -7, -6, -4, -3, -1 };
        assertArrayEquals(expectedArray, array, "Sorting an array with negative values should return the sorted array");
    }
}
