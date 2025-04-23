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

import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkData;

// Unit tests for the BenchmarkResult class.
class BenchmarkResultTest
{
    private BenchmarkContext context;
    private ProfilingMode profilingMode;
    private double value;
    private BenchmarkResult benchmarkResult;

    @BeforeEach
    void setUp()
    {
        context = new BenchmarkContext(BenchmarkData.DataType.RANDOM, 10, "BubbleSort");
        profilingMode = ProfilingMode.EXECUTION_TIME;
        value = 42.0;
        benchmarkResult = new BenchmarkResult(context, profilingMode, value);
    }

    @Test
    void constructor_nullContext_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkResult(null, profilingMode, value));
    }

    @Test
    void constructor_nullProfilingMode_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkResult(context, null, value));
    }

    @Test
    void constructor_whenValueIsNegative_thenThrowsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkResult(context, profilingMode, -1));
    }

    @Test
    void constructor_whenParamsAreValid_thenInitFields()
    {
        assertEquals(context, benchmarkResult.getContext());
        assertEquals(profilingMode, benchmarkResult.getProfilingMode());
        assertEquals(value, benchmarkResult.getValue());
    }

    @Test
    void equals_givenSameObject_shouldReturnTrue()
    {
        BenchmarkResult result2 = new BenchmarkResult(context, profilingMode, value);
        assertEquals(benchmarkResult, result2, "Objects with same values should be equal");
    }

    @Test
    void equals_givenDifferentProfilingModes_shouldReturnFalse()
    {
        BenchmarkResult result2 = new BenchmarkResult(context, ProfilingMode.MEMORY_USAGE, value);
        assertNotEquals(benchmarkResult, result2, "Objects with different profiling modes should not be equal");
    }

    @Test
    void equals_givenDifferentContexts_shouldReturnFalse()
    {
        BenchmarkContext differentContext = new BenchmarkContext(BenchmarkData.DataType.SORTED, 10, "BubbleSort");
        BenchmarkResult result2 = new BenchmarkResult(differentContext, profilingMode, value);
        assertNotEquals(benchmarkResult, result2, "Objects with different contexts should not be equal");
    }

    @Test
    void equals_givenDifferentValues_shouldReturnFalse()
    {
        BenchmarkResult result2 = new BenchmarkResult(context, profilingMode, 43.0);
        assertNotEquals(benchmarkResult, result2, "Objects with different values should not be equal");
    }

    @Test
    void equals_whenCalledWithNull_shouldReturnFalse()
    {
        assertNotEquals(benchmarkResult, null, "Object should not be equal to null");
    }

    @Test
    void equals_whenCalledWithObjectOfDifferentType_shouldReturnFalse()
    {
        Object otherObject = new Object();
        assertNotEquals(benchmarkResult, otherObject, "Object should not be equal to an object of a different type");
    }

    @Test
    void hashCode_givenSameObject_shouldReturnSameHashcode()
    {
        BenchmarkResult result2 = new BenchmarkResult(context, profilingMode, value);
        assertEquals(benchmarkResult.hashCode(), result2.hashCode(), "Hash codes should be equal for same objects");
    }

    @Test
    void hashCode_givenDifferentProfilingModes_shouldReturnDifferentHashcode()
    {
        BenchmarkResult result2 = new BenchmarkResult(context, ProfilingMode.MEMORY_USAGE, value);
        assertNotEquals(benchmarkResult.hashCode(), result2.hashCode(), "Hash codes should be different for different profiling modes");
    }

    @Test
    void hashCode_givenDifferentContexts_shouldReturnDifferentHashcode()
    {
        BenchmarkContext differentContext = new BenchmarkContext(BenchmarkData.DataType.SORTED, 10, "BubbleSort");
        BenchmarkResult result2 = new BenchmarkResult(differentContext, profilingMode, value);
        assertNotEquals(benchmarkResult.hashCode(), result2.hashCode(), "Hash codes should be different for different contexts");
    }

    @Test
    void hashCode_givenDifferentValues_shouldReturnDifferentHashcode()
    {
        BenchmarkResult result2 = new BenchmarkResult(context, profilingMode, 43.0);
        assertNotEquals(benchmarkResult.hashCode(), result2.hashCode(), "Hash codes should be different for different values");
    }

    @Test
    void toString_whenCalled_stringContainsFields()
    {
        assertTrue(benchmarkResult.toString().contains(profilingMode.toString()), "toString should contain the profiling mode.");
        assertTrue(benchmarkResult.toString().contains(String.valueOf(value)), "toString should contain the value.");
        assertTrue(benchmarkResult.toString().contains(context.toString()),"toString should contain context information");
    }
}
