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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.rlacher.sortbench.benchmark.data.BenchmarkData;
import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;

// Unit tests for the AggregatedResult class.
class AggregatedResultTest
{
    private BenchmarkContext context;
    private ProfilingMode profilingMode;
    private double aggregate;
    private int iterations;
    private AggregatedResult aggregatedResult;

    @BeforeEach
    void setUp()
    {
        context = new BenchmarkContext(BenchmarkData.DataType.RANDOM, 10, "BubbleSort");
        profilingMode = ProfilingMode.EXECUTION_TIME;
        aggregate = 10.5;
        iterations = 7;
        aggregatedResult = new AggregatedResult(context, profilingMode, aggregate, iterations);
    }

    @Test
    void constructor_validArguments_aggregatedResultCreated()
    {
        assertEquals(context, aggregatedResult.getContext());
        assertEquals(profilingMode, aggregatedResult.getProfilingMode());
        assertEquals(aggregate, aggregatedResult.getAggregate());
        assertEquals(iterations, aggregatedResult.getIterations());
    }

    @Test
    void constructor_nullContext_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new AggregatedResult(null, profilingMode, aggregate, iterations));
    }

    @Test
    void constructor_nullProfilingMode_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new AggregatedResult(context, null, aggregate, iterations));
    }

    @Test
    void constructor_negativeAggregate_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new AggregatedResult(context, profilingMode, -1.0, iterations));
    }

    @Test
    void constructor_negativeIterations_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new AggregatedResult(context, profilingMode, aggregate, -1));
    }

    @Test
    void compareTo_nullArgument_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> aggregatedResult.compareTo(null), "Comparing to null should throw IllegalArgumentException");
    }

    @Test
    void equals_sameInstance_returnsTrue()
    {
        assertTrue(() -> aggregatedResult.equals(aggregatedResult), "equals() should return true for the same object instance.");
    }

    @Test
    void equals_equivalentInstance_returnsTrue()
    {   
        AggregatedResult equivalentResult = new AggregatedResult(context, profilingMode, aggregate, iterations);
        assertTrue(aggregatedResult.equals(equivalentResult), "equals() should return true for an equivalent object instance.");
    }

    @Test
    void equals_differentContexts_returnsFalse()
    {
        BenchmarkContext differentContext = new BenchmarkContext(BenchmarkData.DataType.SORTED, 10, "BubbleSort");
        AggregatedResult result2 = new AggregatedResult(differentContext, profilingMode, aggregate, iterations);
        assertNotEquals(aggregatedResult, result2);
    }

    @Test
    void equals_differentProfilingModes_returnsFalse()
    {
        AggregatedResult result2 = new AggregatedResult(context, ProfilingMode.MEMORY_USAGE, aggregate, iterations);
        assertNotEquals(aggregatedResult, result2);
    }

    @Test
    void equals_differentAggregates_returnsFalse()
    {
        AggregatedResult result2 = new AggregatedResult(context, profilingMode, 11.0, iterations);
        assertNotEquals(aggregatedResult, result2);
    }

    @Test
    void equals_differentIterations_returnsFalse()
    {
        AggregatedResult result2 = new AggregatedResult(context, profilingMode, aggregate, 6);
        assertNotEquals(aggregatedResult, result2);
    }

    @Test
    void equals_nullObject_returnsFalse()
    {
        assertNotEquals(aggregatedResult, null);
    }

    @Test
    void equals_differentClassObject_returnsFalse()
    {
        assertNotEquals(aggregatedResult, new Object());
    }

    @Test
    void hashCode_equalObjects_returnsSameHashCode()
    {
        AggregatedResult result2 = new AggregatedResult(context, profilingMode, aggregate, iterations);
        assertEquals(aggregatedResult.hashCode(), result2.hashCode());
    }

    @Test
    void toString_validAggregatedResult_stringContainsFields()
    {
        String aggregatedResultString = aggregatedResult.toString();

        assertTrue(aggregatedResultString.contains(context.toString()), "toString() should contain context.toString().");
        assertTrue(aggregatedResultString.contains(profilingMode.toString()), "toString() should contain the profiling mode.");
        assertTrue(aggregatedResultString.contains("10.5"), "toString() should contain the value.");
        assertTrue(aggregatedResultString.contains("7"), "toString() should contain the iterations.");
    }
}
