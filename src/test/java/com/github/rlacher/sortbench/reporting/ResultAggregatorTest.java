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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.github.rlacher.sortbench.benchmark.BenchmarkResult;
import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;

/**
 * Unit tests for the {@link ResultAggregator} class.
 */
class ResultAggregatorTest
{
    /**
     * Tests the constructor of the {@link ResultAggregator} class when null arguments are passed.
     */
    @Test
    void constructor_givenNullArguments_shouldThrowIllegalArgumentException()
    {
        assertAll(() -> assertThrows(IllegalArgumentException.class, () -> new ResultAggregator(ResultAggregator.DEFAULT_FILTER, null), "Aggregator must not be null"),
                  () -> assertThrows(IllegalArgumentException.class, () -> new ResultAggregator(null, ResultAggregator.DEFAULT_AGGREGATOR), "Filter must not be null"),
                  () -> assertThrows(IllegalArgumentException.class, () -> new ResultAggregator(null, null), "Filter and aggregator must not be null"));
    }

    /**
     * Tests the process method of the {@link ResultAggregator} class when null results are passed.
     */
    @Test
    void process_givenNullResults_shouldThrowIllegalArgumentException()
    {
        ResultAggregator aggregator = new ResultAggregator(ResultAggregator.DEFAULT_FILTER, ResultAggregator.DEFAULT_AGGREGATOR);
        assertThrows(IllegalArgumentException.class, () -> aggregator.process(null), "Results must not be null");
    }

    /**
     * Tests the process method of the {@link ResultAggregator} class when empty results are passed.
     */
    @Test
    void process_givenEmptyResults_shouldThrowIllegalArgumentException()
    {
        ResultAggregator aggregator = new ResultAggregator(ResultAggregator.DEFAULT_FILTER, ResultAggregator.DEFAULT_AGGREGATOR);
        assertThrows(IllegalArgumentException.class, () -> aggregator.process(List.of()), "Results must not be empty");
    }

    @Test
    void process_givenValidResults_shouldReturnAggregatedResult()
    {
        ResultAggregator aggregator = new ResultAggregator(ResultAggregator.DEFAULT_FILTER, ResultAggregator.DEFAULT_AGGREGATOR);
        List<BenchmarkResult> results = List.of(new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 1.0),
            new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 2.0),
            new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 3.0));

        final double expected = 2.0;
        BenchmarkResult aggregatedResult = aggregator.process(results);

        assertNotNull(aggregatedResult);
        assertEquals(expected, aggregatedResult.getValue(), 1e-9, "Aggregated result does not match expected value");
    }

    @Test
    void process_givenSingleResultList_shouldReturnSameValue()
    {
        ResultAggregator aggregator = new ResultAggregator(ResultAggregator.DEFAULT_FILTER, ResultAggregator.DEFAULT_AGGREGATOR);
        List<BenchmarkResult> results = List.of(new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 7.0));

        final double expected = 7.0;
        BenchmarkResult aggregatedResult = aggregator.process(results);

        assertNotNull(aggregatedResult);
        assertEquals(expected, aggregatedResult.getValue(), 1e-9, "Aggregated result does not match expected value");
    }

    @Test
    void process_givenValidResults_shouldReturnMatchingProfilingMode()
    {
        ResultAggregator aggregator = new ResultAggregator(ResultAggregator.DEFAULT_FILTER, ResultAggregator.DEFAULT_AGGREGATOR);
        List<BenchmarkResult> results = List.of(new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 1.0),
            new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 2.0),
            new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 3.0));

        ProfilingMode expectedMode = ProfilingMode.EXECUTION_TIME;
        BenchmarkResult aggregatedResult = aggregator.process(results);

        assertNotNull(aggregatedResult);
        assertEquals(expectedMode, aggregatedResult.getProfilingMode(), "Profiling mode does not match expected value");
    }

}
