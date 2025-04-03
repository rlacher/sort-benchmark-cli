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

import java.util.function.Function;

import com.github.rlacher.sortbench.sorter.SortContext;

/**
 * Interface for components that benchmark the performance of operations.
 * 
 * Implementations of this interface measure and accrue performance metrics for a given operation,
 * represented by a {@link java.util.function.Function}.
 */
public interface Benchmarkable
{
    /**
     * Measures and returns performance metrics for the provided operation.
     *
     * @param func The operation to benchmark, represented as a {@link java.util.function.Function}.
     * @param context The context in which the operation is executed, encapsulating strategy.
     * @return An {@link BenchmarkResult} object containing benchmark performance metrics.
     */
    BenchmarkResult benchmark(Function<SortContext, Long> func, SortContext context);
}
