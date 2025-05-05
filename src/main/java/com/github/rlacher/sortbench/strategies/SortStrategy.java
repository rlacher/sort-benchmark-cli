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

import com.github.rlacher.sortbench.results.BenchmarkMetric;

/**
 * Defines the contract for sorting algorithms.
 */
public interface SortStrategy
{
    /**
     * Sorts the given integer array and returns profiling metrics.
     *
     * @param array The array to be sorted. Must not be null.
     * @return A {@link BenchmarkMetric} object containing profiling metrics.
     * @throws NullPointerException If the input array is null.
     */
    BenchmarkMetric sort(final int[] array);

    /**
     * Returns the name of the sorting strategy.
     *
     * <p>By convention, the name is derived directly from the sort strategy's class name.</p>
     *
     * @return The unique name of the sorting strategy (e.g. "MergeSort").
     * @throws IllegalStateException If the class name does not end with "Strategy".
     */
    default String name()
    {
        String simpleClassName = this.getClass().getSimpleName();
        String commonSuffix = "Strategy";

        if(!simpleClassName.endsWith(commonSuffix))
        {
            throw new IllegalStateException(String.format("Sort strategy class name must end with '%s'", commonSuffix));
        }

        return simpleClassName.substring(0, simpleClassName.length() - commonSuffix.length());
    }

    /**
     * Swaps two elements at the specified indices within the given array.
     *
     * <p>This method assumes a non-null array and valid indices for optimal performance,
     * and therefore omits argument checks.</p>
     *
     * @param array The array in which the elements are to be swapped.
     * @param i The index of one element to be swapped.
     * @param j The index of the other element to be swapped.
     * @throws NullPointerException if {@code array} is {@code null}.
     * @throws ArrayIndexOutOfBoundsException if either {@code i} or {@code j} is out of bounds.
     */
    default void swap(final int[] array, final int i, final int j)
    {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
