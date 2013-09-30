package com.gigaspaces.persistency;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;

import com.gigaspaces.itest.model.IssuePojo;

public class BasicMongoSpaceAllInCacheTest extends AbstractSystemTestUnit {

	@Override
	public void test() {
		List<IssuePojo> issuePojos = new LinkedList<IssuePojo>();

		for (int i = 0; i < 15; i++) {
			issuePojos.add(new IssuePojo(i + 1, "dank" + (i + 1)));
		}

		gigaSpace.writeMultiple(issuePojos.toArray(new IssuePojo[] {}));

		waitForEmptyReplicationBacklog(gigaSpace);

		IssuePojo[] pojos = gigaSpace.readMultiple(new IssuePojo(), 20);

		Assert.assertEquals("issue pojos", issuePojos, Arrays.asList(pojos));
	}

}
