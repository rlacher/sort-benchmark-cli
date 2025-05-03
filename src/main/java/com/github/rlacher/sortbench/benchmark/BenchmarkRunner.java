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
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkData;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkDataFactory;
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

    /**
     * Constructs a {@link BenchmarkRunner} that delegates sorting to the provided {@link Sorter}.
     *
     * @param sorter The {@link Sorter} instance to use.
     * @throws IllegalArgumentException If {@code sorter} is {@code null}.
     */
    public BenchmarkRunner(Sorter sorter)
    {
        if(sorter == null)
        {
            throw new IllegalArgumentException("Sorter must not be null");
        }

        this.sorter = sorter;
    }

    /**
     * Runs benchmarks based on the provided parameters.
     *
     * @param selectedStrategies A mapping from strategy names (e.g., "InsertionSort") to their respective {@link SortStrategy}
     * class types for dynamic instantiation.
     * @param dataTypes A set of data types for which to generate benchmark data.
     * @param inputSizes A list of input sizes for the benchmark data.
     * @param iterations The number of iterations for each data arrangement.
     * @param profilingMode The profiling mode to use for the benchmark.
     * @return A list of benchmark results with size equal or greater than 1.
     * @throws IllegalArgumentException If any of the input parameters are {@code null} or invalid:
     * <ul>
     * <li>{@code selectedStrategies} is {@code null}, empty, contains {@code null}
     * keys or values, or duplicate keys (ignoring case) or values.</li>
     * <li>{@code dataTypes} is {@code null} or empty.</li>
     * <li>{@code inputSizes} is {@code null} or empty or contains non-positive
     * integers.</li>
     * <li>{@code iterations} is not positive.</li>
     * <li>{@code profilingMode} is {@code null}.</li>
     * </ul>
     */
    public List<BenchmarkResult> run(
        final Map<String, Class<? extends SortStrategy>> selectedStrategies,
        final Set<BenchmarkData.DataType> dataTypes,
        final List<Integer> inputSizes,
        final int iterations,
        final ProfilingMode profilingMode)
    {
        if(selectedStrategies == null)
        {
            throw new IllegalArgumentException("Strategy map must not be null");
        }
        if(selectedStrategies.isEmpty())
        {
            throw new IllegalArgumentException("Strategy map must not be empty");
        }
        if(selectedStrategies.keySet().stream().anyMatch(key -> key == null))
        {
            throw new IllegalArgumentException("Strategy map must not contain null values.");
        }
        if(selectedStrategies.values().stream().anyMatch(value -> value == null))
        {
            throw new IllegalArgumentException("Strategy map must not contain null keys.");
        }
        if(selectedStrategies.keySet().stream().map(String::toLowerCase).distinct().count() != selectedStrategies.size())
        {
            throw new IllegalArgumentException("Strategy map must not contain duplicate keys (case-insensitive).");
        }
        if(selectedStrategies.values().stream().distinct().count() != selectedStrategies.size())
        {
            throw new IllegalArgumentException("Strategy map must not contain duplicate values.");
        }
        if(dataTypes == null)
        {
            throw new IllegalArgumentException("Data types set must not be null");
        }
        if(dataTypes.isEmpty())
        {
            throw new IllegalArgumentException("Data types set must not be empty");
        }
        if(inputSizes == null)
        {
            throw new IllegalArgumentException("Input sizes list must not be null");
        }
        if(inputSizes.isEmpty())
        {
            throw new IllegalArgumentException("Input sizes list must not be empty");
        }
        if(inputSizes.stream().anyMatch(size -> size <= 0))
        {
            throw new IllegalArgumentException("Input sizes must be positive integers.");
        }
        if(iterations <= 0)
        {
            throw new IllegalArgumentException("Iterations must be a positive integer.");
        }
        if(profilingMode == null)
        {
            throw new IllegalArgumentException("Profiling mode must not be null");
        }

        // Initialise benchmarkData with data arrangements that all sort strategies will run on.
        Map<BenchmarkContext, List<BenchmarkData>> benchmarkDataMap = generateBenchmarkData(selectedStrategies.keySet(), dataTypes, inputSizes, iterations);

        final Map<String, SortStrategy> instantiatedStrategies = selectedStrategies.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> getStrategyInstance(entry.getValue(), profilingMode)));

        List<BenchmarkResult> benchmarkResults = runIterations(instantiatedStrategies, benchmarkDataMap);

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
     * @param strategyNames A set of sorting strategy names.
     * @param dataTypes A set of data types for which to generate data.
     * @param sizes A set of integer data sizes for which to generate data.
     * @param iterations The number of data arrangements per size and data type.
     * @return A map of {@link BenchmarkContext} to a list of benchmark data. The map may be
     * empty if any of the input parameters are empty.
     * @throws NullPointerException If any of {@code dataTypes}, {@code sizes} or {@code strategyNames} are {@code null}.
     * This is unexpected as the caller {@link BenchmarkRunner#run} ensures these arguments are not {@code null}.
     */
    protected Map<BenchmarkContext, List<BenchmarkData>> generateBenchmarkData(
        final Set<String> strategyNames,
        final Set<BenchmarkData.DataType> dataTypes,
        final List<Integer> sizes,
        final int iterations)
    {
        Map<BenchmarkContext, List<BenchmarkData>> generatedData = new HashMap<>();

        for(BenchmarkData.DataType dataType : dataTypes)
        {
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
                }
            }
        }

        return generatedData;
    }

    /**
     * Creates an instance of a {@link SortStrategy} based on the provided strategy class and profiling mode.
     *
     * @param strategyClass The class type of the sorting strategy.
     * @param mode The profiling mode to use.
     * @return An {@link SortStrategy} instance of type {@code strategyClass} configured with the provided profiling mode.
     * @throws IllegalArgumentException If {@code strategyClass} is {@code null}.
     * @throws IllegalStateException If instantiation fails for the {@link SortStrategy}.
     */
    protected SortStrategy getStrategyInstance(Class<? extends SortStrategy> strategyClass, final ProfilingMode mode)
    {
        if (strategyClass == null)
        {
            throw new IllegalArgumentException(String.format("Strategy class must not be null"));
        }

        SortStrategy strategy = null;
        try
        {
            strategy = strategyClass.getConstructor(new Class<?>[]{ Benchmarker.class }).newInstance(new Benchmarker(mode));
        }
        catch(ReflectiveOperationException exception)
        {
            throw new IllegalStateException(String.format("Could not instantiate strategy class: %s.", strategyClass.getSimpleName()), exception);
        }

        return strategy;
    }
}
