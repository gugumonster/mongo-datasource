package com.gigaspaces.persistency;

import java.io.File;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitDeployment;
import org.openspaces.core.GigaSpace;

import com.gigaspaces.client.ClearModifiers;
import com.gigaspaces.client.CountModifiers;
import com.gigaspaces.persistency.utils.CommandLineProcess;
import com.gigaspaces.persistency.utils.IRepetitiveRunnable;
import com.j_spaces.core.IJSpace;
import com.j_spaces.core.admin.StatisticsAdmin;
import com.j_spaces.core.client.FinderException;
import com.j_spaces.core.client.SpaceFinder;
import com.j_spaces.core.filters.ReplicationStatistics.ChannelState;
import com.j_spaces.core.filters.ReplicationStatistics.OutgoingChannel;
import com.j_spaces.core.filters.ReplicationStatistics.OutgoingReplication;
import com.mongodb.MongoClient;

@SuppressWarnings("deprecation")
public abstract class AbstractSystemTestUnit {

	protected final static Admin admin = new AdminFactory().addGroup(
			getTestGroup()).createAdmin();;

	protected GigaSpace gigaSpace;
	protected ProcessingUnit pu;

	protected static CommandLineProcess gsAgent;
	protected static CommandLineProcess mongod;

	private static String gs_home = "c:/temp/gigaspaces-xap-premium-9.6.0-ga";
	private static String mongo_home = "c:/mongodb";
	private static String deploy_path = "c:/temp/mongodb-ds-deploy";

	static final String QA_DB = "qadb";
	static final String GS_AGENT = "/bin/gs-agent.bat";
	static final String MONGO_D = "/bin/mongod.exe";

	@BeforeClass
	public static void init() {
		startMongoDB();

		startGSAgent();

		admin.getGridServiceManagers().waitForAtLeastOne();

	}

	@Before
	public void start() {
		deploy();
	}

	private static void dropDB() {
		MongoClient client;

		try {
			client = new MongoClient();

			client.dropDatabase(QA_DB);

		} catch (UnknownHostException e) {
			throw new AssertionError(e);
		}

	}

	protected final static String getGSAPath() {
		return gs_home + GS_AGENT;
	}

	protected final static String getMongoDBPath() {
		return mongo_home + MONGO_D;
	}

	protected String getDeploymentJarPath() {
		return deploy_path + getPUJar();
	}

	protected boolean hasMirrorService() {
		return getMirrorService() != null;
	}

	protected String getMirrorService() {
		return null;
	}

	protected String getPUJar() {
		return "/mongodb-qa-space-0.0.1-SNAPSHOT.jar";
	}

	protected IJSpace findSpace(String instanceId) throws FinderException {
		return (IJSpace) SpaceFinder
				.find(String
						.format("jini://*/*/qa-space?cluster_schema=partitioned-sync2backup&groups=%s&total_members=2,1&id=%s",
								getTestGroup(), instanceId));
	}

	protected static void startGSAgent() {
		gsAgent = new CommandLineProcess(getGSAPath());

		gsAgent.addEnvironmentVariable("LOOKUPGROU" + "PS", getTestGroup());

		new Thread(gsAgent).start();
	}

	protected static void startMongoDB() {

		mongod = new CommandLineProcess(getMongoDBPath());

		Thread t = new Thread(mongod);

		t.start();
	}

	protected static void stopMongoDB() {
		mongod.stop();
	}

	@Test
	public abstract void test();

	protected void deploy() {

		if (hasMirrorService())
			deployMirrorService();

		File puArchive = new File(getDeploymentJarPath());

		ProcessingUnitDeployment deployment = new ProcessingUnitDeployment(
				puArchive);

		try {

			pu = admin.getGridServiceManagers().deploy(deployment);

			pu.waitFor(4);

			gigaSpace = pu.getSpace().getGigaSpace();
		} catch (Exception ex) {
			throw new AssertionError(ex);
		}
	}

	private void deployMirrorService() {
		File mirrorPuArchive = new File(deploy_path + getMirrorService());

		ProcessingUnitDeployment deployment = new ProcessingUnitDeployment(
				mirrorPuArchive);

		try {

			pu = admin.getGridServiceManagers().deploy(deployment);

			pu.waitFor(1);

			// gigaSpace = pu.getSpace().getGigaSpace();
		} catch (Exception ex) {
			throw new AssertionError(ex);
		}

	}

	public static String getTestGroup() {
		return "qa_group";
	}

	@After
	public void stop() {

		// pu.undeployAndWait();

	}

	@AfterClass
	public static void destroy() {

		// for (GridServiceAgent gsa : admin.getGridServiceAgents()) {
		// gsa.shutdown();
		// }
		//
		// gsAgent.stop();

		// dropDB();

		// mongod.stop();
	}

	protected void say(String string) {
		// TODO Auto-generated method stub
		
	}
	protected void waitForActiveReplicationChannelWithMirror(final IJSpace space)
			throws Exception {
		repeat(new IRepetitiveRunnable() {
			public void run() throws Exception {
				boolean channelFound = false;

				for (OutgoingChannel channel : getOutgoingReplication(space)
						.getChannels()) {
					if (!channel.getTargetMemberName().contains(
							"mirror-service")) {
						continue;
					}

					Assert.assertEquals("No replication with mirror",
							ChannelState.ACTIVE, channel.getChannelState());
					channelFound = true;
				}

				if (!channelFound) {
					Assert.fail("no replication channel with mirror");
				}
			}
		}, 60 * 1000);

	}

	protected OutgoingReplication getOutgoingReplication(IJSpace space)
			throws Exception {
		return ((StatisticsAdmin) space.getAdmin()).getHolder()
				.getReplicationStatistics().getOutgoingReplication();
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

	protected void waitForEmptyReplicationBacklog(IJSpace space) {
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
			} catch (Throwable e) {
				try {
					Thread.sleep(repeateInterval);
				} catch (InterruptedException e1) {
				}
			}
		}
	}
}
