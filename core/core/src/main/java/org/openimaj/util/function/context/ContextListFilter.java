package org.openimaj.util.function.context;

import java.util.ArrayList;
import java.util.List;

import org.openimaj.util.data.Context;
import org.openimaj.util.function.Function;
import org.openimaj.util.function.Predicate;

/**
 * Filter a list of items belonging to a single {@link Context} entry using a
 * predicate
 * 
 * @author Sina Samangooei (ss@ecs.soton.ac.uk)
 * 
 * @param <IN>
 *            Type of items in the list
 */
public class ContextListFilter<IN> extends ContextAdaptor<Predicate<IN>, List<IN>, List<IN>>
		implements
		Function<Context, Context>
{
	/**
	 * Construct with the given options.
	 * 
	 * @param inner
	 *            the predicate
	 * @param extract
	 *            the extractor
	 * @param insert
	 *            the insertor
	 */
	public ContextListFilter(Predicate<IN> inner, ContextExtractor<List<IN>> extract, ContextInsertor<List<IN>> insert)
	{
		super(inner, extract, insert);
	}

	/**
	 * Construct with the given predicate. The insertor and extractor are
	 * created from the given keys.
	 * 
	 * @param inner
	 *            the predicate
	 * @param extract
	 *            the key to extract from the context to produce the input for
	 *            the object
	 * @param insert
	 *            the key to insert with the the output for the object
	 */
	public ContextListFilter(Predicate<IN> inner, String extract, String insert)
	{
		super(inner, extract, insert);
	}

	/**
	 * Construct with the given predicate. The insertor and extractor are
	 * created from the same key, so the output will overwrite the input.
	 * 
	 * @param inner
	 *            the predicate
	 * @param both
	 *            the key to extract/insert
	 */
	public ContextListFilter(Predicate<IN> inner, String both)
	{
		super(inner, both, both);
	}

	@Override
	public Context apply(Context in) {
		final List<IN> obj = extract.extract(in);
		final List<IN> out = new ArrayList<IN>();

		for (final IN inItem : obj) {
			if (inner.test(inItem))
				out.add(inItem);
		}

		insert.insert(out, in);

		return in;
	}
}
