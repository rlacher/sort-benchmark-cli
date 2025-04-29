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

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import com.github.rlacher.sortbench.benchmark.Benchmarker;
import com.github.rlacher.sortbench.results.BenchmarkMetric;
import com.github.rlacher.sortbench.strategies.implementations.*;

/*
 * Test contract for all concrete implementations of the SortStrategy interface.
 * 
 * Shares common parameterised tests to ensure consistent sorting behaviour across all concrete implementations.
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
            new HeapSortStrategy(mockBenchmarker),
            new InsertionSortStrategy(mockBenchmarker),
            new MergeSortStrategy(mockBenchmarker),
            new QuickSortStrategy(mockBenchmarker)
        );
    }

    @Test
    void name_nonCompliantClassName_throwsIllegalStateException()
    {
        SortStrategy nonCompliantStrategy = new SortStrategy()
        {
            @Override
            public BenchmarkMetric sort(final int[] array)
            {
                throw new UnsupportedOperationException("Not implemented for testing.");
            }
        };

        assertThrows(IllegalStateException.class, nonCompliantStrategy::name, "name() should throw IllegalStateException if class name does not end with \"Strategy\".");
    }

    @Test
    void name_compliantClassName_returnsCorrectlyStrippedName()
    {
        class ExampleSortStrategy implements SortStrategy
        {
            @Override
            public BenchmarkMetric sort(final int[] array)
            {
                throw new UnsupportedOperationException("Not implemented for testing.");
            }
        }

        SortStrategy exampleSortStrategy = new ExampleSortStrategy();

        String expectedName = "ExampleSort";
        String actualName = exampleSortStrategy.name();

        assertEquals(expectedName, actualName, String.format("Calling name() should return \"%s\", but returned \"%s\"", expectedName, actualName));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_emptyArray_returnsEmptyArray(SortStrategy strategy)
    {
        final int[] array = {};
        strategy.sort(array);

        final int[] expectedArray = {};
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting an empty array should return an empty array", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_singleElementArray_returnsSameArray(SortStrategy strategy)
    {
        final int[] array = {5};
        strategy.sort(array);

        final int[] expectedArray = {5};
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting a single element array should return the same array", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_twoElementsInOrder_returnsSameArray(SortStrategy strategy)
    {
        int[] array = { 1, 2 };
        strategy.sort(array);

        final int[] expectedArray = { 1, 2 };
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting an array with two elements in order should return the same array", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_twoElementsOutOfOrder_returnsSortedArray(SortStrategy strategy)
    {
        int[] array = { 2, 1 };
        strategy.sort(array);

        final int[] expectedArray = { 1, 2 };
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting an array with two elements out of order should return the sorted array", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies") 
    void sort_identicalNumbers_returnsSameArray(SortStrategy strategy)
    {
        int[] array = { 1, 1, 1 };
        strategy.sort(array);
        final int[] expectedArray = { 1, 1, 1 };
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting an array with the same elements should return the same array", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_someNegativeNumbers_returnsSortedArrayWithNegativeNumbers(SortStrategy strategy)
    {
        final int[] array = {-2, 1, -5, 0, 3};
        strategy.sort(array);

        final int[] expectedArray = {-5, -2, 0, 1, 3};
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting an array with negative numbers should return a sorted array with negative numbers", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_allNegativeNumbers_returnsSortedArray(SortStrategy strategy)
    {
        int[] array = { -1, -7, -6, -4, -3 };
        strategy.sort(array);

        final int[] expectedArray = { -7, -6, -4, -3, -1 };
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting an array with all negative numbers should return a sorted array", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_maxAndMinIntegers_returnsSortedArray(SortStrategy strategy)
    {
        final int[] array = {Integer.MAX_VALUE, Integer.MIN_VALUE, 0};
        strategy.sort(array);

        final int[] expectedArray = {Integer.MIN_VALUE, 0, Integer.MAX_VALUE};
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting an array with Integer.MAX_VALUE and Integer.MIN_VALUE should return a sorted array", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_alreadySorted_returnsSameArray(SortStrategy strategy)
    {
        final int[] array = {1, 2, 3, 4, 5};
        strategy.sort(array);

        final int[] expectedArray = {1, 2, 3, 4, 5};
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting an already sorted array should return the same array", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_reverseSortedArray_returnsSortedArray(SortStrategy strategy)
    {
        final int[] array = {5, 4, 3, 2, 1};
        strategy.sort(array);

        final int[] expectedArray = {1, 2, 3, 4, 5};
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting a reverse sorted array should return a sorted array", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_duplicateNumbers_returnsSortedArray(SortStrategy strategy)
    {
        final int[] array = {3, 1, 4, 1, 5, 9, 2, 6, 5};
        strategy.sort(array);

        final int[] expectedArray = {1, 1, 2, 3, 4, 5, 5, 6, 9};
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting an array with duplicates should return a sorted array with duplicates", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_almostSorted_returnsSortedArray(SortStrategy strategy)
    {
        final int[] array = {1, 2, 3, 8, 5, 6, 7};
        strategy.sort(array);

        final int[] expectedArray = {1, 2, 3, 5, 6, 7, 8};
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting an almost sorted array with one element out of place should return a sorted array", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_twoElementsSwapped_returnsSortedArray(SortStrategy strategy)
    {
        final int[] array = {1, 3, 2, 4, 5};
        strategy.sort(array);

        final int[] expectedArray = {1, 2, 3, 4, 5};
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting an array with two elements swapped should return a sorted array", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_manyIdenticalAtStart_returnsSortedArray(SortStrategy strategy)
    {
        final int[] array = {5, 5, 5, 5, 1, 2, 3, 4};
        strategy.sort(array);

        final int[] expectedArray = {1, 2, 3, 4, 5, 5, 5, 5};
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting an array with many identical elements at the start should return a sorted array", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_manyIdenticalAtEnd_returnsSortedArray(SortStrategy strategy)
    {
        final int[] array = {1, 2, 3, 4, 5, 5, 5, 5};
        strategy.sort(array);

        final int[] expectedArray = {1, 2, 3, 4, 5, 5, 5, 5};
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting an array with many identical elements at the end should return a sorted array", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_manyIdenticalInMiddle_returnsSortedArray(SortStrategy strategy)
    {
        final int[] array = {1, 2, 5, 5, 5, 5, 3, 4};
        strategy.sort(array);

        final int[] expectedArray = {1, 2, 3, 4, 5, 5, 5, 5};
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting an array with many identical elements in the middle should return a sorted array", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_largeGaps_returnsSortedArray(SortStrategy strategy)
    {
        final int[] array = {1, 1000, 2, 2000, 3, 3000};
        strategy.sort(array);

        final int[] expectedArray = {1, 2, 3, 1000, 2000, 3000};
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting an array with large gaps should return a sorted array", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_mixedPatternsLongArray_returnsSortedArray(SortStrategy strategy)
    {
        // Input array with mixed positive and negative numbers,
        // some locally ordered chunks, and duplicate values.
        int[] array =
        {
            3, -1, 5, 0, 8, -4, 2, 7, -2, 6,
            9, 1, 4, 0, 7, -3, 5, -5, 8, 2,
            6, -1, 3, 9, 0, 4, 7, -4, 1, 8
        };
        strategy.sort(array);

        final int[] expectedArray = array.clone();
        Arrays.sort(expectedArray);
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting a longer array with mixed positive/negative, local order, and duplicates should return a fully sorted array.", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_nonRecursiveHeapifyBug_returnsSortedArray(SortStrategy strategy)
    {
        final int[] array = {1, 2, 3, 10, 4, 5, 6};
        strategy.sort(array);

        final int[] expectedArray = {1, 2, 3, 4, 5, 6, 10};
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting an array to test for a non-recursive heapify bug should return a sorted array", strategy.name()));
    }

    @ParameterizedTest
    @MethodSource("sortStrategies")
    void sort_mergeSortSubarrayCopyBug_returnsSortedArray(SortStrategy strategy)
    {
        final int[] array = {4, 1, 3, 2};
        strategy.sort(array);

        final int[] expectedArray = {1, 2, 3, 4};
        assertArrayEquals(expectedArray, array, String.format("%s: Sorting an array to test for a merge sort subarray copy bug should return a sorted array", strategy.name()));
    }
}
