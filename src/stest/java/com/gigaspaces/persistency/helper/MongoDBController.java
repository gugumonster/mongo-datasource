package com.gigaspaces.persistency.helper;

import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.distribution.Version;

public class MongoDBController {

	private static final String LOCALHOST = "localhost";
	private static final String QA_DB = "qadb";
	private static final int PORT = 27017;
	private MongodProcess mongodProcess;

	private MongoClient client;

	public void start() {
		try {

			MongodStarter starter = MongodStarter.getDefaultInstance();

			MongodExecutable mognoExecutable = starter
					.prepare(new MongodConfig(Version.Main.PRODUCTION, PORT,
							false));

			mongodProcess = mognoExecutable.start();

			client = new MongoClient(LOCALHOST, PORT);
			
			client.dropDatabase(QA_DB);

			client.getDB(QA_DB);
			
		} catch (Throwable e) {
			throw new AssertionError(e);
		}
	}

	public void stop() {
		mongodProcess.stop();
	}
}
