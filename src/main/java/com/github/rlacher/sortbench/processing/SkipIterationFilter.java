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

package com.github.rlacher.sortbench.processing;

import java.util.function.Predicate;

import com.github.rlacher.sortbench.results.BenchmarkResult;

/**
 * Skips the initial number of iterations
 *
 * Provides a basic JVM warmup by ignoring the first few benchmark runs,
 * allowing time for JVM optimisation. The number of initial iterations
 * to skip is configured via the {@code iterationsToSkip} constructor parameter.
 */
public class SkipIterationFilter implements Predicate<BenchmarkResult>
{
    /** The number of initial iterations to skip. */
    private final int iterationsToSkip;

    /** Tracks the number of iterations processed. */
    private int iterationCounter;

    /**
     * Constructs a filter to skip a specified number of initial iterations.
     * 
     * @param iterationsToSkip The number of initial iterations to skip.
     * @throws IllegalArgumentException If {@code iterationsToSkip} are negative.
     */
    public SkipIterationFilter(final int iterationsToSkip)
    {
        if(iterationsToSkip < 0)
        {
            throw new IllegalArgumentException("Iterations to skip must be non-negative.");
        }

        this.iterationsToSkip = iterationsToSkip;
        this.iterationCounter = 0;
    }

    /**
     * Evaluates if a benchmark result should be processed (i.e., not skipped).
     *
     * Skips the first {@code iterationsToSkip} results.
     *
     * @param result The {@link BenchmarkResult} instance to test. Must not be null.
     * @return {@code true} if the result should be processed, {@code false} if it should be skipped.
     * @throws IllegalArgumentException If {@code result} is null.
     */
    @Override
    public boolean test(BenchmarkResult result)
    {
        if(result == null)
        {
            throw new IllegalArgumentException("Benchmark result must not be null");
        }

        ++iterationCounter;
        return iterationCounter > iterationsToSkip;
    }
}