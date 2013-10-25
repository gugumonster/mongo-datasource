package com.gigaspaces.persistency.metadata;

import com.gigaspaces.document.DocumentObjectConverter;
import com.gigaspaces.metadata.SpaceDocumentSupport;

/**
 * A {@link DocumentObjectConverter} that does not fail if a type is missing
 * during conversion. Instead, it will return the original document.
 * 
 * @author Shadi Massalha
 */
public class MongoDocumentObjectConverter extends DocumentObjectConverter {

	public MongoDocumentObjectConverter() {
		super(false);
	}

	@Override
	public Object fromDocumentIfNeeded(Object object,
			SpaceDocumentSupport documentSupport, Class<?> expectedType) {
		return super
				.fromDocumentIfNeeded(object, documentSupport, expectedType);
	}

}
