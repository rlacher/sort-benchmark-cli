package com.github.rlacher.sortanalyser.core;

/**
 * Defines the contract for all sorting algorithms.
 */
public interface Sortable
{
	/**
	 * Sorts an array of integers in place.
	 *
	 * @param array The array to be sorted.
	 * @throws NullPointerException If 'array' is null.
	 * 
	 * An empty array is considered sorted and will not result in an exception.
	 * Postcondition: The array is sorted in ascending order.
	 */
	void sort(final int[] array) throws NullPointerException;
}
