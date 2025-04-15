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
        aggregator = new ResultAggregator(ResultAggregator.DEFAULT_FILTER, ResultAggregator.DEFAULT_AGGREGATOR);
        context = new BenchmarkContext(BenchmarkData.DataType.RANDOM, 10, "BubbleSort");
        profilingMode = ProfilingMode.EXECUTION_TIME;
    }

    @Test
    void constructor_nullArguments_throwsIllegalArgumentException()
    {
        assertAll(() -> assertThrows(IllegalArgumentException.class, () -> new ResultAggregator(ResultAggregator.DEFAULT_FILTER, null), "Aggregator must not be null"),
                  () -> assertThrows(IllegalArgumentException.class, () -> new ResultAggregator(null, ResultAggregator.DEFAULT_AGGREGATOR), "Filter must not be null"),
                  () -> assertThrows(IllegalArgumentException.class, () -> new ResultAggregator(null, null), "Filter and aggregator must not be null"));
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
        List<BenchmarkResult> results = List.of(
            new BenchmarkResult(context, profilingMode, 1.0),
            new BenchmarkResult(context, profilingMode, 2.0),
            new BenchmarkResult(context, profilingMode, 3.0)
        );

        AggregatedResult aggregatedResult = aggregator.process(results);

        assertNotNull(aggregatedResult);
        assertEquals(2.0, aggregatedResult.getAggregate(), 1e-9, "Aggregated result does not match expected value");
        assertEquals(3, aggregatedResult.getIterations(), "Iterations count is incorrect");
        assertEquals(context, aggregatedResult.getContext(), "Context is incorrect");
        assertEquals(profilingMode, aggregatedResult.getProfilingMode(), "Profiling mode is incorrect");
    }

    @Test
    void process_singleResultList_returnSameValue()
    {
        List<BenchmarkResult> results = List.of(new BenchmarkResult(context, profilingMode, 7.0));

        AggregatedResult aggregatedResult = aggregator.process(results);

        assertNotNull(aggregatedResult);
        assertEquals(7.0, aggregatedResult.getAggregate(), 1e-9, "Aggregated result does not match expected value");
        assertEquals(1, aggregatedResult.getIterations(), "Iterations count is incorrect");
        assertEquals(context, aggregatedResult.getContext(), "Context is incorrect");
        assertEquals(profilingMode, aggregatedResult.getProfilingMode(), "Profiling mode is incorrect");
    }

    @Test
    void process_inconsistentProfilingModes_throwsIllegalArgumentException()
    {
        List<BenchmarkResult> results = List.of(
            new BenchmarkResult(context, profilingMode, 1.0),
            new BenchmarkResult(context, ProfilingMode.MEMORY_USAGE, 2.0)
        );

        assertThrows(IllegalArgumentException.class, () -> aggregator.process(results), "Results must have the same profiling mode");
    }

    @Test
    void process_inconsistentContexts_throwsIllegalArgumentException()
    {
        BenchmarkContext differentContext = new BenchmarkContext(BenchmarkData.DataType.SORTED, 10, "BubbleSort");
        List<BenchmarkResult> results = List.of(
            new BenchmarkResult(context, profilingMode, 1.0),
            new BenchmarkResult(differentContext, profilingMode, 2.0)
        );

        assertThrows(IllegalArgumentException.class, () -> aggregator.process(results), "Results must have the same context");
    }
}
