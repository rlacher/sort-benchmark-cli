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

import java.util.Objects;

import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;

/**
 * A POJO class representing the result of a benchmark corresponding to the {@link ProfilingMode}.
 */
public final class BenchmarkResult
{
    /**
     * The profiling mode used during the benchmark.
     */
    private final ProfilingMode profilingMode;

    /**
     * The benchmark metric value in correspondence to the {@link ProfilingMode}.
     */
    private final double value;


    /**
     * Constructs a new BenchmarkResult object.
     *
     * @param mode The profiling mode used during the benchmark.
     * @param value The value of the benchmark metric.
     * @throws IllegalArgumentException If either mode or value are negative.
     */
    public BenchmarkResult(final ProfilingMode mode, final double value)
    {
        if(mode == null)
        {
            throw new IllegalArgumentException("Profiling mode must not be null.");
        }

        if(value < 0)
        {
            throw new IllegalArgumentException("Value must not be negative.");
        }

        this.profilingMode = mode;
        this.value = value;
    }
    
    /**
     * Returns the profiling mode used during the benchmark.
     * 
     * @return The profiling mode used in the benchmark.
     */
    public ProfilingMode getProfilingMode()
    {
        return profilingMode;
    }

    /**
     * Returns the metric value representing the benchmark result.
     *
     * @return The benchmark metric value.
     */
    public double getValue()
    {
        return value;
    }

    /**
     * Checks if two BenchmarkResult objects are equal based on their profiling mode and value.
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
        return (Double.compare(this.value, that.value) == 0) && (profilingMode == that.profilingMode);
    }

    /**
     * Generates a hash code for the BenchmarkResult object based on its profiling mode and value.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(value, profilingMode);
    }

    /**
     * Turns the BenchmarkResult into a string representation.
     *
     * @return A string representation of the benchmark result.
     */
    @Override
    public String toString()
    {
        return String.format("{mode=%s, value=%.2f}", profilingMode, value);
    }   
}
