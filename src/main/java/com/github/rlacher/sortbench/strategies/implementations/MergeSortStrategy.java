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
  * Implements the merge sort algorithm as a sorting strategy.
  * 
  * This class implements the {@link SortStrategy} interface and provides a method
  * to perform merge sort on an array of integers.
  */
 public class MergeSortStrategy implements SortStrategy
 {
     /** 
      * Logger for logging messages.
      */
     private static final Logger logger = Logger.getLogger(MergeSortStrategy.class.getName());
 
     /**
      * The benchmarker used for profiling.
      */
     private Benchmarker benchmarker;
 
     /**
      * Constructor for the MergeSortStrategy class.
      * 
      * @param benchmarker The benchmarker to be used for profiling.
      * @throws IllegalArgumentException If the benchmarker is null.
      */
     public MergeSortStrategy(Benchmarker benchmarker)
     {
         if (benchmarker == null)
         {
             throw new IllegalArgumentException("Benchmarker must not be null.");
         }
 
         this.benchmarker = benchmarker;
         logger.finest(MergeSortStrategy.class.getSimpleName() + " initialised with benchmarker.");
     }

     /**
     * Returns the unique name of the MergeSortStrategy
     *
     * @return The unique name of the strategy.
     */
    @Override
    public String name()
    {
        return "MergeSort";
    }

     /**
      * Sorts the provided array in ascending order using the merge sort algorithm.
      *
      * Merge sort is a divide-and-conquer algorithm that recursively divides the array
      * into single-element subarrays. These subarrays are then merged pairwise to
      * produce the sorted result.
      * 
      * Input array validation is assumed to be performed by the calling {@link Sorter} class.
      * 
      * @param array The array to be sorted.
      * @return The sorted array.
      */
     @Override
     public BenchmarkMetric sort(final int[] array)
     {
         benchmarker.startProfiling();
 
         if(array.length > 1)
         {
            mergeSortRecursive(array, 0, array.length);
         }
 
         benchmarker.stopProfiling();
         logger.finest(MergeSortStrategy.class.getSimpleName() + " completed sorting.");
 
         return benchmarker.getMetric();
     }


    /**
     * Recursively sorts the array using merge sort.
     *
     * Divides the array and merges sorted subarrays using {@link #twoWayMerge(int[], int, int, int)}.
     *
     * @param array The array to sort.
     * @param left Start index (inclusive).
     * @param rightExcl End index (exclusive).
     */
    private void mergeSortRecursive(final int[] array, final int left, final int rightExcl)
    {
        // Subarray size > 1
        if(rightExcl - left > 1)
        {
            final int mid = (left + rightExcl) / 2;
            
            mergeSortRecursive(array, left, mid);
            mergeSortRecursive(array, mid, rightExcl);

            twoWayMerge(array, left, mid, rightExcl);
        }

        benchmarker.measureMemory();
    }

    /**
     * Merges two consecutive sorted subarrays in-place within the given array.
     *
     * @param array The array to modify.
     * @param leftStart Start index (inclusive) of the left subarray.
     * @param rightStartEnd End index (exclusive) of the left and start (inclusive) of the right subarray.
     * @param rightEndExcl End index (exclusive) of the right subarray.
     * @throws NullPointerException if the array is null.
     */
     private void twoWayMerge(final int[] array,
                              final int leftStart,
                              final int rightStartEnd,
                              final int rightEndExcl)
     {
        final int length = rightEndExcl - leftStart;
        final int[] buffer = new int[length];
        int bufferIndex = 0;
        int leftIndex = leftStart;
        int rightIndex = rightStartEnd;

        while (leftIndex < rightStartEnd && rightIndex < rightEndExcl)
        {
            final int leftValue = array[leftIndex];
            final int rightValue = array[rightIndex];

            // Ensure sorting is stable using '<='
            if (leftValue <= rightValue)
            {
                buffer[bufferIndex++] = leftValue;
                leftIndex++;
                benchmarker.reportWrite();
            }
            else
            {
                buffer[bufferIndex++] = rightValue;
                rightIndex++;
                benchmarker.reportWrite();
            }
        }

        // Copy remaining elements, if any
        System.arraycopy(array, leftIndex, buffer, bufferIndex, rightStartEnd - leftIndex);
        benchmarker.reportWrites(rightStartEnd - leftIndex);
        System.arraycopy(array, rightIndex, buffer, bufferIndex, rightEndExcl - rightIndex);
        benchmarker.reportWrites(rightEndExcl - rightIndex);

        System.arraycopy(buffer, 0, array, leftStart, length);
        benchmarker.reportWrites(length);
     }
 }
 