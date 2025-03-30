package com.github.rlacher.sortanalyser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import com.github.rlacher.sortanalyser.core.BenchmarkResult;
import com.github.rlacher.sortanalyser.core.Benchmarkable;
import com.github.rlacher.sortanalyser.dummy.DummySorter;
import com.github.rlacher.sortanalyser.data.TestData;
import com.github.rlacher.sortanalyser.data.TestDataFactory;

/**
 * The entry point for the sorting algorithm benchmarking application.
 * 
 * This class runs the sorting algorithms.
 */
public class Main
{
    /** 
     * Logger for logging messages.
     */
    private static final Logger logger = Logger.getLogger(Main.class.getName());

	/**
     * Initial test data for benchmarking
     */
	private static final TestData TEST_DATA = TestDataFactory.createRandomData(10);

	/**
     * The main method, which starts the sorting algorithm benchmarking process.
     * 
     * This class initializes and runs a list of sorting algorithms against a predefined test dataset.
     *
     * @param args Command-line arguments (currently unused).
     */
    public static void main(String[] args)
    {
        List<Benchmarkable> sortAlgorithms = new ArrayList<>();
        sortAlgorithms.add(new DummySorter());
        
        logger.info(String.format("Test data: %s", TEST_DATA));
        runAlgorithms(sortAlgorithms, TEST_DATA);
    }

    /**
     * Runs the provided sorting algorithms on the given test data.
     * 
     * The benchmark ressult is printed to the console for each algorithm.
     * 
     * @param algorithms The list of sorting algorithms to be executed.
     * @param testData The test data to be sorted.
     */
    private static void runAlgorithms(List<Benchmarkable> algorithms, final TestData testData)
    {
        Objects.requireNonNull(algorithms, "The list of algorithms must not be null.");
        Objects.requireNonNull(testData, "The test data must not be null.");

        for (Benchmarkable algorithm : algorithms)
        {
            BenchmarkResult benchmarkResult = algorithm.benchmarkedOperation(testData.getDataCopy());
            logger.info(String.format("Sorting algorithm: %s, benchmark result: %s", algorithm.getClass().getSimpleName(), benchmarkResult.toString()));
        }
    }
}
