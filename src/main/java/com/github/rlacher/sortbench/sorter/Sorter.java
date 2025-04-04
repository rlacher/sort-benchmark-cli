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

package com.github.rlacher.sortbench.sorter;

import java.util.logging.Logger;

import com.github.rlacher.sortbench.benchmark.BenchmarkResult;
import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
import com.github.rlacher.sortbench.strategies.SortStrategy;

/**
 * Context class for sorting algorithms, implementing the {@link SortContext} interface and utilising the Strategy design pattern.
 * 
 * This class encapsulates the sorting logic, delegating the actual sorting operation to a {@link SortStrategy}.
 * This allows for dynamic switching of sorting algorithms at runtime.
 * 
 * The sorter centralises argument validation to ensure data integrity.
 */
public class Sorter implements SortContext
{
    /** 
     * Logger for logging messages.
     */
    private static final Logger logger = Logger.getLogger(Sorter.class.getName());

    /**
     * The current sorting strategy.
     */
    private SortStrategy sortStrategy;

    /**
     * Constructor for the Sorter class.
     * 
     * @param sortStrategy The sorting strategy to be used.
     * @throws IllegalArgumentException If the sort strategy is null.
     */
    public Sorter(SortStrategy sortStrategy)
    {
        if(sortStrategy == null)
        {
            throw new IllegalArgumentException("Sort strategy must not be null.");
        }

        logger.fine(String.format("Set initial sort strategy to %s", sortStrategy.getClass().getSimpleName()));
        this.sortStrategy = sortStrategy;
    }

    /**
     * Sets the sorting strategy to be used.
     * 
     * @throws IllegalArgumentException If the sort strategy is null.
     * @param sortStrategy The sorting strategy to be used.
     */
    public void setStrategy(SortStrategy sortStrategy)
    {
        if(sortStrategy == null)
        {
            throw new IllegalArgumentException("Sort strategy must not be null.");
        }

        logger.fine(String.format("Setting sort strategy to %s", sortStrategy.getClass().getSimpleName()));
        this.sortStrategy = sortStrategy;
    }

    /**
     * Validates the input array and delegates the sorting task to the currently set {@link SortStrategy}.
     * 
     * It implements the {@link SortContext} interface.
     * 
     * @param array The array to be sorted.
     * @return Returns a {@link BenchmarkResult} with the metric value for the {@link ProfilingMode}.
     * @throws IllegalArgumentException If the array is null or empty.
     */
    @Override
    public BenchmarkResult sort(final int[] array)
    {
        if(array == null)
        {
            throw new IllegalArgumentException("Array must not be null.");
        }

        if(array.length == 0)
        {
            logger.info("Array is empty, no sorting performed.");
            return new BenchmarkResult(ProfilingMode.NONE, 0);
        }

        logger.finer(String.format("Performing sort using sort strategy %s", sortStrategy.getClass().getSimpleName()));
        return sortStrategy.sort(array);
    }
}
