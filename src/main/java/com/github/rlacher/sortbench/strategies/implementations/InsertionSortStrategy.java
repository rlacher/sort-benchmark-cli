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
  
 import com.github.rlacher.sortbench.benchmark.BenchmarkResult;
 import com.github.rlacher.sortbench.benchmark.Benchmarker;
 import com.github.rlacher.sortbench.sorter.Sorter;
 import com.github.rlacher.sortbench.strategies.SortStrategy;
 
 /**
  * Implements the insertion sort algorithm as a sorting strategy.
  * 
  * This class implements the {@link SortStrategy} interface and provides a method
  * to perform insertion sort on an array of integers.
  */
 public class InsertionSortStrategy implements SortStrategy
 {
     /** 
      * Logger for logging messages.
      */
     private static final Logger logger = Logger.getLogger(InsertionSortStrategy.class.getName());
 
     /**
      * The benchmarker used for profiling.
      */
     private Benchmarker benchmarker;
 
     /**
      * Constructor for the InsertionSortStrategy class.
      * 
      * @param benchmarker The benchmarker to be used for profiling.
      * @throws IllegalArgumentException If the benchmarker is null.
      */
     public InsertionSortStrategy(Benchmarker benchmarker)
     {
         if (benchmarker == null)
         {
             throw new IllegalArgumentException("Benchmarker must not be null.");
         }
 
         this.benchmarker = benchmarker;
         logger.finest(InsertionSortStrategy.class.getSimpleName() + " initialised with benchmarker.");
     }
 
     /**
      * Sorts the provided array in ascending order using the insertion sort algorithm.
      *
      * Insertion sort builds a sorted subarray from the front.
      * It iterates from the second element to the last, inserting each into its correct position within the sorted subarray.
      * Elements are compared and shifted backwards until the correct position is found.
      * 
      * Input array validation is assumed to be performed by the calling {@link Sorter} class.
      * 
      * @param array The array to be sorted.
      * @return The sorted array.
      */
     @Override
     public BenchmarkResult sort(final int[] array)
     {
         benchmarker.startProfiling();

         for(int i = 1; i < array.length; ++i)
         {
            final int current = array[i];
            int j = i - 1;

            while(j >= 0 && current < array[j])
            {
                shift(array, j);
                benchmarker.incrementSwaps();
                --j;
            }
            insert(array, j + 1, current);
            benchmarker.incrementSwaps();
         }
 
         benchmarker.stopProfiling();
         logger.finest(InsertionSortStrategy.class.getSimpleName() + " completed sorting.");
 
         return benchmarker.getResult();
     }

    /**
     * Shifts a single element one position to the right within the array.
     * Part of insertion sort's placement phase.
     *
     * No array boundary checks due to performance considerations.
     *
     * @param array The array in which the shift occurs.
     * @param index The index of the element to be shifted.
     * @throws NullPointerException if the array is null.
     * @throws ArrayIndexOutOfBoundsException if the index is invalid.
     */
     private void shift(final int[] array, final int index)
     {
        array[index + 1] = array[index];
     }

     /**
     * Inserts an element into the array.
     * Part of insertion sort's placement phase.
     *
     * No array boundary checks due to performance considerations.
     *
     * @param array The array to modify.
     * @param index The insertion index.
     * @param value The value to insert.
     * @throws NullPointerException if the array is null.
     * @throws ArrayIndexOutOfBoundsException if the index is invalid.
     */
     private void insert(final int[] array, final int index, final int value)
     {
        array[index] = value;
     }

 }
 