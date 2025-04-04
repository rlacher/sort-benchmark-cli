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

import com.github.rlacher.sortbench.benchmark.BenchmarkResult;
import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;

/**
 * Defines a context for sorting operations, decoupling the sorting algorithms from the caller.
 * 
 * Implementations of this interface encapsulate the sorting process, including 
 * data validation and strategy delegation.
 */
public interface SortContext
{
    /**
     * Sorts the provided integer array using the encapsulated sorting strategy.
     * 
     * @param array The array to be sorted.
     * @return Returns a {@link BenchmarkResult} with the metric value for the {@link ProfilingMode}.
     * @throws IllegalArgumentException If the input array is null.
     */
    BenchmarkResult sort(final int[] array);
}
