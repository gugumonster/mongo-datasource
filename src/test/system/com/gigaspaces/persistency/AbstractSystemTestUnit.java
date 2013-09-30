package com.gigaspaces.persistency;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitDeployment;
import org.openspaces.core.GigaSpace;

import com.gigaspaces.client.ClearModifiers;
import com.gigaspaces.client.CountModifiers;
import com.j_spaces.core.IJSpace;
import com.j_spaces.core.admin.StatisticsAdmin;

public abstract class AbstractSystemTestUnit {

	protected final Admin admin;

	protected GigaSpace gigaSpace;
	protected ProcessingUnit pu;

	AbstractSystemTestUnit() {
		admin = new AdminFactory().addGroup(getTestGroup()).createAdmin();
	}

	@Before
	public void start() {
		admin.getGridServiceManagers().waitForAtLeastOne();

		deployQASpace();

	}

	private static Map<String, String> init() {

		Map<String, String> env = new HashMap<String, String>();

		env.put("LOOKUPGROUPS", getTestGroup());

		return env;
	}

	@Test
	public abstract void test();

	private void deployQASpace() {
		File puArchive = new File("C:/Temp/qa-space.jar");

		ProcessingUnitDeployment deployment = new ProcessingUnitDeployment(
				puArchive);

		try {

			pu = admin.getGridServiceManagers().deploy(deployment);

			pu.waitFor(4);

			gigaSpace = pu.getSpace().getGigaSpace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static String getTestGroup() {
		return "qa_group";
	}

	@After
	public void stop() {
		pu.undeployAndWait();

	}

	protected void waitForEmptyReplicationBacklogAndClearMemory(
			GigaSpace gigaSpace) {
		waitForEmptyReplicationBacklog(gigaSpace);
		clearMemory(gigaSpace);
	}

	protected void clearMemory(final GigaSpace gigaSpace) {

		repeat(new IRepetitiveRunnable() {

			public void run() throws Exception {
				gigaSpace.clear(null, ClearModifiers.EVICT_ONLY);

				Assert.assertEquals("gigaSpace memory did not clear", 0,
						gigaSpace
								.count(null, CountModifiers.MEMORY_ONLY_SEARCH));
			}
		}, 10 * 1000);

	}

	protected void waitForEmptyReplicationBacklog(final GigaSpace gigaSpace) {
		waitForEmptyReplicationBacklog(gigaSpace.getSpace());
	}

	private void waitForEmptyReplicationBacklog(IJSpace space) {
		repeat(new IRepetitiveRunnable() {

			public void run() throws Exception {
				long l = -1;

				l = ((StatisticsAdmin) gigaSpace.getSpace().getAdmin())
						.getHolder().getReplicationStatistics()
						.getOutgoingReplication().getRedoLogSize();

				Assert.assertEquals("backlog not empty", 0, l);

			}
		}, 10 * 1000);

	}

	protected void repeat(IRepetitiveRunnable iRepetitiveRunnable,
			long repeateInterval) {
		while (true) {
			try {

				iRepetitiveRunnable.run();

				break;
			} catch (Exception e) {
				try {
					Thread.sleep(repeateInterval);
				} catch (InterruptedException e1) {
				}
			}
		}
	}
}
