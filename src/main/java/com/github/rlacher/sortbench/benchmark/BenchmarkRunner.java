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

package com.github.rlacher.sortbench.benchmark;

import com.github.rlacher.sortbench.sorter.SortContext;

/**
 * Executes and measures the performance of sorting operations using a provided {@link SortContext}.
 */
public class BenchmarkRunner implements Benchmarkable
{
    /**
     * Measures the performance of a sorting operation.
     * 
     * @param func The sorting operation to benchmark, represented as a {@link java.util.function.Function}.
     * @param context The context providing the sorting logic by encapsulating the sort strategy.
     * @return A {@link BenchmarkResult} object containing the execution time, memory usage, and swap count.
     * @throws IllegalArgumentException If the provided function or context is null.
     */
    @Override
    public BenchmarkResult benchmark(final java.util.function.Function<SortContext, Long> func, final SortContext context)
    {
        if(func == null)
        {
            throw new IllegalArgumentException("Function must not be null.");
        }

        if(context == null)
        {
            throw new IllegalArgumentException("Context must not be null.");
        }
        
         // Request garbage collection to minimize memory usage before benchmarking
         System.gc();

         final long startTimeNs = System.nanoTime();
         final long startMemoryBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
 
         long swapCount = func.apply(context);
 
         final long endTimeNs = System.nanoTime();
 
         // Request garbage collection again to minimize memory usage after benchmarking
         System.gc();
 
         final long endMemoryBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
 
         final long executionTimeMs = (endTimeNs - startTimeNs) / 1_000_000;
         final long memoryUsedKb = Math.max(0, (endMemoryBytes - startMemoryBytes) / 1024);

         return new BenchmarkResult(executionTimeMs, memoryUsedKb, swapCount);
    }

}
