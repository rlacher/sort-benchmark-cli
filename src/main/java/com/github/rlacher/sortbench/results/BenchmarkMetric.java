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

import com.github.rlacher.sortbench.benchmark.Benchmarker;
import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
 
/**
 * Represents a raw profiling measurement from a single benchmark iteration.
 *
 * <p>The meaning of this metric value is determined by the {@link ProfilingMode} used.
 * This object is the direct result of a sorting operation,
 * instantiated by the {@link Benchmarker}.</p>
 */
public final class BenchmarkMetric
{
    /**
     * The profiling mode used during the benchmark iteration.
     */
    private final ProfilingMode profilingMode;

    /**
     * The profiling metric in correspondence to the {@link ProfilingMode}.
     */
    private final double value;


    /**
     * Constructs a new {@link BenchmarkMetric} object.
     *
     * @param mode The profiling mode used during the benchmark iteration.
     * @param value The metric of the benchmark iteration.
     * @throws IllegalArgumentException If either mode or value are negative.
     */
    public BenchmarkMetric(final ProfilingMode mode, final double value)
    {
        if(mode == null)
        {
            throw new IllegalArgumentException("Profiling mode must not be null.");
        }

        if(value < 0)
        {
            throw new IllegalArgumentException("Metric value must not be negative.");
        }

        this.profilingMode = mode;
        this.value = value;
    }
    
    /**
     * Returns the profiling mode used during the benchmark iteration.
     * 
     * @return The profiling mode used in the benchmark.
     */
    public ProfilingMode getProfilingMode()
    {
        return profilingMode;
    }

    /**
     * Returns a measurement value representing the profiling metric of a benchmark iteration.
     *
     * @return The benchmark metric value.
     */
    public double getValue()
    {
        return value;
    }

    /**
     * Checks if two BenchmarkMetric objects are equal based on their profiling mode and metric value.
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

        BenchmarkMetric that = (BenchmarkMetric) obj;
        return (Double.compare(this.value, that.value) == 0) && (profilingMode == that.profilingMode);
    }

    /**
     * Generates a hash code for the BenchmarkMetric object based on its profiling mode and value.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(value, profilingMode);
    }

    /**
     * Turns the BenchmarkMetric into a string representation.
     *
     * @return A string representation of the benchmark metric.
     */
    @Override
    public String toString()
    {
        return String.format("{profilingMode=%s, value=%.2f}", profilingMode, value);
    }
}
