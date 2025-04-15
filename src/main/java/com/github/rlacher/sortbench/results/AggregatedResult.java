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

import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;

/**
 * Represents the aggregated result of a benchmark run, derived from multiple {@link BenchmarkResult} instances.
 *
 * This class encapsulates the aggregated benchmark result produced by the {@link ResultAggregator},
 * associating it with a specific {@link BenchmarkContext} and {@link ProfilingMode}.
 */
public final class AggregatedResult
{
    /** The benchmark context that defines the contextual parameters of the aggregated result. */
    private final BenchmarkContext context;

    /** The profiling mode used for this aggregated result. */
    private final ProfilingMode profilingMode;

    /** The aggregated benchmark value, corresponding to the {@link ProfilingMode}. */
    private final double aggregate;

    /** The number of iterations used to derive the aggregate value. */
    private final int iterations;

    /**
     * Constructs a new AggregatedResult object.
     *
     * @param context The benchmark context. Must not be null.
     * @param profilingMode The profiling mode. Must not be null.
     * @param aggregate The aggregated metric value. Must not be negative.
     * @param iterations The number of iterations. Must not be negative.
     * @throws IllegalArgumentException If context or profilingMode are null, or aggregate or iterations are negative.
     */
    public AggregatedResult(final BenchmarkContext context, final ProfilingMode profilingMode, final double aggregate, final int iterations)
    {
        if (context == null)
        {
            throw new IllegalArgumentException("Benchmark context must not be null.");
        }
        if (profilingMode == null)
        {
            throw new IllegalArgumentException("Profiling mode must not be null.");
        }
        if (aggregate < 0)
        {
            throw new IllegalArgumentException("Aggregate value must not be negative.");
        }
        if (iterations < 0)
        {
            throw new IllegalArgumentException("Iterations must not be negative.");
        }

        this.context = context;
        this.profilingMode = profilingMode;
        this.aggregate = aggregate;
        this.iterations = iterations;
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
     * Gets the aggregated metric value.
     *
     * @return The aggregate value.
     */
    public double getAggregate()
    {
        return aggregate;
    }

    /**
     * Gets the number of iterations.
     *
     * @return The number of iterations.
     */
    public int getIterations()
    {
        return iterations;
    }

    /**
     * Returns a string representation of the AggregatedResult object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString()
    {
        return String.format("{context=%s, profilingMode=%s, aggregate=%.2f, iterations=%d}", context, profilingMode, aggregate, iterations);
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

        AggregatedResult that = (AggregatedResult) obj;

        return context.equals(that.getContext())
                && profilingMode == that.getProfilingMode()
                && iterations == that.iterations
                && Double.compare(aggregate, that.aggregate) == 0;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(context.hashCode(), profilingMode, aggregate, iterations);
    }
}