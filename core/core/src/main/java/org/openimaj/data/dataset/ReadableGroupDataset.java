package org.openimaj.data.dataset;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Set;

import org.openimaj.io.ObjectReader;
import org.openimaj.util.iterator.ConcatenatedIterable;

/**
 * Base class for {@link GroupedDataset}s in which each instance is read with an
 * {@link ObjectReader}.
 * 
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 * 
 * @param <KEY>
 *            Type of dataset class key
 * @param <DATASET>
 *            Type of sub-datasets.
 * @param <INSTANCE>
 *            Type of instances in the dataset
 */
public abstract class ReadableGroupDataset<KEY, DATASET extends Dataset<INSTANCE>, INSTANCE>
		extends AbstractMap<KEY, DATASET>
		implements GroupedDataset<KEY, DATASET, INSTANCE>
{
	protected ObjectReader<INSTANCE> reader;

	/**
	 * Construct with the given {@link ObjectReader}.
	 * 
	 * @param reader
	 *            the {@link ObjectReader}.
	 */
	public ReadableGroupDataset(ObjectReader<INSTANCE> reader) {
		this.reader = reader;
	}

	@Override
	public INSTANCE getRandomInstance() {
		final int index = (int) (Math.random() * numInstances());
		int count = 0;

		for (final KEY key : this.getGroups()) {
			for (final INSTANCE i : getInstances(key)) {
				if (index == count)
					return i;

				count++;
			}
		}
		return null;
	}

	@Override
	public int numInstances() {
		int size = 0;

		for (final KEY key : this.getGroups()) {
			size += this.getInstances(key).numInstances();
		}

		return size;
	}

	@Override
	public Iterator<INSTANCE> iterator() {
		return new ConcatenatedIterable<INSTANCE>(this.values()).iterator();
	}

	@Override
	public DATASET getInstances(KEY key) {
		return this.get(key);
	}

	@Override
	public Set<KEY> getGroups() {
		return this.keySet();
	}

	@Override
	public INSTANCE getRandomInstances(KEY key) {
		return get(key).getRandomInstance();
	}
}
