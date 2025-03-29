package com.github.rlacher.sortanalyser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.rlacher.sortanalyser.core.SortAlgorithm;
import com.github.rlacher.sortanalyser.dummy.DummySorter;

/**
 * The entry point for the sorting algorithm benchmarking application.
 * 
 * This class runs the sorting algorithms.
 */
public class Main {

	// Initial test data: An unsorted array of integers.
	private static final int[] TEST_DATA = {3, 7, 0, 9, 2, 5, 8, 1, 6, 4};

	/**
     * The main method, which starts the sorting algorithm benchmarking process.
     *
     * @param args Command-line arguments (currently unused).
     */
    public static void main(String[] args)
    {
        List<SortAlgorithm> sortAlgorithms = new ArrayList<>();
        sortAlgorithms.add(new DummySorter());
        
        for(SortAlgorithm sorter : sortAlgorithms)
        {
			int[] testDataCopy = Arrays.copyOf(TEST_DATA, TEST_DATA.length);
			sorter.sort(testDataCopy);
		}
    }
}
