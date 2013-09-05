package com.gigaspaces.persistency.metadata;


/**
 * @author Shadi Massalha
 *
 * @param <F> source mapping object
 * @param <T> destination mapping object
 */
public interface Mapper<F, T> {

	/**
	 * @param bson
	 * @return
	 */
	T maps(F bson);
}
