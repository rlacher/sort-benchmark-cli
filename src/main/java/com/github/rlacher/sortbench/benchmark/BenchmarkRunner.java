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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkData;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkDataFactory;
import com.github.rlacher.sortbench.results.BenchmarkMetric;
import com.github.rlacher.sortbench.results.BenchmarkResult;
import com.github.rlacher.sortbench.results.BenchmarkContext;
import com.github.rlacher.sortbench.sorter.Sorter;
import com.github.rlacher.sortbench.strategies.SortStrategy;
import com.github.rlacher.sortbench.strategies.implementations.*;

/**
 * Executes and benchmarks sorting algorithms based on a provided configuration and returns raw benchmark results.
 * 
 * Each benchmark run focuses on a single profiling mode,
 * though it may involve multiple sorting strategies, data sizes, and data types.
 */
public class BenchmarkRunner
{
    /** Logger for logging messages. */
    private static final Logger logger = Logger.getLogger(BenchmarkRunner.class.getName());

    /** Maps sort strategy names (e.g. "BubbleSort") to their {@link SortStrategy} classes for dynamic instantiation. */
    private static final Map<String, Class<? extends SortStrategy>> strategyMap = new HashMap<>();

    static
    {
        strategyMap.put("BubbleSort", BubbleSortStrategy.class);
        strategyMap.put("InsertionSort", InsertionSortStrategy.class);
        strategyMap.put("MergeSort", MergeSortStrategy.class);
        strategyMap.put("HeapSort", HeapSortStrategy.class);
    }

    /** Sort context to execute and benchmark different sorting strategies. */
    private Sorter sorter;

    /**
     * Creates an instance of a SortStrategy based on the provided strategy name and profiling mode.
     * 
     * @param strategyName The name of the sorting strategy.
     * @param mode The profiling mode to use.
     * @return An instance of the SortStrategy. Returns null in case of instantiation failure.
     */
    public static SortStrategy getStrategyInstance(String strategyName, ProfilingMode mode)
    {
        Class<? extends SortStrategy> strategyClass = strategyMap.get(strategyName);
        if (strategyClass == null)
        {
            throw new IllegalArgumentException(String.format("Unknown sort strategy '%s'", strategyName));
        }

        SortStrategy strategy = null;
        try
        {
            strategy = strategyClass.getConstructor(new Class<?>[]{ Benchmarker.class }).newInstance(new Benchmarker(mode));
        }
        catch(ReflectiveOperationException exception)
        {
            logger.warning(String.format("Exception instantiating strategy name '%s' (type: %s, message: %s)", strategyName, exception.getClass().getSimpleName(), exception.getMessage()));
        }

        return strategy;
    }

    public BenchmarkRunner(Sorter sorter)
    {
        if(sorter == null)
        {
            throw new IllegalArgumentException("Sorter must not be null");
        }

        this.sorter = sorter;
    }

    /**
     * Runs benchmarks based on the provided configuration.
     *
     * @param benchmarkConfig A map containing benchmark configuration parameters.
     * @return A list of benchmark results.
     * @throws IllegalArgumentException If benchmark config is null or configuration is invalid.
     */
    public List<BenchmarkResult> run(Map<String, Object> benchmarkConfig)
    {
        if(benchmarkConfig == null)
        {
            throw new IllegalArgumentException("Benchmark config must not be null");
        }

        final List<?> inputSizesList = (List<?>) benchmarkConfig.get("input_sizes");

        if (inputSizesList == null || !inputSizesList.stream().allMatch(Integer.class::isInstance))
        {
            throw new IllegalArgumentException("Invalid input sizes configuration");
        }

        final int[] inputSizes = inputSizesList.stream()
            .map(Integer.class::cast)
            .mapToInt(Integer::intValue)
            .toArray();

        final int iterations = (int) benchmarkConfig.get("iterations");
        final ProfilingMode profilingMode = (ProfilingMode) benchmarkConfig.get("profiling_mode");
        final List<?> strategyNames = (List<?>) benchmarkConfig.get("strategies");

        if (strategyNames == null || !strategyNames.stream().allMatch(String.class::isInstance))
        {
            throw new IllegalArgumentException("Invalid strategies configuration");
        }

        final List<SortStrategy> strategies = strategyNames.stream()
            .map(String.class::cast)
            .map(s -> getStrategyInstance(s, profilingMode))
            .collect(Collectors.toList());

        // Initialise benchmarkData with data arrangements that all sort strategies will run on.
        Map<Integer, List<BenchmarkData>> benchmarkDataMap = generateRandomBenchmarkDataBySizes(inputSizes, iterations);

        List<BenchmarkResult> benchmarkResults = runIterations(strategies, benchmarkDataMap);

        return benchmarkResults;
    }

    /**
     * Runs the provided sorting strategies on the given benchmark data through the sorter context.
     *
     * @param strategies The list of sorting strategies to be executed.
     * @param benchmarkDataMap The map of data sizes to lists of benchmark data.
     * @return A list of benchmark results.
     * @throws IllegalArgumentException If strategies or benchmarkDataMap are null or empty.
     */
    private List<BenchmarkResult> runIterations(final List<SortStrategy> strategies, final Map<Integer, List<BenchmarkData>> benchmarkDataMap)
    {
        if (strategies == null || strategies.isEmpty())
        {
            throw new IllegalArgumentException("Strategies list must not be null or empty.");
        }
    
        if (benchmarkDataMap == null || benchmarkDataMap.isEmpty())
        {
            throw new IllegalArgumentException("Benchmark data map must not be null or empty.");
        }

        List<BenchmarkResult> allResults = new ArrayList<>();

        benchmarkDataMap.forEach((dataSize, benchmarkDataList) ->
        {
            logger.fine(String.format("Data size: %d", dataSize));

            strategies.forEach(sortStrategy ->
            {
                sorter.setStrategy(sortStrategy);

                List<BenchmarkMetric> metrics = benchmarkDataList.stream()
                .map(benchmarkData ->
                        sorter.sort(new BenchmarkData(benchmarkData).getData())
                    )
                .toList();

                metrics.stream().forEach(metric ->
                    logger.fine(String.format("Sorting strategy: %s, benchmark metric: %s", sortStrategy.getClass().getSimpleName(), metric.toString())));

                BenchmarkContext context = new BenchmarkContext(BenchmarkData.DataType.RANDOM, dataSize, sortStrategy.name());

                List<BenchmarkResult> results = metrics.stream()
                    .map
                    (
                        metric -> new BenchmarkResult(context, metric.getProfilingMode(), metric.getValue())
                    )
                    .collect(Collectors.toList());

                allResults.addAll(results);
            });
        });

        return allResults;
    }

    /**
     * Generates benchmark data grouped by data length to facilitate batch processing.
     * This improves JVM optimisation by enhancing data locality.
     *
     * @param sizes An array of data sizes to generate benchmark data for.
     * @return A map where keys are data lengths and values are lists of benchmark data.
     */
    private static Map<Integer, List<BenchmarkData>> generateRandomBenchmarkDataBySizes(final int[] sizes, final int iterations)
    {
        Map<Integer, List<BenchmarkData>> dataBySize = new HashMap<>();

        IntStream.range(0, sizes.length)
            .forEach(i -> 
            {
                final int dataSize = sizes[i];
                List<BenchmarkData> dataList = Stream.generate(() -> BenchmarkDataFactory.createRandomData(dataSize))
                    .limit(iterations)
                    .collect(Collectors.toList());
                dataBySize.put(dataSize, dataList);
            });

        return dataBySize;
    }
}
