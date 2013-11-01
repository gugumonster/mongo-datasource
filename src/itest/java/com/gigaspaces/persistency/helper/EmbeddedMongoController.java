package com.gigaspaces.persistency.helper;

import de.flapdoodle.embed.mongo.MongoShellProcess;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongosConfigBuilder;
//import de.flapdoodle.embed.mongo.distribution.Version;

public class EmbeddedMongoController {

	private MongodExecutable _mongodExe;
    private MongoShellProcess _mongod;

	private int _port = 27017;

	public void initMongo(boolean isEmbedded) {

		
	//	MongodStarter runtime = MongodStarter.getDefaultInstance();
		
		
		   //IMongodConfig mongodConfig = new MongodConfigBuilder();
	//	   .version(Version.Main.PRODUCTION)
	//	   .net(new Net(12345, Network.localhostIsIPv6())).build();

          // IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults(Command.MongoD).build();
		
		// _mongodExe = runtime.prepare(new MongosConfigBuilder().build();
         //.version(Version.Main.PRODUCTION)
         //.net(new Net(12345, Network.localhostIsIPv6()))
         //.build());
		 
		 //_mongod = _mongodExe.start();
		// mongodExe = runtime.prepare(new MongodConfig(Version.V2_3_0, 12345,
		// Network.localhostIsIPv6()));
		// mongod = mongodExe.start();
		// mongo = new Mongo("localhost", 12345);
	}

	public void stopMongo() {
		// TODO Auto-generated method stub

	}

	public int getPort() {		
		return _port;
	}

	public void createDb(String dbName) {
		// TODO Auto-generated method stub
		
	}

	public void dropDb(String dbName) {
		// TODO Auto-generated method stub
		
	}

}
