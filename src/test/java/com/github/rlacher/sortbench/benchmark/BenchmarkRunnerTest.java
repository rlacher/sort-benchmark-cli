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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.github.rlacher.sortbench.sorter.Sorter;
import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkData;
import com.github.rlacher.sortbench.results.BenchmarkMetric;
import com.github.rlacher.sortbench.results.BenchmarkResult;

// Unit tests for the BenchmarkRunner class.
class BenchmarkRunnerTest {

    private Sorter mockSorter;
    private BenchmarkRunner benchmarkRunner;
    private Map<String, Object> validConfig;

    @BeforeEach
    void setUp()
    {
        mockSorter = Mockito.mock(Sorter.class);
        benchmarkRunner = new BenchmarkRunner(mockSorter);
        validConfig = new HashMap<>();
        validConfig.put("input_sizes", Arrays.asList(10));
        validConfig.put("iterations", 1);
        validConfig.put("strategies", Arrays.asList("MergeSort"));
        validConfig.put("profiling_mode", ProfilingMode.EXECUTION_TIME);
        validConfig.put("data_type", BenchmarkData.DataType.RANDOM.toString());
    }

   @Test
    void constructor_nullSorter_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkRunner(null), "Constructor should throw IllegalArgumentException when sorter is null");
    }

    @Test
    void run_nullConfig_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(null), "run should throw IllegalArgumentException when config is null");
    }

    @Test
    void run_invalidInputSizes_throwsIllegalArgumentException()
    {
        validConfig.put("input_sizes", Arrays.asList("invalid"));
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(validConfig), "run should throw IllegalArgumentException when input_sizes is invalid");
    }

    @Test
    void run_invalidStrategies_throwsIllegalArgumentException()
    {
        validConfig.put("strategies", Arrays.asList(123));
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(validConfig), "run should throw IllegalArgumentException when strategies is invalid");
    }

    @Test
    void run_undefinedDataType_throwsIllegalArgumentException()
    {
        validConfig.remove("data_type");
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(validConfig), "run should throw IllegalArgumentException when data type is undefined");
    }

    @Test
    void run_nonStringDataType_throwsIllegalArgumentException()
    {
        validConfig.replace("data_type", 3);
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(validConfig), "run should throw IllegalArgumentException when data type is not of type String");
    }

    @Test
    void run_validConfig_returnsBenchmarkResults()
    {
        BenchmarkMetric mockMetric = mock(BenchmarkMetric.class);
        when(mockSorter.sort(any(int[].class))).thenReturn(mockMetric);
        when(mockMetric.getProfilingMode()).thenReturn((ProfilingMode)validConfig.get("profiling_mode"));

        List<BenchmarkResult> results = benchmarkRunner.run(validConfig);
        assertNotNull(results, "run should return a non-null list of BenchmarkResults");
        assertFalse(results.isEmpty(), "run should return a non-empty list of BenchmarkResults");
    }

    @Test
    void run_unknownStrategy_throwsIllegalArgumentException()
    {
        Map<String, Object> unknownStrategyConfig = new HashMap<>(validConfig);
        // Valid string in validation but unknown strategy name
        unknownStrategyConfig.put("strategies", List.of("UnknownSort"));
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(unknownStrategyConfig), "getStrategyInstance should throw IllegalArgumentException for an invalid strategy name");
    }
}
