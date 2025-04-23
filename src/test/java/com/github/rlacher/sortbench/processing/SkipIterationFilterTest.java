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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.rlacher.sortbench.results.BenchmarkResult;

// Unit tests for the SkipIterationFilter class.
public class SkipIterationFilterTest
{
    private BenchmarkResult mockBenchmarkResult;

    @BeforeEach
    void setUp()
    {
        mockBenchmarkResult = mock(BenchmarkResult.class);
    }

    @Test
    void constructor_negativeIterationsToSkip_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new SkipIterationFilter(-1), "Constructor with negative iterationsToSkip should throw IllegalArgumentException");
    }

    @Test
    void test_nullBenchmarkResult_throwsIllegalArgumentException()
    {
        final SkipIterationFilter skipFilter = new SkipIterationFilter(2);
        assertThrows(IllegalArgumentException.class, () -> skipFilter.test(null), "test() with null BenchmarkResult should throw IllegalArgumentException");
    }

    @Test
    void test_fewerIterationsThanSkipCount_returnsFalse()
    {
        final SkipIterationFilter skipFilter = new SkipIterationFilter(3);
        assertFalse(skipFilter.test(mockBenchmarkResult), "test() should return false for the first iteration when iterationsToSkip is 3");
        assertFalse(skipFilter.test(mockBenchmarkResult), "test() should return false for the second iteration when iterationsToSkip is 3");
    }

    @Test
    void test_iterationsEqualToSkipCount_returnsFalse()
    {
        final SkipIterationFilter skipFilter = new SkipIterationFilter(2);
        skipFilter.test(mockBenchmarkResult);
        assertFalse(skipFilter.test(mockBenchmarkResult), "test() should return false when the number of iterations equals the iterationsToSkip");
    }

    @Test
    void test_moreIterationsThanSkipCount_returnsTrue()
    {
        final SkipIterationFilter skipFilter = new SkipIterationFilter(1);
        skipFilter.test(mockBenchmarkResult);
        assertTrue(skipFilter.test(mockBenchmarkResult), "test() should return true when the number of iterations exceeds the iterationsToSkip");
    }
}
