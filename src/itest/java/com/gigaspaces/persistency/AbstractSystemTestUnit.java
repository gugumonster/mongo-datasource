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
import org.openspaces.admin.gsa.GridServiceAgent;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitDeployment;
import org.openspaces.core.GigaSpace;

import com.gigaspaces.client.ClearModifiers;
import com.gigaspaces.client.CountModifiers;
import com.gigaspaces.persistency.utils.CommandLineProcess;
import com.gigaspaces.persistency.utils.IRepetitiveRunnable;
import com.j_spaces.core.IJSpace;
import com.j_spaces.core.admin.StatisticsAdmin;

public abstract class AbstractSystemTestUnit {

	protected final Admin admin;

	protected GigaSpace gigaSpace;
	protected ProcessingUnit pu;

	protected CommandLineProcess gsAgent;
	protected CommandLineProcess mongod;

	AbstractSystemTestUnit() {
		admin = new AdminFactory().addGroup(getTestGroup()).createAdmin();
	}

	@Before
	public void start() {

		startMongoDB();

		startGSAgent();

		admin.getGridServiceManagers().waitForAtLeastOne();

		deployQASpace();

	}

	protected void startGSAgent() {
		Map<String, String> env = new HashMap<String, String>();

		env.put("LOOKUPGROUPS", getTestGroup());

		gsAgent = new CommandLineProcess(
				"C:/Temp/gigaspaces-xap-premium-9.6.0-ga/bin/gs-agent.bat", env);

		new Thread(gsAgent).start();
	}

	protected void startMongoDB() {
		mongod = new CommandLineProcess("C:/mongodb/bin/mongod.exe", null);

		new Thread(mongod).start();
	}

	@Test
	public abstract void test();

	private void deployQASpace() {
		File puArchive = new File("C:/Temp/mongodb-qa-space-0.0.1-SNAPSHOT.jar");

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

		for (GridServiceAgent gsa : admin.getGridServiceAgents()) {
			gsa.shutdown();
		}

		CommandLineProcess stopMongo = new CommandLineProcess(
				"taskkill /F /IM mongod.exe", null);

		// stopMongo.run();
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
