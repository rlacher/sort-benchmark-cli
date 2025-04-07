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

package com.github.rlacher.sortbench.reporting;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.github.rlacher.sortbench.benchmark.BenchmarkResult;

/**
 * Aggregates benchmark results based on a filter and an aggregator function.
 */
public class ResultAggregator
{
    /// Default filter that accepts all results.
    public static final Predicate<BenchmarkResult> DEFAULT_FILTER = result -> true;

    /// Default aggregator that calculates the average of the benchmark results.
    public static final Function<List<BenchmarkResult>, Double> DEFAULT_AGGREGATOR = results ->
            results == null || results.isEmpty() ? Double.NaN :
            results.stream().mapToDouble(BenchmarkResult::getValue).average().orElse(Double.NaN);

    /// Filter to select which benchmark results to aggregate.
    private final Predicate<BenchmarkResult> filter;

    /// Aggregator function to compute the final result from the filtered results.
    private final Function<List<BenchmarkResult>, Double> aggregator;

    /**
     * Constructor to create a ResultAggregator passing a filter and an aggregator.
     * @param filter The filter to select benchmark results
     * @param aggregator The aggregator function to compute the final result
     * @throws IllegalArgumentException If the filter or aggregator is null
     */
    public ResultAggregator(Predicate<BenchmarkResult> filter, Function<List<BenchmarkResult>, Double> aggregator)
    {
        if(filter == null)
        {
            throw new IllegalArgumentException("Filter must not be null");
        }
        if(aggregator == null)
        {
            throw new IllegalArgumentException("Aggregator must not be null");
        }

        this.filter = filter;
        this.aggregator = aggregator;
    }

    /**
     * Processes the given list of benchmark results, applying the filter and aggregator.
     * 
     * @param results The list of benchmark results to process
     * @return A new {@link BenchmarkResult} containing the aggregated result
     * @throws IllegalArgumentException If the results list is null or empty
     */
    public BenchmarkResult process(List<BenchmarkResult> results)
    {
        if(results == null)
        {
            throw new IllegalArgumentException("Results list must not be null");
        }

        List<BenchmarkResult> filteredResults = results.stream().filter(filter).toList();

        if(filteredResults.isEmpty())
        {
            throw new IllegalArgumentException("No results match the filter criteria");
        }

        return new BenchmarkResult(filteredResults.getFirst().getProfilingMode(), aggregator.apply(filteredResults));
    }
}
