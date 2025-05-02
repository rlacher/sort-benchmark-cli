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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.github.rlacher.sortbench.sorter.Sorter;
import com.github.rlacher.sortbench.strategies.SortStrategy;
import com.github.rlacher.sortbench.strategies.implementations.MergeSortStrategy;
import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkData;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkData.DataType;
import com.github.rlacher.sortbench.results.BenchmarkContext;
import com.github.rlacher.sortbench.results.BenchmarkMetric;
import com.github.rlacher.sortbench.results.BenchmarkResult;

// Unit tests for the BenchmarkRunner class.
class BenchmarkRunnerTest
{
    private Sorter mockSorter;
    private BenchmarkRunner benchmarkRunner;
    private Map<String, Object> validConfig;
    private Map<String, Class<? extends SortStrategy>> validStrategyMap;

    @BeforeEach
    void setUp()
    {
        mockSorter = Mockito.mock(Sorter.class);
        validStrategyMap = Map.of("MergeSort", MergeSortStrategy.class);
        benchmarkRunner = new BenchmarkRunner(mockSorter, validStrategyMap);
        validConfig = new HashMap<>();
        validConfig.put("input_sizes", Arrays.asList(10));
        validConfig.put("iterations", 1);
        validConfig.put("strategies", Set.of("MergeSort"));
        validConfig.put("profiling_mode", ProfilingMode.EXECUTION_TIME);
        validConfig.put("data_types", Set.of(BenchmarkData.DataType.RANDOM.toString()));
    }

