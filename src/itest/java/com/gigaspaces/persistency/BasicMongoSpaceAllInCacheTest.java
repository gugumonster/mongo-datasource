package com.gigaspaces.persistency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gigaspaces.itest.model.IssuePojo;
import com.gigaspaces.persistency.utils.AssertUtils;

public class BasicMongoSpaceAllInCacheTest extends AbstractSystemTestUnit {

	@Override
	public void test() {
		List<IssuePojo> issuePojos = new ArrayList<IssuePojo>();

		for (int i = 0; i < 15; i++) {
			issuePojos.add(new IssuePojo(i + 1, "dank" + (i + 1)));
		}

		gigaSpace.writeMultiple(issuePojos.toArray(new IssuePojo[] {}));

		waitForEmptyReplicationBacklog(gigaSpace);

		IssuePojo[] pojos = gigaSpace.readMultiple(new IssuePojo(), 20);

		// Assert.assertArrayEquals(issuePojos.toArray(), pojos);

		AssertUtils.AssertEquals("", issuePojos, Arrays.asList(pojos));
	}

}
