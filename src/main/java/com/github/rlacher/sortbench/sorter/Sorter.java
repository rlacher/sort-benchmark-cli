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

import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
import com.github.rlacher.sortbench.results.BenchmarkMetric;
import com.github.rlacher.sortbench.strategies.SortStrategy;

/**
 * Context class for sorting algorithms, utilising the Strategy design pattern.
 *
 * <p>This class encapsulates the sorting logic, delegating the actual sorting operation to a {@link SortStrategy}.
 * This allows for dynamic switching of sorting algorithms at runtime.</p>
 *
 * <p>For simplicity at this project scale, the Sorter class directly acts as the Strategy pattern's context, rather than using a separate interface.</p>
 * 
 * <p>The sorter centralises argument validation to ensure data integrity.</p>
 */
public class Sorter
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
     * Constructs a default instance of {@link Sorter}.
     *
     * <p>This constructor performs no custom initialisation.</p>
     */
    public Sorter() {}

    /**
     * Sets the sorting strategy to be used.
     *
     * @throws IllegalArgumentException If the sort strategy is {@code null}.
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
     * <p>Requires a prior call to {@link Sorter#setStrategy}.</p>
     *
     * @param array The array to be sorted.
     * @return Returns a {@link BenchmarkMetric} with the metric value for the {@link ProfilingMode}. Returns 0 if the array is empty.
     * @throws IllegalArgumentException If the array is {@code null}.
     * @throws IllegalStateException If the sort strategy is {@code null}.
     */
    public BenchmarkMetric sort(final int[] array)
    {
        if(array == null)
        {
            throw new IllegalArgumentException("Array must not be null.");
        }

        if(sortStrategy == null)
        {
            throw new IllegalStateException("Sort strategy is null");
        }

        if(array.length == 0)
        {
            logger.info("Array is empty, no sorting performed.");
            return new BenchmarkMetric(ProfilingMode.NONE, 0);
        }

        logger.finer(String.format("Performing sort using sort strategy %s", sortStrategy.getClass().getSimpleName()));
        return sortStrategy.sort(array);
    }
}
