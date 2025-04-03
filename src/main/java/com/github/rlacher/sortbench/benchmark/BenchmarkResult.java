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

/**
 * Stores the result of a benchmark operation.
 */
public final class BenchmarkResult
{
    /**
     * The time taken to execute the operation in milliseconds.
     */
    private final long executionTimeMs;

    /**
     * The amount of runtime memory used during the operation in kilobytes.
     */
    private final long memoryUsedKb;

    /**
     * The number of swaps performed during the operation.
     */
    private final long swapCount;

    /**
     * Constructs a new BenchmarkResult object.
     *
     * @param executionTimeMs The time taken to execute the operation in milliseconds.
     * @param memoryUsedKb The amount of runtime memory used during the operation in kilobytes.
     * @param swapCount The number of swaps performed during the operation.
     * @throws IllegalArgumentException If either executionTimeMs, memoryUsedKb, or swapCount is negative.
     */
    public BenchmarkResult(final long executionTimeMs, final long memoryUsedKb, final long swapCount)
    {
        if(executionTimeMs < 0)
        {
            throw new IllegalArgumentException("Execution time must not be negative.");
        }
        if(memoryUsedKb < 0)
        {
            throw new IllegalArgumentException("Memory use must not be negative.");
        }
        if(swapCount < 0)
        {
            throw new IllegalArgumentException("Swap count must not be negative.");
        }   

        this.executionTimeMs = executionTimeMs;
        this.memoryUsedKb = memoryUsedKb;
        this.swapCount = swapCount;
    }
    /**
     * Returns the execution time in milliseconds.
     *
     * @return The execution time in milliseconds.
     */
    public long getExecutionTimeMs()
    {
        return executionTimeMs;
    }
    /**
     * Returns the memory used in kilobytes.
     *
     * @return The memory used in kilobytes.
     */
    public long getMemoryUsedKb()
    {
        return memoryUsedKb;
    }

    /**
     * Returns the number of swaps performed.
     *
     * @return The number of swaps performed.
     */
    public long getSwapCount()
    {
        return swapCount;
    }

    /**
     * Turns the BenchmarkResult into a string representation.
     *
     * @return A string representation of the benchmark result.
     */
    @Override
    public String toString()
    {
        return  "{executionTimeMs=" + executionTimeMs +
                ", memoryUsedKb=" + memoryUsedKb +
                ", swapCount=" + swapCount +
                "}";
    }   
}
