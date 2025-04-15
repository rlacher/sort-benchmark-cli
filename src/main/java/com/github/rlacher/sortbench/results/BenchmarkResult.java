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

package com.github.rlacher.sortbench.results;

import java.util.Objects;

import com.github.rlacher.sortbench.benchmark.BenchmarkRunner;
import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;

/**
 * Represents the raw, per-iteration result of a benchmark run.
 *
 * This class encapsulates the profiling metrics and contextual information
 * collected during a single execution of a sorting algorithm.
 * Instances of this class are created by the {@link BenchmarkRunner}.
 *
 * @see BenchmarkContext
 * @see AggregatedResult
 */
public final class BenchmarkResult
{
    /** The benchmark context that defines the contextual parameters of the benchmark run. */
    private final BenchmarkContext context;

    /** The profiling mode used for this benchmark result. */
    private final ProfilingMode profilingMode;

    /** The benchmark metric value, corresponding to the {@link ProfilingMode}. */
    private final double value;

    /**
     * Constructs a new BenchmarkResult object.
     *
     * @param context The benchmark context. Must not be null.
     * @param profilingMode The profiling mode. Must not be null.
     * @param value The benchmark metric value. Must not be negative.
     * @throws IllegalArgumentException If context or profilingMode are null, or value is negative.
     */
    public BenchmarkResult(final BenchmarkContext context, final ProfilingMode profilingMode, final double value)
    {
        if (context == null)
        {
            throw new IllegalArgumentException("Benchmark context must not be null.");
        }
        if (profilingMode == null)
        {
            throw new IllegalArgumentException("Profiling mode must not be null.");
        }
        if (value < 0)
        {
            throw new IllegalArgumentException("Benchmark value must not be negative.");
        }
        this.context = context;
        this.profilingMode = profilingMode;
        this.value = value;
    }

    /**
     * Gets the benchmark context.
     *
     * @return The benchmark context object.
     */
    public BenchmarkContext getContext()
    {
        return context;
    }

    /**
     * Gets the profiling mode.
     *
     * @return The profiling mode.
     */
    public ProfilingMode getProfilingMode()
    {
        return profilingMode;
    }

    /**
     * Gets the benchmark metric value.
     *
     * @return The benchmark metric value.
     */
    public double getValue()
    {
        return value;
    }

    /**
     * Returns a string representation of the BenchmarkResult object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString()
    {
        return String.format("{context=%s, profilingMode=%s, value=%.2f}", context, profilingMode, value);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj The object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }

        BenchmarkResult that = (BenchmarkResult) obj;

        return context.equals(that.getContext())
                && profilingMode == that.getProfilingMode()
                && Double.compare(value, that.getValue()) == 0;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(context.hashCode(), profilingMode, value);
    }
}