    @Test
    void constructor_nullSorter_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkRunner(null, validStrategyMap), "Constructor should throw IllegalArgumentException when sorter is null");
    }

    @Test
    void constructor_nullStrategyMap_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkRunner(mockSorter, null), "Constructor should throw IllegalArgumentException when strategy map is null");
    }

    @Test
    void constructor_emptyMap_throwsIllegalStateException()
    {
        HashMap<String, Class<? extends SortStrategy>> emptyMap = new HashMap<>();
        assertThrows(IllegalStateException.class, () -> new BenchmarkRunner(mockSorter, emptyMap), "Constructor should throw IllegalArgumentException when strategy map is null");
    }

    @Test
    void run_nullConfig_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(null), "run() should throw IllegalArgumentException when config is null");
    }

    @Test
    void run_invalidInputSizes_throwsIllegalArgumentException()
    {
        validConfig.put("input_sizes", Arrays.asList("invalid"));
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(validConfig), "run() should throw IllegalArgumentException when input_sizes is invalid");
    }

    @Test
    void run_invalidStrategies_throwsIllegalArgumentException()
    {
        validConfig.put("strategies", Arrays.asList(123));
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(validConfig), "run() should throw IllegalArgumentException when strategies is invalid");
    }

    @Test
    void run_undefinedDataTypes_throwsIllegalArgumentException()
    {
        validConfig.remove("data_types");
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(validConfig), "run() should throw IllegalArgumentException when data types are undefined");
    }

    @Test
    void run_emptyDataTypes_throwsIllegalArgumentException()
    {
        validConfig.replace("data_types", Set.of());
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(validConfig), "run() should throw IllegalArgumentException when data types are empty");
    }

    @Test
    void run_nonStringDataType_throwsIllegalArgumentException()
    {
        validConfig.replace("data_types", Set.of(3));
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(validConfig), "run() should throw IllegalArgumentException when a data type is not a String");
    }

    @Test
    void run_unknownDataType_throwsIllegalArgumentException()
    {
        validConfig.replace("data_types", Set.of("UnknownDataType"));
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(validConfig), "run() should throw IllegalArgumentException when a data type is unknown");
    }

    @Test
    void run_validConfig_returnsBenchmarkResults()
    {
        BenchmarkMetric mockMetric = mock(BenchmarkMetric.class);
        when(mockSorter.sort(any(int[].class))).thenReturn(mockMetric);
        when(mockMetric.getProfilingMode()).thenReturn((ProfilingMode)validConfig.get("profiling_mode"));

        List<BenchmarkResult> results = benchmarkRunner.run(validConfig);
        assertNotNull(results, "run() should return a non-null list of BenchmarkResults");
        assertFalse(results.isEmpty(), "run() should return a non-empty list of BenchmarkResults");
    }

    @Test
    void run_unknownStrategy_throwsIllegalArgumentException()
    {
        Map<String, Object> unknownStrategyConfig = new HashMap<>(validConfig);
        // Valid string in validation but unknown strategy name
        unknownStrategyConfig.put("strategies", List.of("UnknownSort"));
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(unknownStrategyConfig), "getStrategyInstance() should throw IllegalArgumentException for an invalid strategy name");
    }

    @Test
    void run_emptyInputSizes_throwsIllegalArgumentException()
    {
        Map<String, Object> emptySizesConfig = new HashMap<>(validConfig);
        emptySizesConfig.put("input_sizes", List.of());
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(emptySizesConfig), 
        "Running with empty input sizes should result in an IllegalArgumentException.");
    }

    @Test
    void run_emptyStrategies_throwsIllegalArgumentException()
    {
        Map<String, Object> emptyStrategiesConfig = new HashMap<>(validConfig);
        emptyStrategiesConfig.put("strategies", List.of());
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(emptyStrategiesConfig), 
        "Running with empty strategies should result in an IllegalArgumentException.");
    }

    @Test
    void run_benchmarkDataWithInvalidStrategy_throwsIllegalStateException()
    {
        BenchmarkContext invalidStrategyContext = new BenchmarkContext(DataType.SORTED, 10, "InvalidStrategyName");

        Map<BenchmarkContext, List<BenchmarkData>> invalidStrategyMap = new HashMap<>();
        invalidStrategyMap.put(invalidStrategyContext, new ArrayList<BenchmarkData>());

        BenchmarkRunner spiedRunner = spy(new BenchmarkRunner(mockSorter, validStrategyMap));
        doReturn(invalidStrategyMap)
            .when(spiedRunner)
            .generateBenchmarkData(anySet(), any(), any(), anyInt());

        assertThrows(IllegalStateException.class, () -> spiedRunner.run(validConfig),
        "Running with benchmark data containing an invalid strategy should throw an IllegalStateException.");
    }

    @Test
    void run_nonInstantiableStrategy_throwsIllegalStateException()
    {
        class FailingSortStrategy implements SortStrategy
        {
            public FailingSortStrategy(Benchmarker benchmarker)
            {
                throw new RuntimeException("Intentionally failing instantiation for testing purposes.");
            }

            @Override
            public BenchmarkMetric sort(final int[] array)
            {
                return new BenchmarkMetric(ProfilingMode.NONE, 0);
            }
        };

        String failingStrategyName = FailingSortStrategy.class.getSimpleName();
        Map<String, Class<? extends SortStrategy>> failingMap = Map.of(failingStrategyName, FailingSortStrategy.class);
        validConfig.replace("strategies", Set.of(failingStrategyName));

        benchmarkRunner = new BenchmarkRunner(mockSorter, failingMap);
        assertThrows(IllegalStateException.class, () -> benchmarkRunner.run(validConfig), "run() should throw IllegalStateException if a strategy is not instantiable.");
    }

    @Test
    void run_duplicateStrategies_throwsIllegalStateException()
    {
        Map<String, Class<? extends SortStrategy>> duplicatesMap = Map.of
        (
            "mergesort", MergeSortStrategy.class,
            "MERGESORT", MergeSortStrategy.class
        );

        benchmarkRunner = new BenchmarkRunner(mockSorter, duplicatesMap);
        assertThrows(IllegalStateException.class, () -> benchmarkRunner.run(validConfig), "run() should throw IllegalStateException if a strategy name is ambiguous.");
    }
}
