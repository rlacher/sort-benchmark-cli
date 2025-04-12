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

import java.util.logging.Logger;
 
import com.github.rlacher.sortbench.benchmark.BenchmarkResult;
import com.github.rlacher.sortbench.benchmark.Benchmarker;
import com.github.rlacher.sortbench.sorter.Sorter;

/**
 * Implements the bubble sort algorithm as a sorting strategy.
 * 
 * This class implements the {@link SortStrategy} interface and provides a method
 * to perform bubble sort on an array of integers.
 */
public class BubbleSortStrategy implements SortStrategy
{
    /** 
     * Logger for logging messages.
     */
    private static final Logger logger = Logger.getLogger(BubbleSortStrategy.class.getName());

    /**
     * The benchmarker used for profiling.
     */
    private Benchmarker benchmarker;

    /**
     * Constructor for the DummySortStrategy class.
     * 
     * @param benchmarker The benchmarker to be used for profiling.
     * @throws IllegalArgumentException If the benchmarker is null.
     */
    public BubbleSortStrategy(Benchmarker benchmarker)
    {
        if (benchmarker == null)
        {
            throw new IllegalArgumentException("Benchmarker must not be null.");
        }

        this.benchmarker = benchmarker;
        logger.finest(BubbleSortStrategy.class.getSimpleName() + " initialised with benchmarker.");
    }

    /**
     * Sorts the provided array in ascending order using the bubble sort algorithm.
     * 
     * Summary of bubble sort algorithm:
     * 
     * 1. Compare adjacent elements in the array.
     * 2. If the first element is greater than the second, swap them.
     * 3. Repeat the process for all elements in the array.
     * 4. The array is sorted when no swaps are needed in a complete pass.
     * 
     * No argument validation is performed on the input array as the delegating
     * {@link Sorter} class is responsible for it.
     * 
     * @param array The array to be sorted.
     * @return The sorted array.
     */
    @Override
    public BenchmarkResult sort(final int[] array)
    {
        benchmarker.startProfiling();

        int r = array.length - 1;
        int j;

        while(r > 0)
        {
            j = 0;
            for (int i = 0; i < r; ++i)
            {
                if (array[i] > array[i + 1])
                {
                    swap(array, i, i + 1);
                    benchmarker.incrementSwaps();
                    j = i;
                }
            }
            r = j;
        }

        benchmarker.stopProfiling();
        logger.finest(BubbleSortStrategy.class.getSimpleName() + " completed sorting.");

        return benchmarker.getResult();
    }
}
