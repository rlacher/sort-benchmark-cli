package com.github.rlacher.sortanalyser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.github.rlacher.sortanalyser.core.SortAlgorithm;
import com.github.rlacher.sortanalyser.dummy.DummySorter;

/**
 * The entry point for the sorting algorithm benchmarking application.
 * 
 * This class runs the sorting algorithms.
 */
public class Main
{
	// Initial test data: An unsorted array of integers.
	private static final int[] TEST_DATA = {3, 7, 0, 9, 2, 5, 8, 1, 6, 4};

	/**
     * The main method, which starts the sorting algorithm benchmarking process.
     * 
     * This class initializes and runs a list of sorting algorithms against a predefined test dataset.
     *
     * @param args Command-line arguments (currently unused).
     */
    public static void main(String[] args)
    {
        List<SortAlgorithm> sortAlgorithms = new ArrayList<>();
        sortAlgorithms.add(new DummySorter());
        
        runAlgorithms(sortAlgorithms, TEST_DATA);
    }

    /**
     * Runs the provided sorting algorithms on the given test data.
     * 
     * @param algorithms The list of sorting algorithms to be executed.
     * @param testData The test data to be sorted. A copy of the data is created for each algorithm.
     */
    private static void runAlgorithms(List<SortAlgorithm> algorithms, final int[] testData)
    {
        Objects.requireNonNull(algorithms, "The list of algorithms must not be null.");
        Objects.requireNonNull(testData, "The test data must not be null.");

        for (SortAlgorithm algorithm : algorithms)
        {
            int[] dataCopy = Arrays.copyOf(testData, testData.length);
            algorithm.sort(dataCopy);
        }
    }
}
