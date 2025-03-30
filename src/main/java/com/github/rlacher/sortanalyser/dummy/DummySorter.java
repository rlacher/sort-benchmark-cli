package com.github.rlacher.sortanalyser.dummy;

import java.util.Objects;

import com.github.rlacher.sortanalyser.core.SortAlgorithm;

/**
 * A dummy implementation of the {@link SortAlgorithm} interface.
 * 
 * This class does not perform any sorting.
 */
public class DummySorter implements SortAlgorithm
{
	/**
     * Does not sort the provided array. Prints a message to the console instead.
     *
     * @param array The array to be sorted (ignored).
     * @throws NullPointerException If the input array is null.
     */
	@Override
	public void sort(final int[] array) throws NullPointerException
	{
		Objects.requireNonNull(array, "Array must not be null");
		
		System.out.println("I am a dummy sorter and do not actually sort.");
	}
}
