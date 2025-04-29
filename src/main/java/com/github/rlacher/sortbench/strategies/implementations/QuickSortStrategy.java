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

package com.github.rlacher.sortbench.strategies.implementations;
import java.util.logging.Logger;
import com.github.rlacher.sortbench.benchmark.Benchmarker;
import com.github.rlacher.sortbench.results.BenchmarkMetric;
import com.github.rlacher.sortbench.sorter.Sorter;
import com.github.rlacher.sortbench.strategies.SortStrategy;

/**
 * Implements the quick sort algorithm as a sorting strategy.
 *
 * <p>This class implements the {@link SortStrategy} interface and provides a method
 * to perform quick sort on an array of integers.</p>
 */
public class QuickSortStrategy implements SortStrategy
{
    /** Logger for logging messages. */
    private static final Logger logger = Logger.getLogger(QuickSortStrategy.class.getName());

    /** The benchmarker used for profiling. */
    private Benchmarker benchmarker;

    /**
     * Constructor for the {@link QuickSortStrategy} class.
     *
     * @param benchmarker The benchmarker to be used for profiling.
     * @throws IllegalArgumentException If the benchmarker is {@code null}.
     */
    public QuickSortStrategy(Benchmarker benchmarker)
    {
        if (benchmarker == null)
        {
            throw new IllegalArgumentException("Benchmarker must not be null.");
        }

        this.benchmarker = benchmarker;
        logger.finest(QuickSortStrategy.class.getSimpleName() + " initialised with benchmarker.");
    }

    /**
     * Sorts the provided array in ascending order using the quick sort algorithm.
     *
     * <p>Summary of quick sort algorithm: Quicksort, using the first element as the
     * pivot, partitions the array such that elements smaller than the pivot are on the
     * left and equal or larger elements are on the right. The pivot is then swapped to
     * its correct sorted position between these partitions. The algorithm recursively
     * sorts the left and right subarrays until they contain zero or one element.</p>
     *
     * <p>Input array validation is assumed to be performed by the calling {@link Sorter} class.</p>
     *
     * @param array The array to be sorted.
     * @return A {@link BenchmarkMetric} containing performance information about the sorting process.
     * @throws NullPointerException If array is {@code null} (though this should be prevented by the caller).
     */
    @Override
    public BenchmarkMetric sort(final int[] array)
    {
        benchmarker.startProfiling();
        quickSort(array, 0, array.length);
        benchmarker.stopProfiling();
        logger.finest(QuickSortStrategy.class.getSimpleName() + " completed sorting.");

        return benchmarker.getMetric();
    }
    /**
     * Recursively sorts a subarray of the given array using the quicksort algorithm.
     *
     * @param array The array to be sorted.
     * @param start The start index (inclusive) of the subarray to sort.
     * @param end The end index (exclusive) of the subarray to sort.
     */
    private void quickSort(final int[] array, final int start, final int end)
    {
       final int size = end - start;
       if(size > 1)
       {
           final int pivotIndex = partition(array, start, end);
           quickSort(array, start, pivotIndex);
           quickSort(array, pivotIndex + 1, end);
       }
    }
    /**
     * Partitions a subarray around the pivot element (the first element of the subarray).
     *
     * <p>Elements smaller than the pivot are moved to the left, and elements greater than or equal to
     * the pivot are moved to the right.</p>
     *
     * <p>This partitioning employs a two-pointer approach, with pointers moving from the left and right of the subarray.
     * Only call with subarrays of size > 1. Subroutine of {@link QuickSortStrategy#quickSort}.</p>
     *
     * @param array The array to be partitioned.
     * @param start The start index (inclusive) of the subarray to partition.
     * @param end The end index (exclusive) of the subarray to partition.
     * @return The final index of the pivot element after partitioning.
     * @throws NullPointerException If the input array is {@code null}.
     * @throws ArrayIndexOutOfBoundsException If {@code start} or {@code end} indices are invalid for the array.
     */
    private int partition(final int[] array, final int start, final int end)
    {
       final int pivotValue = array[start];
       int left = start + 1;
       int right = end - 1;
       while(left <= right)
       {
           // Move left pointer to the right until it finds an element greater than or equal to the pivot
           while(left <= right && array[left] < pivotValue)
           {
               ++left;
           }
           // Move right pointer to the left until it finds an element less than the pivot
           while(left <= right && array[right] >= pivotValue)
           {
               --right;
           }
           // Swap if pointers have not crossed
           if(left < right)
           {
               swap(array, left, right);
               benchmarker.reportSwap();
               ++left;
               --right;
           }
       }
       // Place the pivot in its sorted position
       swap(array, start, right);
       benchmarker.reportSwap();
       return right;
   }
}
