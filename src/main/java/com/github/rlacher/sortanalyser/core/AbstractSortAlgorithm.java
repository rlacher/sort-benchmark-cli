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

package com.github.rlacher.sortanalyser.core;

public abstract class AbstractSortAlgorithm implements Sortable, Benchmarkable
{
    /**
     * Sorts the provided array in ascending order.
     *
     * This method must be implemented by concrete sorting algorithms to define the specific sorting logic.
     *
     * @param array The array to be sorted.
     * @throws NullPointerException If the provided array is null.
     */
    @Override
    public abstract void sort(final int[] array) throws NullPointerException;

    /**
     * Executes and benchmarks an operation on the provided array.
     * 
     * @param array. The array to be operated on and benchmarked.
     * @return The result of the benchmarked operation in a {@link BenchmarkResult} object.
     * @throws NullPointerException If the array is null, thrown by the internally called sort() method.
     */
    @Override
    public final BenchmarkResult benchmarkedOperation(int[] array) throws NullPointerException
    {
         // Request garbage collection to minimize memory usage before benchmarking
        System.gc();

        final long startTimeNs = System.nanoTime();
        final long startMemoryBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        sort(array);

        final long endTimeNs = System.nanoTime();

        // Request garbage collection again to minimize memory usage after benchmarking
        System.gc();

        final long endMemoryBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        final long executionTimeMs = (endTimeNs - startTimeNs) / 1_000_000;
        final long memoryUsedKb = Math.max(0, (endMemoryBytes - startMemoryBytes) / 1024);
        return new BenchmarkResult(executionTimeMs, memoryUsedKb);
    }
}
