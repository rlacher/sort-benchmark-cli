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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;

/**
 * Aggregates benchmark results based on a filter and an aggregator function.
 */
public class ResultAggregator
{
    /** Default {@link Supplier} providing a filter that accepts all results. */
    public static final Supplier<Predicate<BenchmarkResult>> DEFAULT_FILTER_SUPPLIER = () -> result -> true;

    /** Default {@link Supplier} to an aggregator that calculates the average of the benchmark results. */
    public static final Supplier<Function<List<BenchmarkResult>, Double>> DEFAULT_AGGREGATOR_SUPPLIER = () -> results ->
            results.stream().mapToDouble(BenchmarkResult::getValue).average().orElse(Double.NaN);

    /** Filter supplier to select which benchmark results to aggregate. */
    private final Supplier<Predicate<BenchmarkResult>> filterSupplier;

    /** Aggregator function supplier to compute the final result from the filtered results. */
    private final Supplier<Function<List<BenchmarkResult>, Double>> aggregatorSupplier;

    /**
     * Constructs a {@code ResultAggregator} with the provided suppliers for the filter and aggregator.
     *
     * @param filterSupplier    Supplier of the {@link Predicate} to filter results.
     * @param aggregatorSupplier Supplier of the {@link Function} to aggregate results.
     * @throws IllegalArgumentException If either {@code filterSupplier} or {@code aggregatorSupplier} is {@code null}.
     * @throws IllegalStateException If the filter and aggregator supplier provide a {@code null} {@link Predicate} or {@link Function}, respectively.
     */
    public ResultAggregator(Supplier<Predicate<BenchmarkResult>> filterSupplier,
                            Supplier<Function<List<BenchmarkResult>, Double>> aggregatorSupplier)
    {
        if(filterSupplier == null)
        {
            throw new IllegalArgumentException("Filter supplier cannot be null.");
        }

        final Predicate<BenchmarkResult> filter = filterSupplier.get();
        if (filter == null)
        {
            throw new IllegalStateException("Filter provided by the supplier cannot be null.");
        }

        if(aggregatorSupplier == null)
        {
            throw new IllegalArgumentException("Aggregator supplier cannot be null.");
        }

        final Function<List<BenchmarkResult>, Double> aggregator = aggregatorSupplier.get();
        if (aggregator == null)
        {
            throw new IllegalStateException("Aggregator provided by the supplier cannot be null.");
        }

        this.filterSupplier = filterSupplier;
        this.aggregatorSupplier = aggregatorSupplier;
    }

    /**
     * Processes the given list of benchmark results, applying the filter from
     * the {@code filterSupplier} and aggregator from the {@code aggregatorSupplier}.
     * 
     * @param results The list of benchmark results to process. Must neither be {@code null} nor empty.
     * @return A list of {@link AggregatedResult} containing the aggregated results.
     * @throws IllegalArgumentException If the results list is {@code null} or empty, or if any two results within the list
     * have differing {@link BenchmarkContext} or {@link ProfilingMode} values.
     */
    public List<AggregatedResult> process(List<BenchmarkResult> results)
    {
        if(results == null)
        {
            throw new IllegalArgumentException("Results list must not be null");
        }

        if(results.isEmpty())
        {
            throw new IllegalArgumentException("Results must not be empty");
        }

        final ProfilingMode profilingMode = results.get(0).getProfilingMode();

        if(results.stream().anyMatch(result -> result.getProfilingMode() != profilingMode))
        {
            throw new IllegalArgumentException("All results must have the same profiling mode");
        }

        final Map<String, List<BenchmarkResult>> resultMap = results.stream()
            .collect(Collectors.groupingBy(result -> result.getContext().toString()));

        List<AggregatedResult> aggregatedResults = new ArrayList<>();
        for(var resultsByContext: resultMap.values())
        {
            AggregatedResult aggregatedResultByContext = processResultsByContext(resultsByContext);
            aggregatedResults.add(aggregatedResultByContext);
        }

        Collections.sort(aggregatedResults);
        return aggregatedResults;
    }

    /**
     * Processes benchmark results for a single context.
     * 
     * @param resultsByContext The results to process.
     * @return The aggregated result.
     * @throws IllegalArgumentException If the results within the list have different contexts, or if no results match the internal filter.
     * @throws NullPointerException If {@code resultsByContext} is {@code null}. This exception is unexpected
     * as the calling {@link ResultAggregator#process} method ensures a non-null list.
     */
    protected AggregatedResult processResultsByContext(final List<BenchmarkResult> resultsByContext)
    {
        final BenchmarkContext context = resultsByContext.get(0).getContext();

        if(resultsByContext.stream().anyMatch(result -> !Objects.equals(result.getContext(), context)))
        {
            throw new IllegalArgumentException("All results in group must have the same context");
        }

        final Predicate<BenchmarkResult> filter = filterSupplier.get();
        final List<BenchmarkResult> filteredResults = resultsByContext.stream().filter(filter).toList();

        if(filteredResults.isEmpty())
        {
            throw new IllegalArgumentException("No results match the filter criteria");
        }

        // Use original list size for correct iteration count
        final int iterations = resultsByContext.size();
        final ProfilingMode profilingMode = resultsByContext.getFirst().getProfilingMode();

        final Function<List<BenchmarkResult>, Double> aggregator = aggregatorSupplier.get();

        return new AggregatedResult(context, profilingMode, aggregator.apply(filteredResults), iterations);
    }
}
