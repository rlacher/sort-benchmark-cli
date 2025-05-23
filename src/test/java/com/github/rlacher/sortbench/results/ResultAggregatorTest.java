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

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkData;

// Unit tests for the ResultAggregator class.
class ResultAggregatorTest
{
    private ResultAggregator aggregator;
    private BenchmarkContext context;
    private ProfilingMode profilingMode;

    @BeforeEach
    void setUp()
    {
        aggregator = new ResultAggregator(ResultAggregator.DEFAULT_FILTER_SUPPLIER, ResultAggregator.DEFAULT_AGGREGATOR_SUPPLIER);
        context = new BenchmarkContext(BenchmarkData.DataType.RANDOM, 10, "BubbleSort");
        profilingMode = ProfilingMode.EXECUTION_TIME;
    }

    @Test
    void constructor_nullArguments_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new ResultAggregator(null, null),
                    "Constructor should throw IllegalArgumentException when filterSupplier and aggregatorSupplier are null");
    }

   @Test
    void constructor_nullFilterSupplier_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new ResultAggregator(null, ResultAggregator.DEFAULT_AGGREGATOR_SUPPLIER),
                "Constructor should throw IllegalArgumentException when filterSupplier is null");
    }

    @Test
    void constructor_nullAggregatorSupplier_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new ResultAggregator(ResultAggregator.DEFAULT_FILTER_SUPPLIER, null),
                "Constructor should throw IllegalArgumentException when aggregatorSupplier is null");
    }

    @Test
    void constructor_supplierReturnsNullFilter_throwsIllegalStateException()
    {
        final Supplier<Predicate<BenchmarkResult>> supplierReturnsNullFilter = () -> null;
        assertThrows(IllegalStateException.class, () -> new ResultAggregator(supplierReturnsNullFilter, ResultAggregator.DEFAULT_AGGREGATOR_SUPPLIER),
                "Constructor should throw IllegalStateException when filter supplier returns null");
    }

    @Test
    void constructor_supplierReturnsNullAggregator_throwsIllegalStateException()
    {
        final Supplier<Function<List<BenchmarkResult>, Double>> supplierReturnsNullAggregator = () -> null;
        assertThrows(IllegalStateException.class, () -> new ResultAggregator(ResultAggregator.DEFAULT_FILTER_SUPPLIER, supplierReturnsNullAggregator),
                "Constructor should throw IllegalStateException when aggregator supplier returns null");
    }

    @Test
    void process_nullResults_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> aggregator.process(null), "Results must not be null");
    }

    @Test
    void process_emptyResults_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> aggregator.process(List.of()), "Results must not be empty");
    }

    @Test
    void process_validResults_returnsAggregatedResult()
    {
        final List<BenchmarkResult> results = List.of(
            new BenchmarkResult(context, profilingMode, 1.0),
            new BenchmarkResult(context, profilingMode, 2.0),
            new BenchmarkResult(context, profilingMode, 3.0)
        );

        List<AggregatedResult> aggregatedResults = aggregator.process(results);

        assertNotNull(aggregatedResults);
        assertEquals(1, aggregatedResults.size());

        AggregatedResult aggregatedResult = aggregatedResults.getFirst();

        assertNotNull(aggregatedResult);
        assertEquals(2.0, aggregatedResult.getAggregate(), 1e-9, "Aggregated result does not match expected value");
        assertEquals(3, aggregatedResult.getIterations(), "Iterations count is incorrect");
        assertEquals(context, aggregatedResult.getContext(), "Context is incorrect");
        assertEquals(profilingMode, aggregatedResult.getProfilingMode(), "Profiling mode is incorrect");
    }

    @Test
    void process_singleResultList_returnSameValue()
    {
        final List<BenchmarkResult> results = List.of(new BenchmarkResult(context, profilingMode, 7.0));

        List<AggregatedResult> aggregatedResults = aggregator.process(results);

        assertNotNull(aggregatedResults);
        assertEquals(1, aggregatedResults.size());

        AggregatedResult aggregatedResult = aggregatedResults.getFirst();

        assertEquals(7.0, aggregatedResult.getAggregate(), 1e-9, "Aggregated result does not match expected value");
        assertEquals(1, aggregatedResult.getIterations(), "Iterations count is incorrect");
        assertEquals(context, aggregatedResult.getContext(), "Context is incorrect");
        assertEquals(profilingMode, aggregatedResult.getProfilingMode(), "Profiling mode is incorrect");
    }

    @Test
    void process_inconsistentProfilingModes_throwsIllegalArgumentException()
    {
        final List<BenchmarkResult> results = List.of(
            new BenchmarkResult(context, profilingMode, 1.0),
            new BenchmarkResult(context, ProfilingMode.MEMORY_USAGE, 2.0)
        );

        assertThrows(IllegalArgumentException.class, () -> aggregator.process(results), "Results must have the same profiling mode");
    }

    @Test
    void process_twoContexts_returnsTwoAggregatedResults()
    {
        final BenchmarkContext differentContext = new BenchmarkContext(BenchmarkData.DataType.SORTED, 10, "BubbleSort");
        final List<BenchmarkResult> results = List.of(
            new BenchmarkResult(context, profilingMode, 1.0),
            new BenchmarkResult(differentContext, profilingMode, 2.0)
        );

        List<AggregatedResult> aggregatedResults = aggregator.process(results);

        assertNotNull(aggregatedResults, "Aggregated results must not be null");
        assertEquals(2, aggregatedResults.size(), "process must return a list of two aggregated results.");
        
        // Check that the contexts are present, regardless of order
        assertTrue(aggregatedResults.stream().anyMatch(result -> result.getContext().equals(context)),
                "Aggregated results should contain a result with the first context");
        assertTrue(aggregatedResults.stream().anyMatch(result -> result.getContext().equals(differentContext)),
                "Aggregated results should contain a result with the second context");
    }

    @Test
    void process_excludeAllFilter_throwsIllegalArgumentException()
    {
        final Supplier<Predicate<BenchmarkResult>> excludeAllFilterSupplier = () -> result -> false;
        aggregator = new ResultAggregator(excludeAllFilterSupplier, ResultAggregator.DEFAULT_AGGREGATOR_SUPPLIER);
        final List<BenchmarkResult> results = List.of(
            new BenchmarkResult(context, profilingMode, 1.0),
            new BenchmarkResult(context, profilingMode, 2.0)
        );

        assertThrows(IllegalArgumentException.class, () -> aggregator.process(results), "Processing results with a filter that excludes all should throw an IllegalArgumentException");
    }
}
