Overview
================

Implementation for XAP mongodb persistency SpaceDataSource and SpaceSynchronizationEndpoint

Prerequisites
=============

* XAP
  * [Download](http://www.gigaspaces.com/xap-download) (9.6 or later) and follow the [installation instructions](http://wiki.gigaspaces.com/wiki/display/XAP97/Installation)
  * Create environment variable `GS_HOME` -> path/to/[XAP installation directory]
  * Add `GS_HOME\bin` to `PATH` environment variable
* MongoDB
  * [Download](http://www.mongodb.org/downloads) and follow the [installation instructions](http://docs.mongodb.org/manual/installation/)
  * Create environment variable `MONGO_HOME` -> path/to/[mongo installtion directory]
  * Add `MONGO_HOME\bin` to `PATH` environment variable
* Maven
  * [Download](http://maven.apache.org/download.cgi) (3.0.5 or later) and follow the [installation instructions](http://maven.apache.org/download.cgi#Installation) at the end of document
  * Add `M2_HOME\bin` to `PATH` environment variable

Build
=====

* Clone the project git clone
* Navigate to mongo-datasource project directory
* Build project 
  * without tests: `mvn clean install`
  * with tests: `mvn clean surefire:test install`
* run integration and system tests `mvn integration-test` or `mvn verify` 

> ##### Notes #####
> * its recommended that you run `mvn clean install` and then running the testing phases separately `mvn surefire:test integration-test` 
  because integration and system test can take long time
> * eclipse users uses m2e plugin sometimes miss synchronization its recommended to right click on 
  mongodb-datasource project and from the menu [Maven]-> [Update project]


Repositories
============
	<repositories>
		<repository>
			<id>org.openspaces</id>
			<name>OpenSpaces</name>
			<url>http://maven-repository.openspaces.org</url>
		</repository>

		<repository>
			<releases>
				<enabled>true</enabled>
				<updatePolicy>always</updatePolicy>
				<checksumPolicy>warn</checksumPolicy>
			</releases>
			<id>allanbank</id>
			<name>Allanbank Releases</name>
			<url>http://www.allanbank.com/repo/</url>
			<layout>default</layout>
		</repository>
	</repositories>

Dependencies
============
    	<dependency>
			<groupId>com.gigaspaces</groupId>
			<artifactId>gs-openspaces</artifactId>
			<version>9.7.0-SNAPSHOT</version>
		</dependency>

		<!-- mongodb java driver -->
		<dependency>
			<groupId>org.mongodb</groupId>
			<artifactId>mongo-java-driver</artifactId>
			<version>2.11.2</version>
		</dependency>
		<dependency>
			<groupId>com.allanbank</groupId>
			<artifactId>mongodb-async-driver</artifactId>
			<version>1.2.3</version>
		</dependency>

		<dependency>
			<groupId>org.antlr</groupId>
			<artifactId>antlr4-runtime</artifactId>
			<version>4.0</version>
		</dependency>

