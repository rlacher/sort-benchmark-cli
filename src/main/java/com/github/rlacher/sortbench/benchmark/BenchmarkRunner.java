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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

/**
 * Executes and benchmarks sorting algorithms based on a provided configuration and returns raw benchmark results.
 *
 * <p>Each benchmark run focuses on a single profiling mode, though it may involve multiple sorting strategies,
 * data sizes, and data types.</p>
 */
public class BenchmarkRunner
{
    /** Logger for logging messages. */
    private static final Logger logger = Logger.getLogger(BenchmarkRunner.class.getName());

    /** Sort context to execute and benchmark different sorting strategies. */
    private Sorter sorter;

    /** Maps sort strategy names (e.g. "BubbleSort") to their {@link SortStrategy} classes for dynamic instantiation. */
    private final Map<String, Class<? extends SortStrategy>> strategyMap;

    /**
     * Constructs a {@link BenchmarkRunner} that delegates sorting to the provided {@link Sorter}.
     *
     * @param sorter The {@link Sorter} instance to use.
     * @param strategyMap A mapping from strategy names (e.g., "InsertionSort") to their respective
     * {@link SortStrategy} class types. Must not be null and must contain at least one entry.
     * @throws IllegalArgumentException If {@code sorter} or {@code strategyMap} are {@code null}.
     * @throws IllegalStateException If {@code strategyMap} is empty, indicating no available sorting strategies.
     */
    public BenchmarkRunner(Sorter sorter, Map<String, Class<? extends SortStrategy>> strategyMap)
    {
        if(sorter == null)
        {
            throw new IllegalArgumentException("Sorter must not be null");
        }

        if(strategyMap == null)
        {
            throw new IllegalArgumentException("Strategy map must not be null");
        }

        if(strategyMap.isEmpty())
        {
            throw new IllegalStateException("Strategy map must have at least one entry.");
        }

        this.sorter = sorter;
        this.strategyMap = strategyMap;
    }

    /**
     * Runs benchmarks based on the provided configuration.
     *
     * @param benchmarkConfig A map containing benchmark configuration parameters.
     * @return A list of benchmark results.
     * @throws IllegalArgumentException If benchmark config is {@code null}, the configuration
     * is otherwise invalid or if required list-based parameters are empty.
     */
    public List<BenchmarkResult> run(Map<String, Object> benchmarkConfig)
    {
        if(benchmarkConfig == null)
        {
            throw new IllegalArgumentException("Benchmark config must not be null");
        }

        final List<Integer> inputSizes = new ArrayList<>(ConfigValidator.validateAndGetCollection(benchmarkConfig, "input_sizes", Integer.class));
        final int iterations = ConfigValidator.validateAndGet(benchmarkConfig, "iterations", Integer.class);
        final ProfilingMode profilingMode = ConfigValidator.validateAndGet(benchmarkConfig, "profiling_mode", ProfilingMode.class);

        final Set<String> strategyNames = new HashSet<>(ConfigValidator.validateAndGetCollection(benchmarkConfig, "strategies", String.class));
        
        final Map<String, SortStrategy> strategies = new HashMap<>(strategyNames.size());
        strategyNames.stream().forEach(s ->
        {
            SortStrategy strategyInstance = getStrategyInstance(s, profilingMode);
            // Put correctly-cased name in strategies map
            strategies.put(strategyInstance.name(), strategyInstance);
        });

        final String dataTypeString = ConfigValidator.validateAndGet(benchmarkConfig, "data_type", String.class);
        final BenchmarkData.DataType dataType = BenchmarkData.DataType.fromString(dataTypeString);

        // Initialise benchmarkData with data arrangements that all sort strategies will run on.
        Map<BenchmarkContext, List<BenchmarkData>> benchmarkDataMap = generateBenchmarkData(dataType, inputSizes, strategies.keySet(), iterations);

        List<BenchmarkResult> benchmarkResults = runIterations(strategies, benchmarkDataMap);

        return benchmarkResults;
    }

    /**
     * Runs the provided sorting strategies on the given benchmark data using the sorter.
     *
     * @param strategies A map of sorting strategy names to instances.
     * @param benchmarkDataMap The map of {@link BenchmarkContext} to lists of benchmark data.
     * @return A list of benchmark results.
     * @throws IllegalArgumentException If the {@code strategies} or {@code benchmarkDataMap} is {@code null} or empty.
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
     * <p>This improves JVM optimisation by enhancing data locality.
     * Deep copies data for each strategy to ensure result comparability.</p>
     *
     * @param dataType The type of data to generate.
     * @param sizes A set of integer data sizes.
     * @param strategyNames A set of sorting strategy names.
     * @param iterations The number of data arrangements per size and data type.
     * @return A map of {@link BenchmarkContext} to a list of benchmark data.
     * @throws NullPointerException If any of {@code dataType}, {@code sizes} or {@code strategyNames} are {@code null}.
     * This is unexpected as the caller {@link BenchmarkRunner#run} ensures these arguments are not {@code null}.
     */
    protected Map<BenchmarkContext, List<BenchmarkData>> generateBenchmarkData(BenchmarkData.DataType dataType, final List<Integer> sizes, final Set<String> strategyNames, final int iterations)
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
     * Creates an instance of a {@link SortStrategy} based on the provided strategy name and profiling mode.
     *
     * @param strategyName The name of the sorting strategy.
     * @param mode The profiling mode to use.
     * @return An instance of the {@link SortStrategy}. Returns {@code null} in case of instantiation failure.
     * @throws IllegalArgumentException If {@code strategyName} does not match any known sort strategy.
     * @throws IllegalStateException If {@code strategyName} matches multiple known sort strategies (case-insensitively),
     * or if an error occurs during the instantiation of the {@link SortStrategy}.
     */
    protected SortStrategy getStrategyInstance(String strategyName, ProfilingMode mode)
    {
        String lowerCaseStrategyName = strategyName.toLowerCase();
        List<String> matchedStrategyKeys = strategyMap.keySet().stream()
                .filter(key -> key.toLowerCase().equals(lowerCaseStrategyName))
                .toList();

        if(matchedStrategyKeys.isEmpty())
        {
            throw new IllegalArgumentException(String.format("Unknown sort strategy '%s' when matching case-insensitively. Available strategies: %s",
            strategyName, strategyMap.keySet()));
        }
        else if(matchedStrategyKeys.size() > 1)
        {
            throw new IllegalStateException(String.format("Strategy name '%s' is ambiguous (matches: %s) when matching case-insensitively.",
            strategyName, matchedStrategyKeys));
        }

        Class<? extends SortStrategy> strategyClass = strategyMap.get(matchedStrategyKeys.getFirst());
        if (strategyClass == null)
        {
            throw new IllegalStateException(String.format("Map returns null retrieving class for '%s'", strategyName));
        }

        SortStrategy strategy = null;
        try
        {
            strategy = strategyClass.getConstructor(new Class<?>[]{ Benchmarker.class }).newInstance(new Benchmarker(mode));
        }
        catch(ReflectiveOperationException exception)
        {
            throw new IllegalStateException(String.format("Could not instantiate strategy '%s' (type: %s).", strategyName, strategyClass.getSimpleName()), exception);
        }

        return strategy;
    }
}
