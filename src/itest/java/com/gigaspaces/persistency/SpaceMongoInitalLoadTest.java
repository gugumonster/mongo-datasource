package com.gigaspaces.persistency;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.space.UrlSpaceConfigurer;

import com.gigaspaces.client.ReadModifiers;
import com.gigaspaces.client.WriteModifiers;
import com.gigaspaces.framework.ThreadBarrier;
import com.gigaspaces.itest.model.Issue;
import com.gigaspaces.itest.model.MongoIssuePojo;
import com.gigaspaces.itest.model.Priority;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class SpaceMongoInitalLoadTest extends AbstractSystemTestUnit {
	@SuppressWarnings("unchecked")
	private final BiMap<Priority, Integer> priorityMap = initPriortyMap(new HashBiMap<Priority, Integer>());

	private GigaSpace gigaSpace;
	private volatile boolean work = true;
	private static final int NUMBER_OF_WRITERS = 1;
	private volatile ThreadBarrier barrier = new ThreadBarrier(
			NUMBER_OF_WRITERS + 1);
	private final AtomicInteger idGenerator = new AtomicInteger(0);
	private final ConcurrentMap<Integer, Integer> writes = new ConcurrentHashMap<Integer, Integer>();

	UrlSpaceConfigurer space1Config;
	UrlSpaceConfigurer space1_1Config;
	UrlSpaceConfigurer space2Config;
	UrlSpaceConfigurer space2_1Config;

	@Override
	public void test() {
		// try {
		// helper = new CassandraHelper(new File(getTestUnit().getConfig()
		// .getTestDirPath()));
		// helper.init();
		// startMirror(2, 1);
		//
		// initConfigurersAndStartSpaces();
		// fillClusterData();
		// teardownCluster();
		// initConfigurersAndStartSpaces();
		//
		// assertValidInitialDataLoad();
		// } finally {
		// helper.stop();
		// }

	}

	private void assertValidInitialDataLoad() {
		MongoIssuePojo[] issues = gigaSpace.readMultiple(new MongoIssuePojo(),
				Integer.MAX_VALUE, ReadModifiers.MEMORY_ONLY_SEARCH);
		Assert.assertEquals("initial data load count", writes.size(),
				issues.length);
		for (MongoIssuePojo issue : issues) {
			checkVaildIssue(issue);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static HashBiMap initPriortyMap(HashBiMap priorityMap) {
		priorityMap.put(Priority.BLOCKER, 0);
		priorityMap.put(Priority.CRITICAL, 1);
		priorityMap.put(Priority.MAJOR, 2);
		priorityMap.put(Priority.MINOR, 3);
		priorityMap.put(Priority.TRIVIAL, 4);
		priorityMap.put(Priority.MEDIUM, 5);
		return priorityMap;
	}

	private void initConfigurersAndStartSpaces() throws Exception {
		// space1Config = getSpaceConfigurer(1, null, 2, 1);
		// space1_1Config = getSpaceConfigurer(1, 1, 2, 1);
		// space2Config = getSpaceConfigurer(2, null, 2, 1);
		// space2_1Config = getSpaceConfigurer(2, 1, 2, 1);
		//
		// space1Config.create();
		// space1_1Config.create();
		// space2Config.create();
		// space2_1Config.create();
		//
		// this.gigaSpace = new
		// GigaSpaceConfigurer(space1Config.space()).clustered(true).create();
		//
		// waitForActiveReplicationChannelWithMirror(space1Config.space());
		// waitForActiveReplicationChannelWithMirror(space2Config.space());
	}

	private void fillClusterData() throws Exception {
		// say("starting workers");
		// startWorkers();
		//
		// say("sleep 30 sec");
		// Thread.sleep(10 * 1000);
		//
		// say("stopping, written so far: " + writes.size());
		// barrier.inspect();
		// say("stopping workers");
		// work = false;
		// barrier.await();
		//
		// waitForEmptyReplicationBacklog(space1Config.space());
		// waitForEmptyReplicationBacklog(space2Config.space());
		//
		// say("total written: " + writes.size());
	}

	private void teardownCluster() throws Exception {
		// space2_1Config.destroy();
		// space1_1Config.destroy();
		// space1Config.destroy();
		// space2Config.destroy();
	}

	private void startWorkers() {
		Thread[] writers = new Thread[NUMBER_OF_WRITERS];
		for (int i = 0; i < writers.length; i++) {
			writers[i] = new IssueWriter();
			writers[i].start();
		}
	}

	private MongoIssuePojo createIssue() {
		int id = idGenerator.getAndIncrement();
		MongoIssuePojo issue = new MongoIssuePojo(id, "" + id);
		issue.setPriority(priorityMap.inverse().get(id % priorityMap.size()));
		issue.setVotes(id % priorityMap.size());
		writes.put(id, id);
		return issue;
	}

	private void checkVaildIssue(Issue issue) {
		Integer key = issue.getKey();
		Assert.assertNotNull("No key", key);
		Assert.assertEquals("bad issue", key % priorityMap.size(),
				(int) issue.getVotes());
		Assert.assertEquals("bad issue",
				String.valueOf((int) issue.getVotes()), issue.getVotesRep());
		Assert.assertEquals("bad issue", "" + key, issue.getReporter()
				.getUsername());
	}

	// @Override
	// protected CachePolicy createCachePolicy() {
	// return new AllInCachePolicy();
	// }

	private class IssueWriter extends Thread {
		public void run() {
			try {
				while (work) {
					barrier.inspect();
					MongoIssuePojo[] toWrite = new MongoIssuePojo[100];
					for (int i = 0; i < toWrite.length; i++) {
						toWrite[i] = createIssue();
					}
					gigaSpace.writeMultiple(toWrite,
							WriteModifiers.MEMORY_ONLY_SEARCH);
				}
				barrier.await();
			} catch (Exception e) {
				barrier.reset(e);
			}
		}
	}

}
