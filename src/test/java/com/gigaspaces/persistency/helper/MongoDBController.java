package com.gigaspaces.persistency.helper;

import java.net.UnknownHostException;

import org.apache.commons.io.FilenameUtils;

import com.gigaspaces.persistency.utils.CommandLineProcess;
import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.distribution.Version;

public class MongoDBController {

	private static final String MONGO_HOME = "MONGO_HOME";
	private static final String LOCALHOST = "localhost";
	private static final String QA_DB = "qadb";
	private static final int PORT = 27017;
	private static final int MONGOD = 0;
	private MongodProcess mongodProcess;
	private CommandLineProcess MONGO_PROCESS;

	private MongoClient client;
	private boolean isEmbedded;
	private Thread thread;

	public void start(boolean isEmbedded) {

		this.isEmbedded = isEmbedded;

		if (this.isEmbedded) {

			startEmbedded();
		} else {
			startServer();
		}

		try {
			client = new MongoClient(LOCALHOST, PORT);
		} catch (UnknownHostException e) {
			throw new AssertionError(e);
		}

		client.dropDatabase(QA_DB);

		client.getDB(QA_DB);

	}

	public void startServer() {

		String path = FilenameUtils.normalize(System.getenv(MONGO_HOME)
				+ "/bin/mongod.exe");

		MONGO_PROCESS = new CommandLineProcess(path);

		thread = new Thread(MONGO_PROCESS);

		thread.start();

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}

	}

	public void startEmbedded() throws AssertionError {
		try {

			MongodStarter starter = MongodStarter.getDefaultInstance();

			MongodExecutable mognoExecutable = starter
					.prepare(new MongodConfig(Version.Main.PRODUCTION, PORT,
							false));

			mongodProcess = mognoExecutable.start();

		} catch (Throwable e) {
			throw new AssertionError(e);
		}
	}

	public void drop() {
		client.dropDatabase(QA_DB);
	}

	public void stop() {
		if (isEmbedded)
			mongodProcess.stop();
		else
			MONGO_PROCESS.stop();

	}
}
