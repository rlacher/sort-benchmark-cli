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

import java.util.List;
import java.util.function.Function;
import java.util.stream.DoubleStream;

import com.github.rlacher.sortbench.results.BenchmarkResult;

/**
 * Aggregates a list of {@link BenchmarkResult} instances to calculate the median value.
 *
 * <p>This aggregator aims to provide a more robust and representative summary of
 * benchmark results by using the median, which is less susceptible to outliers
 * caused by factors like garbage collection or JVM optimisations.</p>
 */
public class MedianAggregator implements Function<List<BenchmarkResult>, Double>
{
    /**
     * Constructs a default instance of {@link MedianAggregator}.
     *
     * This constructor performs no custom initialisation.
     */
    public MedianAggregator() {}

    /**
     * Applies the aggregation logic to calculate the median of the benchmark result values.
     *
     * @param results The list of {@link BenchmarkResult} instances to aggregate. Must not be {@code null}.
     * @return The median value of the benchmark results.
     * @throws IllegalArgumentException If the provided list of benchmark results is {@code null}.
     * @throws IllegalArgumentException If the stream of values derived from the results is empty.
     */
    @Override
    public Double apply(List<BenchmarkResult> results)
    {
        if(results == null)
        {
            throw new IllegalArgumentException("Benchmark results must not be null");
        }

        return median(results.stream().mapToDouble(BenchmarkResult::getValue));
    }

    /**
     * Calculates the median of a stream of double values.
     *
     * @param stream The {@link DoubleStream} of values to calculate the median from.
     * @return The median value of the stream.
     * @throws IllegalArgumentException If the provided stream is empty.
     */
    private static Double median(DoubleStream stream)
    {
        List<Double> sortedList = stream.boxed().sorted().toList();
        final int middle = sortedList.size() / 2;

        final int size = sortedList.size();

        if(size == 0)
        {
            throw new IllegalArgumentException("Stream must have at least one element");
        }

        if(size % 2 == 0)
        {
            return (sortedList.get(middle - 1) + sortedList.get(middle)) / 2.0;
        }
        else
        {
            return sortedList.get(middle);
        }
    }
}
