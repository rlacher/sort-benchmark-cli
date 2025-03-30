package com.github.rlacher.sortanalyser.dummy;

import java.util.Objects;
import java.util.logging.Logger;

import com.github.rlacher.sortanalyser.Main;
import com.github.rlacher.sortanalyser.core.AbstractSortAlgorithm;

/**
 * A dummy implementation of the {@link AbstractSortAlgorithm} class.
 * 
 * This class does not perform any sorting.
 */
public class DummySorter extends AbstractSortAlgorithm
{
    /** 
     * Logger for logging messages.
     */
    private static final Logger logger = Logger.getLogger(DummySorter.class.getName());

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

		logger.info("I am a dummy sorter and do not actually sort.");
	}
}
