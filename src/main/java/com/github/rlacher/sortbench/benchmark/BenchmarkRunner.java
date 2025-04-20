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
import java.util.stream.Stream;

import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkData;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkDataFactory;
import com.github.rlacher.sortbench.config.ConfigValidator;
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
        strategyMap.put("HeapSort", HeapSortStrategy.class);
        strategyMap.put("InsertionSort", InsertionSortStrategy.class);
        strategyMap.put("MergeSort", MergeSortStrategy.class);
        strategyMap.put("QuickSort", QuickSortStrategy.class);
    }

    /** Sort context to execute and benchmark different sorting strategies. */
    private Sorter sorter;

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

        final List<Integer> inputSizes = ConfigValidator.validateAndGetList(benchmarkConfig, "input_sizes", Integer.class);
        final int iterations = ConfigValidator.validateAndGet(benchmarkConfig, "iterations", Integer.class);
        final ProfilingMode profilingMode = ConfigValidator.validateAndGet(benchmarkConfig, "profiling_mode", ProfilingMode.class);

        final List<String> strategyNames = ConfigValidator.validateAndGetList(benchmarkConfig, "strategies", String.class);
        
        final Map<String, SortStrategy> strategies = new HashMap<>(strategyNames.size());
        strategyNames.stream().forEach(s -> strategies.put(s, getStrategyInstance(s, profilingMode)));

        final String dataTypeString = ConfigValidator.validateAndGet(benchmarkConfig, "data_type", String.class);
        final BenchmarkData.DataType dataType = BenchmarkData.DataType.fromString(dataTypeString);

        // Initialise benchmarkData with data arrangements that all sort strategies will run on.
        Map<BenchmarkContext, List<BenchmarkData>> benchmarkDataMap = generateBenchmarkData(dataType, inputSizes, strategyNames, iterations);

        List<BenchmarkResult> benchmarkResults = runIterations(strategies, benchmarkDataMap);

        return benchmarkResults;
    }

    /**
     * Runs the provided sorting strategies on the given benchmark data using the sorter.
     *
     * @param strategies A map of sorting strategy names to instances.
     * @param benchmarkDataMap The map of {@link BenchmarkContext} to lists of benchmark data.
     * @return A list of benchmark results.
     * @throws IllegalArgumentException If the {@code strategies} or {@code benchmarkDataMap} is null or empty.
     * @throws IllegalStateException If a required sorting strategy is not found for a benchmark context.
     */
    protected List<BenchmarkResult> runIterations(final Map<String, SortStrategy> strategies, final Map<BenchmarkContext, List<BenchmarkData>> benchmarkDataMap)
    {
        if (strategies == null || strategies.isEmpty())
        {
            throw new IllegalArgumentException("Strategies map must not be null or empty.");
        }
    
        if (benchmarkDataMap == null || benchmarkDataMap.isEmpty())
        {
            throw new IllegalArgumentException("Benchmark data map must not be null or empty.");
        }

        List<BenchmarkResult> allResults = new ArrayList<>();

        benchmarkDataMap.forEach((context, dataList) ->
        {
            String strategyName = context.getSortStrategyName();
            SortStrategy strategy = strategies.get(strategyName);

            if (strategy == null)
            {
                throw new IllegalStateException(String.format("No sorting strategy found for name '%s' in context: %s", strategyName, context));
            }

            sorter.setStrategy(strategy);

            List<BenchmarkMetric> metrics = dataList.stream()
                .map(data -> sorter.sort(data.getData()))
                .toList();

            for (BenchmarkMetric metric : metrics)
            {
                logger.fine(String.format("Sorting strategy: %s, benchmark metric: %s", strategy.getClass().getSimpleName(), metric.toString()));
                allResults.add(new BenchmarkResult(context, metric.getProfilingMode(), metric.getValue()));
            }
        });

        return allResults;
    }

    /**
     * Generates benchmark data grouped by context to facilitate batch processing.
     * 
     * This improves JVM optimisation by enhancing data locality.
     * Deep copies data for each strategy to ensure result comparability.
     *
     * @param dataType The type of data to generate.
     * @param sizes A list of integer data sizes.
     * @param strategyNames A list of sorting strategy names.
     * @param iterations The number of data arrangements per size and data type.
     * @return A map of {@link BenchmarkContext} to a list of benchmark data.
     */
    protected Map<BenchmarkContext, List<BenchmarkData>> generateBenchmarkData(BenchmarkData.DataType dataType, final List<Integer> sizes, final List<String> strategyNames, final int iterations)
    {
        Map<BenchmarkContext, List<BenchmarkData>> generatedData = new HashMap<>();

        for(int dataSize: sizes)
        {
            List<BenchmarkData> initialDataList = Stream.generate(() -> BenchmarkDataFactory.createData(dataType, dataSize))
                .limit(iterations)
                .collect(Collectors.toList());

            for(String strategyName : strategyNames)
            {
                List<BenchmarkData> dataListCopy = initialDataList.stream()
                    .map(dataArrangement -> new BenchmarkData(dataArrangement))
                    .collect(Collectors.toList());

                BenchmarkContext context = new BenchmarkContext(dataType, dataSize, strategyName);
                generatedData.put(context, dataListCopy);
            };
        };

        return generatedData;
    }

    /**
     * Creates an instance of a SortStrategy based on the provided strategy name and profiling mode.
     * 
     * @param strategyName The name of the sorting strategy.
     * @param mode The profiling mode to use.
     * @return An instance of the SortStrategy. Returns null in case of instantiation failure.
     */
    protected SortStrategy getStrategyInstance(String strategyName, ProfilingMode mode)
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
}
