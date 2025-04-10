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

import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;

// Unit tests for the BenchmarkResult class.
class BenchmarkResultTest
{
    // Tests the constructor of the BenchmarkResult class when the mode is null.
    @Test
    void constructor_whenModeIsNull_thenThrowsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkResult(null, 0));
    }

    // Tests the constructor of the BenchmarkResult class when the value is negative.
    @Test
    void constructor_whenValueIsNegative_thenThrowsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkResult(ProfilingMode.EXECUTION_TIME, -1));
    }

    // Tests the constructor of the BenchmarkResult class with valid parameters.
    @Test
    void constructor_whenParamsAreValid_thenInitFields()
    {
        double value = 42.0;
        BenchmarkResult benchmarkResult = new BenchmarkResult(ProfilingMode.EXECUTION_TIME, value);

        assertEquals(ProfilingMode.EXECUTION_TIME, benchmarkResult.getProfilingMode());
        assertEquals(value, benchmarkResult.getValue());
    }

    // Tests if the BenchmarkResult class is equal to itself.
    @Test
    void equals_givenSameObject_shouldReturnTrue()
    {
        BenchmarkResult result1 = new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 42.0);
        BenchmarkResult result2 = new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 42.0);

        assertEquals(result1, result2, "Objects with same values should be equal");
    }

    // Tests if the BenchmarkResult class is equal to another object of the same type.
    @Test
    void equals_givenDifferentObjects_shouldReturnFalse()
    {
        BenchmarkResult result1 = new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 42.0);
        BenchmarkResult result2 = new BenchmarkResult(ProfilingMode.MEMORY_USAGE, 42.0);

        assertNotEquals(result1, result2, "Objects with different profiling modes should not be equal");
    }

    // Tests if the BenchmarkResult class is equal null.
    @Test
    void equals_whenCalledWithNull_shouldReturnFalse()
    {
        BenchmarkResult result = new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 10.0);

        assertNotEquals(result, null, "Object should not be equal to null");
    }

    // Tests if the BenchmarkResult class is equal to an object of a different type.
    @Test
    void equals_whenCalledWithObjectOfDifferentType_shouldReturnFalse()
    {
        BenchmarkResult result = new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 10.0);
        Object otherObject = new Object();

        assertNotEquals(result, otherObject, "Object should not be equal to an object of a different type");
    }

    // Tests if calling hashCode() twice on the same object returns the same value.
    @Test
    void hashcode_givenSameObject_shouldReturnSameHashcode()
    {
        BenchmarkResult result1 = new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 42.0);
        BenchmarkResult result2 = new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 42.0);

        assertEquals(result1.hashCode(), result2.hashCode(), "Hash codes should be equal for same objects");
    }

    // Tests if the hash codes for two BenchmarkResult objects with the same values are identical.
    @Test
    void hashcode_givenObjectsWithSameValues_shouldReturnSameHashcode()
    {
        BenchmarkResult result1 = new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 42.0);
        BenchmarkResult result2 = new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 42.0);

        assertEquals(result1.hashCode(), result2.hashCode(), "Hash codes should be equal for same values");
    }

    // Tests if the hash codes for two BenchmarkResult objects with different values are different.
    @Test
    void hashcode_givenDifferentObjects_shouldReturnDifferentHashcode()
    {
        BenchmarkResult result1 = new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 42.0);
        BenchmarkResult result2 = new BenchmarkResult(ProfilingMode.MEMORY_USAGE, 42.0);
        BenchmarkResult result3 = new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 43.0);

        assertNotEquals(result1.hashCode(), result2.hashCode(), "Hash codes should be different for different profiling modes");
        assertNotEquals(result1.hashCode(), result3.hashCode(), "Hash codes should be different for different values");
    }

    // Tests the toString() method for returning a string representation of the BenchmarkResult object.
    @Test
    void toString_whenCalled_shouldReturnsStringWithRelevantInfo()
    {
        BenchmarkResult benchmarkResult = new BenchmarkResult(ProfilingMode.EXECUTION_TIME, 32.1);

        assertTrue(benchmarkResult.toString().contains(ProfilingMode.EXECUTION_TIME.toString()), "toString() should contain the profiling mode.");
        assertTrue(benchmarkResult.toString().contains("32.1"), "toString() should contain the value.");
    }
}
