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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;

// Unit tests for the BenchmarkMetric class.
class BenchmarkMetricTest
{

    @Test
    void constructor_validArguments_createsBenchmarkMetric()
    {
        ProfilingMode mode = ProfilingMode.EXECUTION_TIME;
        double metric = 10.5;

        BenchmarkMetric benchmarkMetric = new BenchmarkMetric(mode, metric);

        assertEquals(mode, benchmarkMetric.getProfilingMode(), "Profiling mode should match");
        assertEquals(metric, benchmarkMetric.getValue(), "Metric value should match");
    }

    @Test
    void constructor_nullProfilingMode_throwsIllegalArgumentException()
    {
        double metric = 10.5;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new BenchmarkMetric(null, metric),
                "Should throw IllegalArgumentException for null profiling mode");

        assertEquals("Profiling mode must not be null.", exception.getMessage(), "Exception message should match");
    }

    @Test
    void constructor_negativeMetric_throwsIllegalArgumentException()
    {
        ProfilingMode mode = ProfilingMode.EXECUTION_TIME;
        double metric = -1.0;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new BenchmarkMetric(mode, -1.0),
                "Should throw IllegalArgumentException for negative metric");
    }

    @Test
    void equals_sameObject_returnsTrue()
    {
        ProfilingMode mode = ProfilingMode.EXECUTION_TIME;
        double metric = 10.5;
        BenchmarkMetric benchmarkMetric = new BenchmarkMetric(mode, metric);

        assertEquals(benchmarkMetric, benchmarkMetric, "Same object should be equal");
    }

    @Test
    void equals_equalObjects_returnsTrue()
    {
        ProfilingMode mode = ProfilingMode.EXECUTION_TIME;
        double metric = 10.5;
        BenchmarkMetric benchmarkMetric1 = new BenchmarkMetric(mode, metric);
        BenchmarkMetric benchmarkMetric2 = new BenchmarkMetric(mode, metric);

        assertEquals(benchmarkMetric1, benchmarkMetric2, "Equal objects should be equal");
    }

    @Test
    void equals_differentProfilingMode_returnsFalse()
    {
        double metric = 10.5;
        BenchmarkMetric benchmarkMetric1 = new BenchmarkMetric(ProfilingMode.EXECUTION_TIME, metric);
        BenchmarkMetric benchmarkMetric2 = new BenchmarkMetric(ProfilingMode.MEMORY_USAGE, metric);

        assertNotEquals(benchmarkMetric1, benchmarkMetric2, "Objects with different profiling mode should not be equal");
    }

    @Test
    void equals_differentMetric_returnsFalse()
    {
        ProfilingMode mode = ProfilingMode.EXECUTION_TIME;
        BenchmarkMetric benchmarkMetric1 = new BenchmarkMetric(mode, 10.5);
        BenchmarkMetric benchmarkMetric2 = new BenchmarkMetric(mode, 20.0);

        assertNotEquals(benchmarkMetric1, benchmarkMetric2, "Objects with different metric should not be equal");
    }

    @Test
    void equals_differentClass_returnsFalse()
    {
        ProfilingMode mode = ProfilingMode.EXECUTION_TIME;
        double metric = 10.5;
        BenchmarkMetric benchmarkMetric = new BenchmarkMetric(mode, metric);

        assertNotEquals(benchmarkMetric, new Object(), "Objects of different classes should not be equal");
    }

    @Test
    void hashCode_equalObjects_sameHashCode()
    {
        ProfilingMode mode = ProfilingMode.EXECUTION_TIME;
        double metric = 10.5;
        BenchmarkMetric benchmarkMetric1 = new BenchmarkMetric(mode, metric);
        BenchmarkMetric benchmarkMetric2 = new BenchmarkMetric(mode, metric);

        assertEquals(benchmarkMetric1.hashCode(), benchmarkMetric2.hashCode(), "Equal objects should have same hash code");
    }

    @Test
    void toString_validObject_returnsStringWithRelevantInfo()
    {
        BenchmarkMetric benchmarkMetric = new BenchmarkMetric(ProfilingMode.EXECUTION_TIME, 32.1);

        assertTrue(benchmarkMetric.toString().contains(ProfilingMode.EXECUTION_TIME.toString()), "toString() should contain the profiling mode.");
        assertTrue(benchmarkMetric.toString().contains("32.1"), "toString() should contain the value.");
    }

    @ParameterizedTest
    @EnumSource(ProfilingMode.class)
    void constructor_validProfilingModes(ProfilingMode mode)
    {
        double metric = 1.0;

        BenchmarkMetric benchmarkMetric = new BenchmarkMetric(mode, metric);

        assertEquals(mode, benchmarkMetric.getProfilingMode(), "Profiling mode should match");
    }
}
