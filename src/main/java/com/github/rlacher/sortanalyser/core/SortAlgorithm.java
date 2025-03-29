package com.github.rlacher.sortanalyser.core;

/**
 * Defines the contract for all sorting algorithms.
 */
public interface SortAlgorithm
{
	/**
	 * Sorts an array of integers in place.
	 *
	 * @param array The array to be sorted.
	 * @throws IllegalArgumentException If 'array' is null.
	 * @post The 'array' is sorted in ascending order.
	 * 
	 * An empty array is considered sorted and will not result in an exception.
	 */
	void sort(final int[] array) throws IllegalArgumentException;
}
